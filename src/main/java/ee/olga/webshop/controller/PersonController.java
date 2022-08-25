package ee.olga.webshop.controller;

import ee.olga.webshop.entity.Person;
import ee.olga.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @GetMapping("persons")
    public List<Person> getPersons () {
        return personRepository.findAll();
    }

//    @PostMapping("persons")
//    public List<Person> addPerson(@RequestBody Person person) {
//        personRepository.save(person);
//        return personRepository.findAll();
//    }




}

