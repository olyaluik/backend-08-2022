package ee.olga.webshop.controller;

import ee.olga.webshop.entity.Person;
import ee.olga.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @GetMapping("persons")
    public ResponseEntity<List<Person>> getPersons () {

        return new ResponseEntity<>(personRepository.findAll(), HttpStatus.OK);
    }

    @PatchMapping("change-to-admin/{personCode}")
    public ResponseEntity<List<Person>> changePersonToAdmin(@PathVariable String personCode) {
        Person person = personRepository.findById(personCode).get();
        person.setRole("admin");
        personRepository.save(person);
        return new ResponseEntity<>(personRepository.findAll(), HttpStatus.OK);
    }

    @PatchMapping("change-to-user/{personCode}")
    public ResponseEntity<List<Person>> changePersonToUser(@PathVariable String personCode) {
        Person person = personRepository.findById(personCode).get();
        person.setRole(null);
        personRepository.save(person);
        return new ResponseEntity<>(personRepository.findAll(), HttpStatus.OK);
    }

//    @PostMapping("persons")
//    public List<Person> addPerson(@RequestBody Person person) {
//        personRepository.save(person);
//        return personRepository.findAll();
//    }

}

