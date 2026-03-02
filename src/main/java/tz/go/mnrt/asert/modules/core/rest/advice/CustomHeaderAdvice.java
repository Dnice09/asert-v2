package tz.go.mnrt.asert.modules.core.rest.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

/**
 * This class is a Spring MVC Controller Advice that intercepts all responses
 * of type CustomApiResponse before they are written to the HTTP response.
 * Created by: Mtabe
 * It is used to modify the HTTP status code based on the status field in the
 * CustomApiResponse object.
 */
@ControllerAdvice
public class CustomHeaderAdvice implements ResponseBodyAdvice<CustomApiResponse> {

    /**
     * This method determines whether this advice is applicable to a given
     * controller method's return type.
     *
     * @param returnType    The return type of the controller method.
     * @param converterType The selected converter type.
     * @return true if the return type is assignable to CustomApiResponse, false
     *         otherwise.
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Apply this advice to all methods returning CustomApiResponse
        return CustomApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    /**
     * This method is called before the response body is written to the output
     * message.
     * It allows us to modify the response, such as setting the HTTP status code.
     *
     * @param body                  The body of the response.
     * @param returnType            The return type of the controller method.
     * @param selectedContentType   The selected content type for the response.
     * @param selectedConverterType The selected converter type.
     * @param request               The current request.
     * @param response              The current response.
     * @return The (possibly modified) body of the response.
     */
    @Override
    public CustomApiResponse beforeBodyWrite(CustomApiResponse body, MethodParameter returnType,
            MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        // Check if the body is not null and contains a status field
        if (body != null && body.getStatus() != null) {
            // Resolve the HttpStatus from the status code in the response body
            HttpStatus httpStatus = HttpStatus.resolve(body.getStatus());
            if (httpStatus != null) {
                // Set the HTTP status code of the response
                response.setStatusCode(httpStatus);
            }
        }
        return body; // Return the (possibly modified) response body
    }
}
