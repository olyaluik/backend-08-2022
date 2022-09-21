package ee.olga.webshop.configuration;

import ee.olga.webshop.auth.TokenParser;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        System.out.println("TOIMUS PÄRING");
//
//        super.configure(http);

        http
                .cors().and().headers().xssProtection().disable().and()
                .csrf().disable()
                .addFilter(new TokenParser(authenticationManager()))
                .authorizeRequests()
                .antMatchers("/active-products").permitAll()
                .antMatchers("/get-product/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/signup").permitAll()
                .anyRequest().authenticated();

    }
}
