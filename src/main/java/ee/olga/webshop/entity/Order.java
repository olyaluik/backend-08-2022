package ee.olga.webshop.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@SequenceGenerator(name="seq", initialValue = 786, allocationSize = 1)
@Table(name = "orders") //vahetame tabeli nimi
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long id;

    private Date creationDate;

    private double totalSum;

    private String paidState;

    @ManyToMany
    private List<Product> products;

    @ManyToOne
    private Person person;
}
