package tz.go.mnrt.asert.configs;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.user.dtos.CurrentUserDto;
import tz.go.mnrt.asert.modules.user.service.UserService;

@Slf4j
public class CustomTokenResponse implements TokenEnhancer {

    private final UserService userService;

    public CustomTokenResponse(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        try {
            log.debug("Enhancing token for user: {}", authentication.getName());
            CurrentUserDto currentUserDto = userService.findUserInfo(authentication.getName());

            if (currentUserDto == null) {
                log.error("User info not found for user: {}", authentication.getName());
                throw new ValidationException("User info not found");
            }

            log.debug("Current user DTO: {}", currentUserDto);

            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("user", currentUserDto);
            additionalInfo.put("message", "Successfully Logged In");

            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

            log.debug("Token enhanced successfully for user: {}", authentication.getName());
            return accessToken;
        } catch (Exception e) {
            log.error("Error enhancing token for user: {}", authentication.getName(), e);
            throw new ValidationException("Can't enhance token", e);
        }
    }
}
