package ee.olga.webshop.controller.model;

import lombok.Data;

import java.util.Date;

@Data
public class EveryPayData {
    private String api_username;
    private String account_name;
    private double amount;
    private String order_reference;
    private String nonce;
    private String timestamp;
    private String customer_url;


}
