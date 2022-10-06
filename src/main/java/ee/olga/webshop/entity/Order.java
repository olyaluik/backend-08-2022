package ee.olga.webshop.entity;

import ee.olga.webshop.controller.model.CartProduct;
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

    @OneToMany
    private List<CartProduct> lineItem;

    @ManyToOne
    private Person person;
}
