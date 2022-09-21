package ee.olga.webshop.controller.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class EveryPayResponse {
    public String account_name;
    public String order_reference;
    public String email;
    public Object customer_ip;
    public String customer_url;
    public Date payment_created_at;
    public double initial_amount;
    public double standing_amount;
    public String payment_reference;
    public String payment_link;
    public ArrayList<PaymentMethod> payment_methods;
    public String api_username;
    public Object warnings;
    public Object stan;
    public Object fraud_score;
    public String payment_state;
    public Object payment_method;
}
@Data
class PaymentMethod{
    public String source;
    public String display_name;
    public String country_code;
    public String payment_link;
    public String logo_url;
    public Object applepay_available;
    public Object applepay_merchant_display_name;
}

