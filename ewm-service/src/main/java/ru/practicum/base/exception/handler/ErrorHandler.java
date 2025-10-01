package ru.practicum.base.exception.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.base.exception.*;
import ru.practicum.base.exception.error.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleForbiddenException(ConflictException e) {
        log.error(e.getLocalizedMessage(), e.getMessage());
        return new ErrorResponse(
                HttpStatus.CONFLICT.toString(),
                "Конфликт обработки данных",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleForbiddenException(ConditionsNotMetException e) {
        log.error(e.getLocalizedMessage(), e.getMessage());
        return new ErrorResponse(
                HttpStatus.CONFLICT.toString(),
                "Конфликт обработки данных",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbiddenException(ValidationException e) {
        log.error(e.getLocalizedMessage(), e.getMessage());
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Неверные входные параметры",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error(e.getLocalizedMessage(), e.getMessage());
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.toString(),
                "Объекта не существует",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBlankException(MethodArgumentNotValidException e) {
        log.error(e.getLocalizedMessage(), e.getMessage());

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Неверные входные параметры",
                e.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleonstraintViolationException(ConstraintViolationException e) {
        log.error(e.getLocalizedMessage(), e.getMessage());

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Ошибка валидации",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(DeniedAccess e) {
        log.error(e.getLocalizedMessage(), e.getMessage());
        return new ErrorResponse(
                HttpStatus.FORBIDDEN.toString(),
                "Отсутствуют права",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbiddenException(MethodArgumentTypeMismatchException e) {
        log.error(e.getLocalizedMessage(), e.getMessage());
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Некорректный запрос",
                String.format("Параметр %s не может принимать значение %s", e.getName(), e.getValue()));
    }

}
