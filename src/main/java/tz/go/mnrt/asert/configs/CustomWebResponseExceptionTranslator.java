package tz.go.mnrt.asert.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;
import tz.go.mnrt.asert.modules.core.rest.errors.ApiError;

@SuppressWarnings("rawtypes")
@Slf4j
@Component
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity<ApiError> translate(Exception e) throws Exception {

        log.error(e.getClass().getName());
        String message = e.getLocalizedMessage();
        if (e instanceof InvalidGrantException) {
            message = "Invalid username or password";
        }
        if (e instanceof InvalidTokenException) {
            message = "Invalid token";
        }
        if (e instanceof InsufficientAuthenticationException) {
            message = "Session expired";
        }

        ApiError error = new ApiError(HttpStatus.UNAUTHORIZED.value(), message, message);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(error, headers, HttpStatus.UNAUTHORIZED);
    }
}
