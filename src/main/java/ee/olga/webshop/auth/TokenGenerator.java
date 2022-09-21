package ee.olga.webshop.auth;

import ee.olga.webshop.controller.model.TokenResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {
    public TokenResponse generateNewToken(String personCode) {
        //Valime mille abil käib allkirjastamine
        //SHA512
        //Paneme secret parooli
        //Isikukood
        //Expiration
        String token = "addaassda" +personCode;
        //Teen JSONWEBTOKEN dependency abil uue tokeni
        //ja tgaastan

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(token);
        return tokenResponse;
    }
}
