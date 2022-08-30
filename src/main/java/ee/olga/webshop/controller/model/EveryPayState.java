package ee.olga.webshop.controller.model;

import lombok.Data;

import java.util.Date;

@Data
    public class EveryPayState{
        public String account_name;
        public String order_reference;
        public Object email;
        public String customer_ip;
        public String customer_url;
        public Date payment_created_at;
        public double initial_amount;
        public double standing_amount;
        public String payment_reference;
        public String payment_link;
        public String api_username;
        public Object warnings;
        public int stan;
        public int fraud_score;
        public String payment_state;
        public String payment_method;
        public ObDetails ob_details;
        public Date transaction_time;
    }
@Data
class ObDetails{
    public String debtor_iban;
    public String creditor_iban;
    public String ob_payment_reference;
    public String ob_payment_state;
}