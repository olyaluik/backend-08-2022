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
@Entity
public class Product {
    @Id
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
