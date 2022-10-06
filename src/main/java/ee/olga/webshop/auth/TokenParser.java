package ee.olga.webshop.auth;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
public class TokenParser extends BasicAuthenticationFilter {
    public TokenParser(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //getOrder() {
        //VÕTA GLOBAALNE SISSELOGITU --> SAAN TA KÄTTE JA SEEJÄREL SAAN TA TELLIMUSED KÄTTE
        //Kui päring lõppeb (tagastatakse front-endile), siis see inimene pole enam globaalselt sees

        System.out.println("KONTROLLIN TOKENIT");

        System.out.println(request.getMethod());

        System.out.println(request.getRequestURI());

        //Bearer dafsfag43534fs
        String headerToken = request.getHeader("Authorization");
        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            String token = headerToken.replace("Bearer ", "");

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey("absolutely-secret-key")
                        .parseClaimsJws(token)
                        .getBody();

                String personCode = claims.getSubject();

                log.info("Succesfully logged in {}", personCode);
                log.info("Checking if {}", claims.getAudience());

                List<GrantedAuthority> authorities = null;
                if (claims.getId() != null && claims.getId().equals("admin")) {
                    GrantedAuthority authority = new SimpleGrantedAuthority("admin");
                    authorities = Collections.singletonList(authority);
                }
                System.out.println("Auth: " + authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(personCode, null, authorities);

                //turva globaalne hoidja
                // SecurityContextHolder.getContext().getAuthentication().getPrincipal() --> isikukood;
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                log.error("LOGGED IN WITH EXPIRED TOKEN {}", token);
            } catch (UnsupportedJwtException e) {
                log.error("LOGGED IN WITH UNSUPPORTED JWT TOKEN {}", token);
            } catch (MalformedJwtException e) {
                log.error("LOGGED IN WITH MALFORMED TOKEN {}", token);
            } catch (SignatureException e) {
                log.error("LOGGED IN WITH FALSE SIGNATURE TOKEN {}", token);
            } catch (IllegalArgumentException e) {
                log.error("LOGGED IN WITH ILLEGAL ARGUMENT TOKEN {}", token);
            }
        }

        super.doFilterInternal(request, response, chain);
    }
}
