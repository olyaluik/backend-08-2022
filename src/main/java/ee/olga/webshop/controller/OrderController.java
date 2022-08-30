package ee.olga.webshop.controller;

import ee.olga.webshop.controller.model.EveryPayState;
import ee.olga.webshop.entity.Order;
import ee.olga.webshop.entity.Person;
import ee.olga.webshop.entity.Product;
import ee.olga.webshop.repository.OrderRepository;
import ee.olga.webshop.repository.PersonRepository;
import ee.olga.webshop.repository.ProductRepository;
import ee.olga.webshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderService orderService;

    @GetMapping("orders/{personCode}")
    public List<Order> getPersonOrders(@PathVariable String personCode) {
        Person person = personRepository.findById(personCode).get();
        return orderRepository.findAllByPerson(person);
    }

    @PostMapping("orders/{personCode}")
    public String addNewOrder(@PathVariable String personCode, @RequestBody List<Product> products) {


        List<Product> originalProducts = orderService.findOriginalProducts(products);

        double totalSum = orderService.calculateTotalSum(originalProducts);

        Person person = personRepository.findById(personCode).get();
        Order order = orderService.saveOrder(person, originalProducts, totalSum);



        return orderService.getLinkFromEverypay(order);
    }

    @GetMapping("payment-completed")
        public String checkIfPaid(
   //             @PathParam("order_reference") String order_reference,
                @PathParam ("payment_reference") String payment_reference) {

        //ÕNNESTUNUD order_reference=gj676746&payment_reference=98f67d54a747bd9fbbb019c39e0c3b8e6e535b006a997640b99c24d399e7feb9

        //EBAÕNNESTUNUD

        //teha päring Everypaysse nagu tegime makse
        //saadan kaasa paymentReference
        //ENDPOINT: payments/:payment_reference
        //response.getBody() <-- tema saab vastu, kas on makse:
        //Settled, Voided, Failed, Cancelled
        String url = "https://igw-demo.every-pay.com/api/v4/payments/" + payment_reference + "?api_username=92ddcfab96e34a5f";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic OTJkZGNmYWI5NmUzNGE1Zjo4Y2QxOWU5OWU5YzJjMjA4ZWU1NjNhYmY3ZDBlNGRhZA==");
        HttpEntity httpEntity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<EveryPayState> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, EveryPayState.class);
        
        if (response.getBody()!=null) {
            String order_reference = response.getBody().order_reference;
            Order order = orderRepository.findById(Long.parseLong(order_reference)).get();
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
        } else {
            return "Ühenduse viga!";
        }


 //       return "Makse õnnestus"
    }

}
