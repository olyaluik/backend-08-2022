package ee.olga.webshop.controller.model;

import lombok.Data;

@Data
public class SmartPostParcelMachine {
    public int place_id;
    public String name;
    public String city;
    public String address;
    public String opened;
    public int group_id;
    public String group_name;
    public int group_sort;
    public String description;
    public int active;
    public Object inactive_reason;
    public String created_date;
    public String updated_date;
}
