package ee.olga.webshop.controller;

import ee.olga.webshop.entity.Person;
import ee.olga.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    @PostMapping("persons")
//    public List<Person> addPerson(@RequestBody Person person) {
//        personRepository.save(person);
//        return personRepository.findAll();
//    }




}

