package ee.olga.webshop.controller;

import ee.olga.webshop.controller.model.CartProduct;
import ee.olga.webshop.controller.model.EveryPayResponse;
import ee.olga.webshop.entity.Order;
import ee.olga.webshop.entity.Person;
import ee.olga.webshop.entity.Product;
import ee.olga.webshop.repository.CartProductRepository;
import ee.olga.webshop.repository.OrderRepository;
import ee.olga.webshop.repository.PersonRepository;
import ee.olga.webshop.repository.ProductRepository;
import ee.olga.webshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController

public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartProductRepository cartProductRepository;

    @GetMapping("orders")
    public ResponseEntity<List<Order>> getPersonOrders() {
        String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Person person = personRepository.findById(personCode).get();
        return new ResponseEntity<>(orderRepository.findAllByPerson(person), HttpStatus.OK);
    }

    @PostMapping("orders")              //[{productId:2, quantity: 8}]
    public ResponseEntity<EveryPayResponse> addNewOrder( @RequestBody List<CartProduct> cartProducts) {
        String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        //List {product: Product, quantity: int}
       // List<Product> originalProducts = orderService.findOriginalProducts(ids);

        double totalSum = orderService.calculateTotalSum(cartProducts);

        Person person = personRepository.findById(personCode).get();
        Order order = orderService.saveOrder(person, cartProducts, totalSum);

        return new ResponseEntity<>(orderService.getLinkFromEveryPay(order), HttpStatus.OK);
    }

    @GetMapping("payment-completed")
        public ResponseEntity<String> checkIfPaid(@PathParam ("payment_reference") String payment_reference) {

      return new ResponseEntity<>(orderService.checkIfOrderIsPaid(payment_reference), HttpStatus.OK);
    }

    @GetMapping("orders-by-product/{productID}")
    public List<Long> getOrdersByProduct(@PathVariable Long productID) {
        Product product = productRepository.findById(productID).get();

        List<Long> ids = cartProductRepository.findAllByProductOrderByIdAsc(product).stream()
                .map(CartProduct::getId)
                .collect(Collectors.toList());
        return ids;
    }

}
