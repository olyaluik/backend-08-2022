package ee.olga.webshop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;
import javax.validation.constraints.Null;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name="seq_product", initialValue = 1, allocationSize = 1)
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_product")
    private Long id;
    private String name;
    private double price;
    private String image;
    private boolean active; //isActive ei saa
    @ColumnDefault("0")
    private int stock;

    @ManyToOne
    private Category category; //Liha- ja kalatooted


}
