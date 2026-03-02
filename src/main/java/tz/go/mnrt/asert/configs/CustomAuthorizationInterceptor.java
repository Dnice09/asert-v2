package tz.go.mnrt.asert.configs;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.authority.repository.AuthorityRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.utils.Util;

/**
 * The CustomAuthorizationInterceptor class is a Spring component that
 * implements the HandlerInterceptor interface.
 * It is responsible for handling authorization checks for incoming HTTP
 * requests.
 * The interceptor ensures that users have the appropriate roles to access
 * specific resources and perform actions,
 * considering both authentication-based roles and approval roles based on the
 * state of a facility.
 */
@Component
@Slf4j
public class CustomAuthorizationInterceptor implements HandlerInterceptor {

    private final AuthorityRepository authorityRepository;
    private final HotelRepository hotelRepository;
    private final RoleChecker roleChecker;

    /**
     * Constructor to initialize the CustomAuthorizationInterceptor with necessary
     * dependencies.
     *
     * @param authorityRepository Repository for accessing user authorities.
     * @param facilityRepository  Repository for accessing facility information.
     * @param roleChecker         Service for checking user roles.
     */
    public CustomAuthorizationInterceptor(AuthorityRepository authorityRepository,
            HotelRepository hotelRepository, RoleChecker roleChecker) {
        this.authorityRepository = authorityRepository;
        this.hotelRepository = hotelRepository;
        this.roleChecker = roleChecker;
    }

    /**
     * Intercepts HTTP requests before they reach the controller to perform
     * authorization checks.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param handler  the handler (controller method) to be executed
     * @return true if the request should proceed, false otherwise
     * @throws Exception if an error occurs during authorization
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        log.info("******Intercepting****");
        log.info(request.getRequestURI());

        // Bypass WebSocket handshake requests
        if ("websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            return true;
        }

        if (handler.getClass().getSimpleName().equals("ResourceHttpRequestHandler")
                || request.getRequestURI().contains("swagger")
                || request.getRequestURI().contains("api-doc")
                || request.getRequestURI().contains("authenticate")
                || request.getRequestURI().contains("gepg")
                || request.getRequestURI().contains("/error")
                || request.getRequestURI().contains("logout")) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        NoAuthorization noAuthorization = handlerMethod.getMethodAnnotation(NoAuthorization.class);

        if (noAuthorization == null) {

            String resourceName = Util.parseResource(handlerMethod.getBeanType().getSimpleName());
            resourceName = resourceName.substring(0, 1).toUpperCase() + resourceName.substring(1);
            String actionName = handlerMethod.getMethod().getName();

            log.info("Finding Authority");
            if (authorityRepository
                    .findFirstByUserAndResourceAndAction(authentication.getName(), resourceName, actionName)
                    .isPresent()) {

                // Check for authentication-based roles
                if (roleChecker.hasAuthBasedRole(authentication)) {
                    return true;
                }

                // Additional check for state-based approval roles if facility is not registered
                if (request.getMethod().equals("GET")) {
                    Pattern pattern = Pattern.compile("/facilities/([0-9a-fA-F\\-]{36})"); // Regular expression to
                                                                                           // match "facilities/{uuid}"
                    Matcher matcher = pattern.matcher(request.getRequestURI());

                    if (matcher.find()) {
                        String uuidStr = matcher.group(1); // Extracted UUID as a string
                        UUID facilityUuid;

                        try {
                            facilityUuid = UUID.fromString(uuidStr); // Convert string to UUID
                        } catch (IllegalArgumentException e) {
                            sendBadRequestResponse(response, "Invalid UUID format");
                            return false;
                        }

                        Hotel hotel = hotelRepository
                                .findByUuid(facilityUuid)
                                .orElseThrow(
                                        () -> new ValidationException(
                                                "Facility with uuid {" + facilityUuid + "} not found"));

                        if (hotel.getStatus() != HotelState.GRADED
                                && !roleChecker.hasApprovalRoleBasedOnState(authentication, hotel)) {
                            sendFacilityStateFailureResponse(response, hotel.getStatus().toString(), resourceName);
                            return false;
                        }
                    }
                }

                return true;
            } else {
                sendForbiddenResponse(response, actionName, resourceName);
                return false;
            }

        } else {
            return true;
        }
    }

    /**
     * Sends a forbidden response with a JSON message indicating that the user is
     * not authorized to access the resource or perform the action.
     *
     * @param response     the HTTP response
     * @param actionName   the name of the action the user attempted to perform
     * @param resourceName the name of the resource the user attempted to access
     * @throws IOException if an error occurs while writing the response
     */
    private void sendForbiddenResponse(HttpServletResponse response, String actionName, String resourceName)
            throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter()
                .write("{\"message\":\"You Are Not Authorized to Access {" + actionName + "} on {" + resourceName
                        + "}\", \"errors\":[\"You Are Not Authorized to Access {" + actionName + "} on {" + resourceName
                        + "}\"]}");
    }

    /**
     * Sends a forbidden response with a JSON message indicating that the user is
     * not authorized to access the resource or perform the action.
     *
     * @param response     the HTTP response
     * @param actionName   the name of the action the user attempted to perform
     * @param resourceName the name of the resource the user attempted to access
     * @throws IOException if an error occurs while writing the response
     */
    private void sendFacilityStateFailureResponse(HttpServletResponse response, String state, String resourceName)
            throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter()
                .write("{\"message\":\"Your Role does not have Access to facility at {" + state + "} review stage {"
                        + "}\", \"errors\":[\"Your Role does not have Access to facility at {" + state
                        + "} review stage \"]}");
    }

    /**
     * Sends a bad request response with a JSON message indicating that the request
     * is invalid.
     *
     * @param response the HTTP response
     * @param message  the error message to send
     * @throws IOException if an error occurs while writing the response
     */
    private void sendBadRequestResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\", \"errors\":[\"" + message + "\"]}");
    }
}
