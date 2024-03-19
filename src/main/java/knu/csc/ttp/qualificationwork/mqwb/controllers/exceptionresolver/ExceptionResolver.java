package knu.csc.ttp.qualificationwork.mqwb.controllers.exceptionresolver;

import com.fasterxml.jackson.core.JacksonException;
import jakarta.servlet.http.HttpServletRequest;
import knu.csc.ttp.qualificationwork.mqwb.LoggerUtils;
import knu.csc.ttp.qualificationwork.mqwb.abstractions.controllers.AbstractController;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.RequestException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.client.*;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InsufficientStorageException;
import knu.csc.ttp.qualificationwork.mqwb.exceptions.server.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;

@RestControllerAdvice()
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequestMapping("/api/v1")
public class ExceptionResolver extends AbstractController {

    @Autowired
    public ExceptionResolver(ApplicationContext context) {
        super(context);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FailureDto jacksonExceptionHandler(HttpMessageNotReadableException ex) {
        RequestException requestException = BadRequestException.unknownError(ex);
        LoggerUtils.warnException(logger, requestException);
        return new FailureDto(requestException);
    }

    @ExceptionHandler(JacksonException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FailureDto jacksonExceptionHandler(JacksonException ex) {
        RequestException requestException = BadRequestException.cannotProcessJson(ex);
        LoggerUtils.logException(logger, defaultLogLvl, requestException);
        return new FailureDto(requestException);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public FailureDto methodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        RequestException requestException = MethodNotAllowedException.build(request, ex);
        LoggerUtils.logException(logger, defaultLogLvl, requestException);
        return new FailureDto(requestException);
    }

    // TODO find out why handler does not work with spring security and fix the problem
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public FailureDto noHandlerFound(NoHandlerFoundException ex, HttpServletRequest request){
        RequestException requestException = NotFoundException.handlerNotFoundException(request, ex);
        LoggerUtils.logException(logger, defaultLogLvl, requestException);
        return new FailureDto(requestException);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public FailureDto sqlException(SQLException ex) {
        RequestException requestException = InternalServerErrorException.sqlException(ex);
        LoggerUtils.errorException(logger, requestException);
        return new FailureDto(requestException);
    }



    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public FailureDto authExceptionHandler(AuthException ex){
        return new FailureDto(ex);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FailureDto badRequestHandler(BadRequestException ex){
        return new FailureDto(ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public FailureDto forbiddenException(ForbiddenException ex){
        return new FailureDto(ex);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public FailureDto notFoundExceptionHandler(NotFoundException ex){
        return new FailureDto(ex);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public FailureDto unprocessableEntityExceptionHandler(UnprocessableEntityException ex){
        return new FailureDto(ex);
    }



    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public FailureDto internalServerErrorExceptionHandler(InternalServerErrorException ex){
        return new FailureDto(ex);
    }

    @ExceptionHandler(InsufficientStorageException.class)
    @ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
    public FailureDto insufficientStorageExceptionHandler(InsufficientStorageException ex){
        return new FailureDto(ex);
    }
}
