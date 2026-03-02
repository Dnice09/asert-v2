package tz.go.mnrt.asert.configs;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.util.Map;

@Component
public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> claims) {
        System.out.println("*********************called******************");
        try {
            return super.extractAccessToken(value, claims);

        } catch (Exception e) {
            System.out.println(e.getMessage());

            throw new ValidationException("Invalid token");
        }
    }
}
