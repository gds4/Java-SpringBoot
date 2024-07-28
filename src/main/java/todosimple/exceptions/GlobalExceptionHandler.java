package todosimple.exceptions;

import java.net.http.HttpHeaders;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import lombok.extern.slf4j.Slf4j;
import todosimple.services.exceptions.DataBindingViolationException;
import todosimple.services.exceptions.ObjectNotFoundException;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    //captura o stack trace para ser printado no response, para desativar deve modificar no arquivo de application properties server.error.include-exception de true para false, é utili manter como true enquando desenvolve
    @Value("${server.error.include-exception}")
    private boolean printStackTree;


    //Ocorre quando o dado fornecido está violando alguma regra - por exemplo um campo password vazio ( "" ) - retorna 422
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException methodArgumentNotValidException,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request){
            ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(), 
                "Validation error. Check 'errors' field for details");
            for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()){
                errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
            }

            return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    //Erro geral para erros não tratados - 500
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private ResponseEntity<Object> handleAllUncaughtException(
            Exception exception,
            WebRequest request){
        final String errorMessage = "Unknown error occurred";
        log.error(errorMessage, exception);
        return buildErrorResponse(
            exception,
            errorMessage,
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        );
    }


    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (this.printStackTree){
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request){
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }


    //Verifica a Integridade dos dados fornecidos, por exemplo, ocorre quando se está tentando criar um usuario com um username que já existe no banco, uma vez que username campo unico - retorna 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
        DataIntegrityViolationException dataIntegrityViolationException,
        WebRequest request
    ){
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Failed to save entity with integrity problems: " + errorMessage, dataIntegrityViolationException);
        return buildErrorResponse(
            dataIntegrityViolationException, 
            errorMessage, 
            HttpStatus.CONFLICT, 
            request);
    
    }

    //ocorre quando não estão sendo fornecido todos os campos, retorna um erro - 422
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(
        ConstraintViolationException constraintViolationException,
        WebRequest request
    ){
        log.error("Failed to validate element", constraintViolationException);
        return buildErrorResponse(
            constraintViolationException, 
            HttpStatus.UNPROCESSABLE_ENTITY, 
            request);
    }

    //Ocorre quando não é encontrado o objeto solicitado - 404
    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(
        ObjectNotFoundException objectNotFoundException,
        WebRequest request
    ){
        log.error("Failed to find the requested element", objectNotFoundException);
        return buildErrorResponse(
            objectNotFoundException, 
            HttpStatus.NOT_FOUND, 
            request);
    }

    //ocorre quando tenta excluir um usuario que possui tasks associadas, ou uma entidade que possui dados associados a ela - 409
    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindindViolationException(
        DataBindingViolationException dataBindingViolationException,
        WebRequest request
    ){
        log.error("Failed to save entity with associated data", dataBindingViolationException);
        return buildErrorResponse(
            dataBindingViolationException, 
            HttpStatus.CONFLICT, 
            request);
    }

}
