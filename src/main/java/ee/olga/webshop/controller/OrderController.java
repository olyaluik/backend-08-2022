package ee.olga.webshop.controller;

import ee.olga.webshop.entity.Order;
import ee.olga.webshop.entity.Person;
import ee.olga.webshop.entity.Product;
import ee.olga.webshop.repository.OrderRepository;
import ee.olga.webshop.repository.PersonRepository;
import ee.olga.webshop.repository.ProductRepository;
import ee.olga.webshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    OrderService orderService;

    @GetMapping("orders/{personCode}")
    public ResponseEntity<List<Order>> getPersonOrders(@PathVariable String personCode) {
        Person person = personRepository.findById(personCode).get();
        return new ResponseEntity<>(orderRepository.findAllByPerson(person), HttpStatus.OK);
    }

    @PostMapping("orders/{personCode}")
    public ResponseEntity<String> addNewOrder(@PathVariable String personCode, @RequestBody List<Product> products) {

        List<Product> originalProducts = orderService.findOriginalProducts(products);

        double totalSum = orderService.calculateTotalSum(originalProducts);

        Person person = personRepository.findById(personCode).get();
        Order order = orderService.saveOrder(person, originalProducts, totalSum);

        return new ResponseEntity<>(orderService.getLinkFromEveryPay(order), HttpStatus.OK);
    }

    @GetMapping("payment-completed")
        public ResponseEntity<String> checkIfPaid(@PathParam ("payment_reference") String payment_reference) {

      return new ResponseEntity<>(orderService.checkIfOrderIsPaid(payment_reference), HttpStatus.OK);
    }

}
