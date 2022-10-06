package ee.olga.webshop.service;

import ee.olga.webshop.cache.ProductCache;
import ee.olga.webshop.controller.model.CartProduct;
import ee.olga.webshop.controller.model.EveryPayData;
import ee.olga.webshop.controller.model.EveryPayResponse;
import ee.olga.webshop.controller.model.EveryPayState;
import ee.olga.webshop.entity.Order;
import ee.olga.webshop.entity.Person;
import ee.olga.webshop.entity.Product;
import ee.olga.webshop.repository.CartProductRepository;
import ee.olga.webshop.repository.OrderRepository;
import ee.olga.webshop.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class OrderService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartProductRepository cartProductRepository;

    @Autowired
    ProductCache productCache;
    @Autowired
    RestTemplate restTemplate;

    @Value("${everypay.username}")
    private String apiUserName;
    @Value("${everypay.account}")
    private String accountName;
    @Value("${everypay.customerurl}")
    private String customerUrl;

    @Value("${everypay.headers}")
    private String everyPayHeaders;

    @Value("${everypay.url}")
    private String everyPayUrl;

    public List<Product> findOriginalProducts(List<Long> products) {
        log.info("Fetching original products");
        return products.stream()
                .map(e -> {
                    try {
                        return productCache.getProduct(e);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

    public double calculateTotalSum(List<CartProduct> cartProducts) {
        String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Calculating total sum {}", personCode);
        return cartProducts.stream()
//                .filter(e -> e.isActive())
                .mapToDouble(e -> e.getProduct().getPrice() * e.getQuantity())
                .sum();
    }

    @Transactional
    public Order saveOrder(Person person, List<CartProduct> cartProducts, double totalSum) {

        String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Starting to save order{}", personCode);

        cartProductRepository.saveAll(cartProducts);

        Order order = new Order();
        order.setCreationDate(new Date());
        order.setPerson(person);
        order.setPaidState("initial");
        //OTSI ID ALUSEL KÕIKIDELE TOODETELE ORIGINAAL
//        List<Product> originalProducts = new ArrayList<>();
//        for (Product product: products) {
//            Long id = product.getId();
//            Product originalProduct = productRepository.findById(id).get();
//            originalProducts.add(originalProduct);
//            //Robert C. Martin (Uncle Bob) - Clean Code
//
        order.setLineItem(cartProducts);//otse päringust pannakse andmebaasi
        /*double totalSum = 0.0;
        for(Product product : products) {
            totalSum += product.getPrice();
        }*/
        //element (e) product (p)
        //double totalSum = products.stream().mapToDouble(Product::getPrice).sum();
        //double totalSum = products.stream().filter(Product::isActive).mapToDouble(Product::getPrice).sum(); //otse päringust arvutatakse kogusumma
        order.setTotalSum(totalSum);
        return orderRepository.save(order);
    }

    public EveryPayResponse getLinkFromEveryPay(Order order) {

        String url = "https://igw-demo.every-pay.com/api/v4/payments/oneoff";

        //HttpEntity <-- kogub kokku päringuga seotud sisu body: vasakul ja headers: paremal
        EveryPayData data = new EveryPayData();
        data.setAccount_name(accountName);
        data.setApi_username(apiUserName);
        data.setAmount(order.getTotalSum());
        data.setOrder_reference(order.getId().toString());
        data.setNonce(order.getId().toString() + new Date() + Math.random());
        data.setTimestamp(ZonedDateTime.now().toString());
        data.setCustomer_url(customerUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", everyPayHeaders);

        HttpEntity<EveryPayData> entity = new HttpEntity<>(data, headers);

        ResponseEntity<EveryPayResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, EveryPayResponse.class);

        return response.getBody();
    }

    public String checkIfOrderIsPaid(String payment_reference) {
        String url = everyPayUrl + "/payments/" + payment_reference + "?api_username=" + apiUserName;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", everyPayHeaders);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<EveryPayState> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, EveryPayState.class);

        if (response.getBody()!=null) {
            String order_reference = response.getBody().order_reference;
            Order order = orderRepository.findById(Long.parseLong(order_reference)).get();
            return getPaymentState(response, order_reference, order);
        } else {
            return "Ühenduse viga!";
        }
    }

    private String getPaymentState(ResponseEntity<EveryPayState> response, String order_reference, Order order) {
        switch (response.getBody().payment_state) {
            case "settled":
                order.setPaidState("settled");
                orderRepository.save(order);
                return "Makse õnnestus: " + order_reference;
            case "failed":
                order.setPaidState("failed");
                orderRepository.save(order);
                return "Makse ebaõnnestunud: " + order_reference;
            case "cancelled":
                order.setPaidState("cancelled");
                orderRepository.save(order);
                return "Makse katkestati: " + order_reference;
            default:
                return "Makse ei toiminud: " + order_reference;
        }
    }
}
