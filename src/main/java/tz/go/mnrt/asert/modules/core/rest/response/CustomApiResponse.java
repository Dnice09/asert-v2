package tz.go.mnrt.asert.modules.core.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A generic and reusable API response class used to standardize JSON responses
 * across the application.
 *
 * This class encapsulates common HTTP response attributes such as:
 * - data: the payload
 * - status: HTTP status code (e.g., 200, 400)
 * - message: a descriptive message
 * - pagination info: page, size, total
 * - error details: validation and general errors
 *
 * Example usage:
 * ```java
 * return CustomApiResponse.ok("User fetched successfully", user);
 * return CustomApiResponse.validationError("Invalid input", bindingResult);
 * ```
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomApiResponse implements Serializable {

    private Object data;
    private Integer status;
    private String message;
    private int page;
    private int size;
    private Long total;
    private String[] errors;
    private List<ValidationError> validationErrors;

    /**
     * Encapsulates detailed validation error information for a single field.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        // Field name with the error
        private String field;
        // Error message (e.g., "must not be blank")
        private String message;
        // The actual value provided by the user
        private String rejectedValue;
        // The name of the object that failed validation
        private String objectName;
    }

    // ------------------------ Static Factory Methods ------------------------

    /**
     * Creates a 400 Bad Request response containing validation errors from
     * BindingResult.
     *
     * @param message       Custom message describing the error (e.g., "Validation
     *                      failed")
     * @param bindingResult The BindingResult containing field and global validation
     *                      errors
     * @return CustomApiResponse with populated validation error details
     *
     *         Example usage:
     *         ```java
     *         if (bindingResult.hasErrors()) {
     *         return CustomApiResponse.validationError("Input validation failed",
     *         bindingResult);
     *         }
     *         ```
     */
    public static CustomApiResponse validationError(String message, BindingResult bindingResult) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(message);

        // Handle global (non-field-specific) errors
        List<String> generalErrors = bindingResult.getGlobalErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        if (!generalErrors.isEmpty()) {
            response.setErrors(generalErrors.toArray(new String[0]));
        }

        // Handle field-specific validation errors
        List<ValidationError> validationErrors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(error -> {
            ValidationError validationError = new ValidationError(
                    error.getField(),
                    error.getDefaultMessage(),
                    error.getRejectedValue() != null ? error.getRejectedValue().toString() : null,
                    error.getObjectName());
            validationErrors.add(validationError);
        });

        response.setValidationErrors(validationErrors);
        return response;
    }

    /**
     * Creates a 400 Bad Request response with a single validation error.
     *
     * @param field         Name of the field that failed validation
     * @param message       Description of the error
     * @param rejectedValue Value that failed validation
     * @return CustomApiResponse with a single validation error
     */
    public static CustomApiResponse validationError(String field, String message, String rejectedValue) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed");

        List<ValidationError> validationErrors = new ArrayList<>();
        validationErrors.add(new ValidationError(field, message, rejectedValue, null));

        response.setValidationErrors(validationErrors);
        return response;
    }

    /**
     * Creates a 400 Bad Request response with multiple custom validation errors.
     *
     * @param message          Message describing the overall validation failure
     * @param validationErrors List of individual validation errors
     * @return CustomApiResponse with validationErrors populated
     */
    public static CustomApiResponse validationErrors(String message, List<ValidationError> validationErrors) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(message);
        response.setValidationErrors(validationErrors);
        return response;
    }

    /**
     * Creates a standard 200 OK response with data.
     *
     * @param data The response payload
     * @return CustomApiResponse
     */
    public static CustomApiResponse ok(Object data) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setData(data);
        return response;
    }

    /**
     * Creates a 200 OK response with message and data.
     *
     * @param message Message to accompany the response
     * @param data    Response payload
     * @return CustomApiResponse
     */
    public static CustomApiResponse ok(String message, Object data) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * Creates a 200 OK response with pagination details.
     *
     * @param message       Descriptive message
     * @param data          Response data (e.g., list of users)
     * @param page          Current page number
     * @param size          Size of the page
     * @param totalElements Total number of records available
     * @return CustomApiResponse with pagination info
     */
    public static CustomApiResponse ok(String message, Object data, int page, int size, Long totalElements) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(message);
        response.setData(data);
        response.setPage(page);
        response.setSize(size);
        response.setTotal(totalElements);
        return response;
    }

    /**
     * Returns a 201 Created response.
     *
     * @param message Message describing what was created
     * @param data    Created object
     * @return CustomApiResponse
     */
    public static CustomApiResponse created(String message, Object data) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * Returns a 202 Accepted response.
     *
     * @param message Optional message
     * @param data    Payload that has been accepted for processing
     * @return CustomApiResponse
     */
    public static CustomApiResponse accepted(String message, Object data) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.ACCEPTED.value());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * Returns a 400 Bad Request response with data.
     *
     * @param message Message explaining the bad request
     * @param data    Any relevant data or diagnostics
     * @return CustomApiResponse
     */
    public static CustomApiResponse badRequest(String message, Object data) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * Generates a 200 OK response using a Spring Page object.
     * Extracts content from the Page object to make it directly accessible.
     *
     * @param page Page object containing data and pagination metadata
     * @return CustomApiResponse with list of content and pagination info
     */
    public static <T> CustomApiResponse ok(Page<T> page) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setData(page.getContent()); // Extract content to avoid nested .content access
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    /**
     * Returns a 200 OK response with just a message.
     *
     * @param message Message to send in response
     * @return CustomApiResponse
     */
    public static CustomApiResponse ok(String message) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(message);
        return response;
    }

    /**
     * Returns a 204 No Content response with a message.
     *
     * @param message Description of the response
     * @return CustomApiResponse
     */
    public static CustomApiResponse noContent(String message) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.NO_CONTENT.value());
        response.setMessage(message);
        return response;
    }

    /**
     * Returns a 401 Unauthorized response indicating access is denied.
     *
     * @param message Reason for denial
     * @return CustomApiResponse
     */
    public static CustomApiResponse accessDenied(String message) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(message);
        return response;
    }

    /**
     * Returns a 404 Not Found response.
     *
     * @param message Reason why the resource was not found
     * @return CustomApiResponse
     */
    public static CustomApiResponse notFound(String message) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage(message);
        return response;
    }

    /**
     * Returns a 200 OK response if Optional contains data.
     * Otherwise throws a ValidationException.
     *
     * @param data Optional value (e.g., Optional<User>)
     * @return CustomApiResponse with data if present
     * @throws ValidationException if optional is empty
     */
    public static CustomApiResponse ok(Optional<?> data) {
        if (data.isPresent()) {
            CustomApiResponse response = new CustomApiResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setData(data.get());
            return response;
        } else {
            throw new ValidationException("Resource not found");
        }
    }

    /**
     * Returns a 302 Found response with error details.
     *
     * @param message Message for the client
     * @param error   Array of string errors
     * @return CustomApiResponse
     */
    public static CustomApiResponse found(String message, String[] error) {
        CustomApiResponse response = new CustomApiResponse();
        response.setStatus(HttpStatus.FOUND.value());
        response.setMessage(message);
        response.setErrors(error);
        return response;
    }
}
