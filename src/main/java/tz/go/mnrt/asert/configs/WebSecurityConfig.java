package tz.go.mnrt.asert.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.user.helpers.CustomUserDetailsService;

@SuppressWarnings("deprecation")
@Configuration
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/api/v1/gepg/**",
                        "/swagger-ui/**",
                        "/error",
                        "/api/v1/public/**",
                        Constant.API_V1 + "/translations/all",
                        Constant.API_V1 + "/pos-app-releases/**",
                        Constant.API_V1 + "/pos-devices/by-imei/**",
                        Constant.API_V1 + "/bills/submit-control-numbers",
                        Constant.API_V1 + "/bills/submit-payment-notifications",
                        Constant.API_V1 + "/bills/submit-reconciliations",
                        Constant.API_V1 + "/users/register-user",
                        Constant.API_V1 + "/users/register-assessor",
                        Constant.API_V1 + "/assessors/portal-approved-applications",
                        Constant.API_V1 + "/hotels/get-portal-types",
                        Constant.API_V1 + "/admin-hierarchies/get-portal-regions",
                        Constant.API_V1 + "/users/register-facility-service-owner",
                        Constant.API_V1 + "/admin-hierarchies/get-shehia-by-parent/**",
                        Constant.API_V1 + "/facilities/get-public-facilities/**",
                        Constant.API_V1 + "/facilities/portal-map-facilities/**",
                        Constant.API_V1 + "/facility-levels/portal-map-levels/**",
                        Constant.API_V1 + "/facility-levels/portal-facility-levels/**",
                        Constant.API_V1 + "/public-facilities/**",
                        Constant.API_V1 + "/facilities/get-portal-facility/**",
                        Constant.API_V1 + "/sms/send-sms-by-phone/**",
                        Constant.API_V1 + "/admin-hierarchies/get-districts",
                        Constant.API_V1 + "/dashboard/public/**",
                        Constant.API_V1 + "/hotels/listing",
                        Constant.API_V1 + "/listings/**",
                        Constant.API_V1 + "/hotels/get-facility-types",
                        Constant.API_V1 + "/admin-hierarchies/get-locations",
                        Constant.API_V1 + "/uploads/*/view",
                        Constant.API_V1 + "/admin-hierarchies/search-tree",
                        Constant.API_V1 + "/admin-hierarchies/*/get-children",
                        Constant.API_V1 + "/users/forgot-password",
                        Constant.API_V1 + "/users/reset-password",
                        "/actuator/**",
                        "/ws/**" // WebSocket endpoints
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                        "/api/v1/facilities/portal-map-facilities/**", "/api/v1/facility-levels/portal-map-levels/**",
                        "/api/v1/facility-levels/portal-facility-levels/**",
                        "/api/v1/public-facilities/**",
                        "/api/v1/facilities/get-portal-facility/**")
                .permitAll()
                .antMatchers("**/swagger-ui*/**", "**/swagger-resources/**", "/error")
                .permitAll()
                .antMatchers("/v2/api-docs", "/api/v1/public/**")
                .permitAll()
                .antMatchers("/v2/api-docs")
                .permitAll()
                .antMatchers("**/swagger-ui.html")
                .permitAll()
                .antMatchers("/actuator/**")
                .permitAll()
                .antMatchers("/oauth/authorize")
                .authenticated()
                .antMatchers("/ws/**") // Allow WebSocket endpoints
                .permitAll()
                .and()
                .requestMatchers()
                .antMatchers("/login", "/oauth/authorize");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
