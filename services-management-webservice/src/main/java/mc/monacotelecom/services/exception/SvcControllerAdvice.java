package mc.monacotelecom.services.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.importer.exceptions.CommonImportNotFoundException;
import mc.monacotelecom.inventory.common.importer.exceptions.CommonImportValidationException;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.inventory.common.recycling.exceptions.JobNotFoundException;
import mc.monacotelecom.services.dto.CustomErrorResponse;
import mc.monacotelecom.services.exceptions.SvcConflictException;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.Clock;
import java.time.LocalDateTime;

import static mc.monacotelecom.services.translation.TranslationMessages.FIELD_VALIDATION_ERROR;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class SvcControllerAdvice {
    private final Clock clock;
    private final LocalizedMessageBuilder localizedMessageBuilder;

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class, NullPointerException.class})
    public CustomErrorResponse handleRunTimeException(RuntimeException e) {
        return error(INTERNAL_SERVER_ERROR, e, e.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler({SvcNotFoundException.class, CommonImportNotFoundException.class, JobNotFoundException.class})
    public CustomErrorResponse handleNotFoundException(Exception e) {
        return error(NOT_FOUND, e, e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({SvcValidationException.class, ConstraintViolationException.class, CommonImportValidationException.class})
    public CustomErrorResponse handle(Exception e) {
        return error(BAD_REQUEST, e, e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public CustomErrorResponse handleBindException(BindException e) {
        final var fieldError = e.getFieldErrors().get(0);
        String message = localizedMessageBuilder.getLocalizedMessage(FIELD_VALIDATION_ERROR, ObjectUtils.nullSafeToString(fieldError.getRejectedValue()), fieldError.getField());
        return error(BAD_REQUEST, e, message);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CustomErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final var fieldError = e.getBindingResult().getFieldErrors().get(0);
        String message = localizedMessageBuilder.getLocalizedMessage(FIELD_VALIDATION_ERROR, ObjectUtils.nullSafeToString(fieldError.getRejectedValue()), fieldError.getField());
        return error(BAD_REQUEST, e, message);
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler({SvcConflictException.class})
    public CustomErrorResponse handle(SvcConflictException e) {
        return error(CONFLICT, e, e.getMessage());
    }

    private CustomErrorResponse error(HttpStatus status, Exception e, String message) {
        log.error("Exception : ", e);
        var customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setTimestamp(LocalDateTime.now(clock));
        customErrorResponse.setError(message);
        customErrorResponse.setStatus(status.value());
        return customErrorResponse;
    }
}
