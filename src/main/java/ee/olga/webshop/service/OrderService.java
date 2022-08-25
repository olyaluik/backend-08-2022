package ee.olga.webshop.service;

import ee.olga.webshop.cache.ProductCache;
import ee.olga.webshop.controller.model.EveryPayData;
import ee.olga.webshop.controller.model.EveryPayResponse;
import ee.olga.webshop.entity.Order;
import ee.olga.webshop.entity.Person;
import ee.olga.webshop.entity.Product;
import ee.olga.webshop.repository.OrderRepository;
import ee.olga.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductCache productCache;

    private String apiUserName = "92ddcfab96e34a5f";
    private String accountName = "EUR3D1";
    private String customerUrl = "https://olya-webshop.herokuapp.com/payment-completed";

    public List<Product> findOriginalProducts(List<Product> products) {

        return products.stream()
                .map(e -> {
                    try {
                        return productCache.getProduct(e.getId());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

    public double calculateTotalSum(List<Product> originalProducts) {
        return originalProducts.stream()
//                .filter(e -> e.isActive())
                .mapToDouble(e -> e.getPrice())
                .sum();
    }

    public Order saveOrder(Person person, List<Product> originalProducts, double totalSum) {
        Order order = new Order();
        order.setCreationDate(new Date());
        order.setPerson(person);
        //OTSI ID ALUSEL KÕIKIDELE TOODETELE ORIGINAAL
//        List<Product> originalProducts = new ArrayList<>();
//        for (Product product: products) {
//            Long id = product.getId();
//            Product originalProduct = productRepository.findById(id).get();
//            originalProducts.add(originalProduct);
//            //Robert C. Martin (Uncle Bob) - Clean Code
//
        order.setProducts(originalProducts);//otse päringust pannakse andmebaasi
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

    public String getLinkFromEverypay(Order order) {

        //@Autowired
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://igw-demo.every-pay.com/api/v4/payments/oneoff";

        System.out.println(new Date());
        System.out.println(LocalDateTime.now());

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
        headers.set("Authorization", "Basic OTJkZGNmYWI5NmUzNGE1Zjo4Y2QxOWU5OWU5YzJjMjA4ZWU1NjNhYmY3ZDBlNGRhZA==");

        HttpEntity entity = new HttpEntity(data, headers);

        ResponseEntity<EveryPayResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, EveryPayResponse.class);

        return response.getBody().payment_link;
    }
}
