package ee.olga.webshop.controller;

import ee.olga.webshop.auth.TokenGenerator;
import ee.olga.webshop.controller.exceptions.WrongPasswordException;
import ee.olga.webshop.controller.exceptions.WrongPasswordException;
import ee.olga.webshop.controller.model.LoginData;
import ee.olga.webshop.controller.model.TokenResponse;
import ee.olga.webshop.entity.Person;
import ee.olga.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthController {
    @Autowired
    PersonRepository personRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    TokenGenerator tokenGenerator;

    @PostMapping("signup")
    public ResponseEntity<String> signup(@RequestBody Person person) {
        if (!personRepository.existsById(person.getPersonCode())) {
            if (personRepository.findPersonByEmail(person.getEmail()) == null) {
                String password = person.getPassword();
 //               BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                person.setPassword(passwordEncoder.encode(password));
                personRepository.save(person);
            } else {
                return new ResponseEntity<>("Sellise emailiga kasutaja on juba olemas", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Sellise isikukoodiga kasutaja on juba olemas", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Edukalt registreeritud", HttpStatus.CREATED);
    }

    //sisselogimine hiljem
    @PostMapping("login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginData loginData) throws WrongPasswordException {
        String email = loginData.getEmail();
        Person person = personRepository.findPersonByEmail(email);
        if (passwordEncoder.matches(loginData.getPassword(), person.getPassword())) {
            //genereeri uus token ja tagasta see kasutajale
           // String token = "addaassda";
            TokenResponse tokenResponse = tokenGenerator.generateNewToken(person.getPersonCode());
            //tokenResponse.setToken(token);
            return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        } else {
            throw new WrongPasswordException();
        }
    }

}
