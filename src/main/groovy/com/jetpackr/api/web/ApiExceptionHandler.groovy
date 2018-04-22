package com.jetpackr.api.web

import groovy.transform.CompileStatic
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@CompileStatic
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private final static String VALIDATION_ERROR = "Validation Error"

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        List<String> errors = new ArrayList<String>()

        for (FieldError fieldError : exception.bindingResult.fieldErrors) {
            final String error = "'${fieldError.field}' ${fieldError.defaultMessage}"
            errors << error
        }

        for (ObjectError objectError : exception.bindingResult.globalErrors) {
            final String error = "'${objectError.objectName}' ${objectError.defaultMessage}"
            errors << error
        }

        ApiError apiError =
                new ApiError(
                        message: VALIDATION_ERROR,
                        errors: errors
                )

        return handleExceptionInternal(exception, apiError, headers, HttpStatus.BAD_REQUEST, request)
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception, HttpHeaders headers,
            HttpStatus status, WebRequest request
    ) {
        String error = "'${exception.parameterName}' parameter is missing"

        ApiError apiError =
                new ApiError(
                        message: VALIDATION_ERROR,
                        errors: Arrays.asList(error)
                )

        return handleExceptionInternal(exception, apiError, headers, HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler([ ConstraintViolationException.class ])
    ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception,
            @RequestHeader HttpHeaders headers
    ) {
        List<String> errors = new ArrayList<String>()
        for (ConstraintViolation<?> violation : exception.constraintViolations) {
            final String error = "'${violation.rootBeanClass.name}'.'${violation.propertyPath}' ${violation.message}"
            errors << error
        }

        ApiError apiError =
                new ApiError(
                        message: VALIDATION_ERROR,
                        errors: errors
                )

        new ResponseEntity<ApiError>(apiError, headers, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler([ MethodArgumentTypeMismatchException.class ])
    ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            @RequestHeader HttpHeaders headers
    ) {
        String error = "'${exception.name}' should be of type ${exception.requiredType.name}"

        ApiError apiError =
                new ApiError(
                        message: VALIDATION_ERROR,
                        errors: Arrays.asList(error)
                )

        new ResponseEntity<ApiError>(apiError, headers, HttpStatus.BAD_REQUEST)
    }
}