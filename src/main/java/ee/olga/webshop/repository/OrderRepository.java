package ee.olga.webshop.repository;

import ee.olga.webshop.entity.Order;
import ee.olga.webshop.entity.Person;
import ee.olga.webshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

//HIBERNATE
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByPerson(Person person);

    List<Order> findAllByProductsOrderByIdAsc(Product product);
}

//SELECT * FROM orders WHERE orders.person_person_code = person_code
