package tz.go.mnrt.asert.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import tz.go.mnrt.asert.modules.authority.repository.AuthorityRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoleChecker roleChecker;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new CustomAuthorizationInterceptor(authorityRepository, hotelRepository, roleChecker));
    }
}
