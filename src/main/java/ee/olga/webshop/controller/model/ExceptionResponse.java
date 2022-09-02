package ee.olga.webshop.controller.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class ExceptionResponse {
    private Date date;
    private String message;
    private int statusCode;
    private HttpStatus error;
}
