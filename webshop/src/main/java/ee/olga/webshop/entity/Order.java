package ee.olga.webshop.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@SequenceGenerator(name="seq", initialValue = 100000, allocationSize = 1) //100001, 100002, 100003
@Table(name = "orders") //vahetame tabeli nimi
public class Order {
    @Id

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long id;

    private Date creationDate;

    private double totalSum;

    @ManyToMany
    private List<Product> products;

    @ManyToOne
    private Person person;
}
