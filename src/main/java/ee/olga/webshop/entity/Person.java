package ee.olga.webshop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {
    @Id
    private String personCode;
    private String email;
    private String firstName;
    private String lastName;
    private String telephone;
    private String address;
    private String password;
    private String role;
}

