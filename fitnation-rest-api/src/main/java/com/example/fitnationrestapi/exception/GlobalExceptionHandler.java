package com.example.fitnationrestapi.exception;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.response.ErrorResponse;
import com.example.fitnationcommon.dto.response.ValidationErrorDetail;
import com.example.fitnationcommon.enums.ErrorCode;
import com.example.fitnationcommon.exception.ClassBookingConflictException;
import com.example.fitnationcommon.exception.ClassBookingNotFoundException;
import com.example.fitnationcommon.exception.ClassScheduleNotFoundException;
import com.example.fitnationcommon.exception.ConversationNotFoundException;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationcommon.exception.InvalidPasswordException;
import com.example.fitnationcommon.exception.InvalidRoleException;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.GroupClassNotFoundException;
import com.example.fitnationcommon.exception.MembershipNotFoundException;
import com.example.fitnationcommon.exception.MembershipRequestConflictException;
import com.example.fitnationcommon.exception.MembershipRequestNotFoundException;
import com.example.fitnationcommon.exception.MembershipTypeNotFoundException;
import com.example.fitnationcommon.exception.NutritionPlanNotFoundException;
import com.example.fitnationcommon.exception.ProgressEntryNotFoundException;
import com.example.fitnationprogress.exception.InAppNotificationNotFoundException;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationcommon.exception.UserBlockedException;
import com.example.fitnationcommon.exception.UserInactiveException;
import com.example.fitnationcommon.exception.UserPendingException;
import com.example.fitnationcommon.exception.InvalidTokenException;
import com.example.fitnationcommon.exception.InvalidFilterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import jakarta.validation.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.fitnationcommon.exception.QrSessionExpiredException;
import com.example.fitnationcommon.exception.QrSessionAlreadyUsedException;
import com.example.fitnationcommon.exception.QrSessionInvalidException;
import com.example.fitnationcommon.exception.RateLimitExceededException;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ValidationErrorDetail(
                        fe.getField(),
                        fe.getRejectedValue(),
                        fe.getDefaultMessage()))
                .toList();

        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED,
                ApplicationConstants.VALIDATION_REQUEST_FAILED, request, fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        var fieldErrors = ex.getConstraintViolations().stream()
                .map(cv -> new ValidationErrorDetail(
                        leafPath(cv.getPropertyPath()),
                        cv.getInvalidValue(),
                        cv.getMessage()))
                .toList();

        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED,
                ApplicationConstants.VALIDATION_REQUEST_FAILED, request, fieldErrors);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleJakartaValidation(
            ValidationException ex, HttpServletRequest request) {

        log.debug("Validation failed [{}]: {}", request.getRequestURI(), ex.getMessage());

        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        var fieldErrors = List.of(
                new ValidationErrorDetail(
                        ex.getParameterName(),
                        null,
                        ApplicationConstants.REQUIRED_PARAM_MISSING)
        );

        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED,
                ApplicationConstants.REQUIRED_PARAM_MISSING, request, fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(
            HttpMessageNotReadableException ignored, HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ARGUMENT,
                ApplicationConstants.BODY_NOT_READABLE, request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.warn("Bad request [{}]: {}", request.getRequestURI(), ex.getMessage());

        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ARGUMENT,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFilter(
            InvalidFilterException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_FILTER,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRole(
            InvalidRoleException ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ARGUMENT,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(
            InvalidPasswordException ex, HttpServletRequest request) {

        return build(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request) {

        log.warn("Invalid token [{}]: {}", request.getRequestURI(), ex.getMessage());

        return build(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(QrSessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleQrExpired(
            QrSessionExpiredException ex, HttpServletRequest request) {

        return build(HttpStatus.GONE, ErrorCode.INVALID_ARGUMENT,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(QrSessionAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleQrAlreadyUsed(
            QrSessionAlreadyUsedException ex, HttpServletRequest request) {

        return build(HttpStatus.CONFLICT, ErrorCode.CONFLICT,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(QrSessionInvalidException.class)
    public ResponseEntity<ErrorResponse> handleQrInvalid(
            QrSessionInvalidException ex, HttpServletRequest request) {

        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ARGUMENT,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(
            RateLimitExceededException ex, HttpServletRequest request) {

        return build(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.INVALID_ARGUMENT,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler({
            UserBlockedException.class,
            UserInactiveException.class,
            ForbiddenOperationException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(
            RuntimeException ex, HttpServletRequest request) {

        return build(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        return build(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            TrainerNotFoundException.class,
            MembershipNotFoundException.class,
            MembershipTypeNotFoundException.class,
            NutritionPlanNotFoundException.class,
            ClassScheduleNotFoundException.class,
            ClassBookingNotFoundException.class,
            GroupClassNotFoundException.class,
            MembershipRequestNotFoundException.class,
            ConversationNotFoundException.class,
            ProgressEntryNotFoundException.class,
            InAppNotificationNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex, HttpServletRequest request) {

        return build(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            UserPendingException.class,
            MembershipRequestConflictException.class,
            ClassBookingConflictException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(
            RuntimeException ex, HttpServletRequest request) {

        return build(HttpStatus.CONFLICT, ErrorCode.CONFLICT,
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred.", request, null);
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            ErrorCode code,
            String message,
            HttpServletRequest request,
            Object details) {

        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .code(code.name())
                        .message(message)
                        .path(request.getRequestURI())
                        .details(details)
                        .build()
        );
    }

    private String leafPath(Path path) {
        var full = path.toString();
        var dot = full.lastIndexOf('.');
        return dot >= 0 ? full.substring(dot + 1) : full;
    }
}