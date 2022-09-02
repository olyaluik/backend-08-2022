package ee.olga.webshop.controller;

import ee.olga.webshop.entity.Person;
import ee.olga.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthController {
    @Autowired
    PersonRepository personRepository;

    @PostMapping("signup")
    public ResponseEntity<String> signup(@RequestBody Person person) {
        if (!personRepository.existsById(person.getPersonCode())) {
            personRepository.save(person);
        }
        return new ResponseEntity<>("Edukalt registreeritud", HttpStatus.OK);
    }
}
