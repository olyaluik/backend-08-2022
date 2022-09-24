package ee.olga.webshop.auth;

import ee.olga.webshop.controller.model.TokenResponse;
import ee.olga.webshop.entity.Person;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenGenerator {
    public TokenResponse generateNewToken(Person person) {
        //Valime mille abil käib allkirjastamine
        //SHA512
        //Paneme secret parooli
        //Isikukood
        //Expiration
     //   String token = "addaassda" +personCode;

        /*    "email": "1234567@test.ee",
    "password":"testtest"*/
        //Jwts.builder
        //Subject, Expiration, Issuer, Id, IssuedAt, Audience, Header, NotBefore, Payload

        Date expirationDate = DateUtils.addHours(new Date(), 4);

//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", person.getRole());

        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "absolutely-secret-key")
                .setExpiration(expirationDate)
                .setIssuer("olga-webshop")
                .setSubject(person.getPersonCode())
                .setId(person.getRole())
//                .setClaims(claims)
                .compact();
        //Teen JSONWEBTOKEN dependency abil uue tokeni
        //ja tgaastan

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(token);
        return tokenResponse;
    }
}
