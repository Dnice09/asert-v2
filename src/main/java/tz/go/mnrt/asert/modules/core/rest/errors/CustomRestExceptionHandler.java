package tz.go.mnrt.asert.modules.core.rest.errors;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        logger.error(ex.getMessage());
        super.handleServletRequestBindingException(ex, headers, status, request);
        ApiError apiError = new ApiError(status.value(), ex.getMessage(), ex.getLocalizedMessage());
        return new ResponseEntity<>(apiError, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        super.handleExceptionInternal(ex, body, headers, status, request);
        logger.error(ex.getMessage());
        ApiError apiError = new ApiError(status.value(), ex.getMessage(), ex.getLocalizedMessage());
        return new ResponseEntity<>(apiError, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error(ex.getMessage());
        super.handleNoHandlerFoundException(ex, headers, status, request);

        ApiError apiError = new ApiError(status.value(), ex.getMessage(), ex.getRequestURL());

        return new ResponseEntity<>(apiError, headers, status);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), "Token has expired", "Token");

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({BadCredentialsException.class})
    protected ResponseEntity<Object> handleBadCredential(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());

        ApiError apiError =
                new ApiError(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Authentication failed",
                        "Invalid username or password");

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ValidationException.class})
    protected ResponseEntity<Object> handleValidationException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DeleteException.class})
    protected ResponseEntity<Object> handleDeleteException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({OAuth2Exception.class})
    protected ResponseEntity<Object> handleOAuth2Exception(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiError apiError =
                new ApiError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), ex.getMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({PSQLException.class})
    protected ResponseEntity<Object> handlePSQLException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoSuchMethodException.class})
    protected ResponseEntity<Object> handleNoSuchMethodException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpServerErrorException.class})
    protected ResponseEntity<Object> handleInternalServerError(
            HttpServerErrorException ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiError apiError =
                new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ex.getMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InsufficientAuthenticationException.class})
    protected ResponseEntity<Object> handleAuthenticationRequired(
            InsufficientAuthenticationException ex, WebRequest request) {

        logger.error(ex.getMessage());
        List<String> errors = new ArrayList<>();
        ApiError apiError =
                new ApiError(
                        HttpStatus.UNAUTHORIZED.value(), "Authentication required", "Authentication required");

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({InvalidDataAccessApiUsageException.class})
    protected ResponseEntity<Object> handleInvalidDataAccessException(
            InvalidDataAccessApiUsageException ex, WebRequest request) {
        logger.error(ex.getMessage());

        ApiError apiError =
                new ApiError(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMostSpecificCause().getMessage(),
                        ex.getMostSpecificCause().getLocalizedMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());

        ApiError apiError =
                new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), ex.getLocalizedMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    protected ResponseEntity<Object> handleEmptyResultDataAccessException(
            EmptyResultDataAccessException ex, WebRequest request) {
        logger.error(ex.getMessage());

        ApiError apiError =
                new ApiError(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMostSpecificCause().getMessage(),
                        ex.getMostSpecificCause().getLocalizedMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        logger.error(ex.getMessage());

        super.handleMethodArgumentNotValid(ex, headers, status, request);
        List<String> errors = new ArrayList<>();
        for (FieldError field : ex.getBindingResult().getFieldErrors()) {
            errors.add(field.getField().concat(" ").concat(field.getDefaultMessage()));
        }
        ApiError apiError = new ApiError(status.value(), "Validation Error", errors);
        return new ResponseEntity<>(apiError, headers, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityException(
            DataIntegrityViolationException ex, WebRequest request, HttpServletRequest httpRequest) {
        logger.error(ex.getMostSpecificCause());
        String str = ex.getMostSpecificCause().getLocalizedMessage();
        if (str.contains("violates foreign key constraint")
                && httpRequest.getMethod().equals("DELETE")) {
            str = "You cannot delete this item";
        } else if (str.contains("duplicate key ")) {
            str = str.substring(str.indexOf("Key") + 3).replace(")", "").replace("(", "");
        } else {
            str = str.substring(str.indexOf("Key") + 3);
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), str, str);

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityException(
            ConstraintViolationException ex, WebRequest request) {
        String str = ex.getLocalizedMessage();
        str = str.substring(str.indexOf("Key") + 1);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), str, str);

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidGrantException.class)
    protected ResponseEntity<Object> handleInvalidGrantException(
            InvalidGrantException ex, WebRequest request) {
        String str = ex.getMessage();
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), str, str);
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        System.out.println(ex.getLocalizedMessage());
        super.handleMissingServletRequestParameter(ex, headers, status, request);
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getLocalizedMessage());

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        System.out.println(ex.getLocalizedMessage());

        super.handleHttpRequestMethodNotSupported(ex, headers, status, request);

        String uri = ((ServletWebRequest) request).getRequest().getRequestURI();

        ApiError apiError =
                new ApiError(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        ex.getMessage(),
                        uri.concat(" ").concat(ex.getLocalizedMessage()));

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        System.out.println(ex.getLocalizedMessage());

        super.handleHttpMediaTypeNotSupported(ex, headers, status, request);

        HttpServletRequest req = ((ServletWebRequest) request).getRequest();
        ApiError apiError =
                new ApiError(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        ex.getMessage(),
                        req.getMethod()
                                .concat(" ")
                                .concat(req.getRequestURI())
                                .concat(" ")
                                .concat(ex.getLocalizedMessage()));

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        super.handleHttpMessageNotReadable(ex, headers, status, request);

        String str = ex.getMostSpecificCause().getMessage();

        String message =
                "Invalid data type in request body at "
                        + str.substring(str.indexOf("[") + 1, str.indexOf("]"));

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), message, message);

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
