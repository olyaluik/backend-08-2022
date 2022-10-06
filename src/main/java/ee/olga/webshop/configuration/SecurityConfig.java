package ee.olga.webshop.configuration;

import ee.olga.webshop.auth.TokenParser;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

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
                .antMatchers( "/parcel-machines/**").permitAll()
                .antMatchers(HttpMethod.GET, "/category").permitAll()
                .antMatchers( "/cart-products/**").permitAll()
                .antMatchers( "/orders").permitAll()
                .antMatchers(HttpMethod.GET, "/products").hasAuthority("admin")
                .antMatchers( "/persons").hasAuthority("admin")
                .antMatchers(HttpMethod.DELETE, "/category").hasAuthority("admin")
                .antMatchers(HttpMethod.POST, "/category").hasAuthority("admin")
                .antMatchers( "/add-product").hasAuthority("admin")
                .antMatchers( "/edit-product/**").hasAuthority("admin")
                .antMatchers( "/delete-product/**").hasAuthority("admin")
                .antMatchers( "/add-stock").hasAuthority("admin")
                .antMatchers( "/decrease-stock").hasAuthority("admin")

                .antMatchers(HttpMethod.GET, "/orders-by-product/**").hasAuthority("admin")
                .antMatchers(HttpMethod.POST, "/change-to-admin/**").hasAuthority("admin")
                .antMatchers(HttpMethod.POST, "/change-to-user/**").hasAuthority("admin")


                .anyRequest().authenticated()
                .and().sessionManagement() //--> see + järgmine rida teeb seda, et iga päringuga kontrollitakse tokenit
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }
}
