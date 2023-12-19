package com.sharyar.Electrify.ElectronicsShop.exceptions;

import com.sharyar.Electrify.ElectronicsShop.dto.ApiResponseMessage;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> resourceNotFoundHandler(ResourceNotFoundException e)
    {
        logger.error("Resource not found exceptionHandler invoked!");
        e.printStackTrace();

      ApiResponseMessage response =   ApiResponseMessage.builder().
                message(e.getMessage()).status(HttpStatus.NOT_FOUND)
                .success(false).build();

      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleMethodArgumentValidationException(MethodArgumentNotValidException e)
    {
       List<ObjectError> allErrors =  e.getBindingResult().getAllErrors();
       Map < String, Object > response = new HashMap<>();
       allErrors.stream().forEach(
               objectError -> {
                   String message = objectError.getDefaultMessage();
                   String field = ((FieldError)objectError).getField();
                   response.put(field, message);
               }
       );

       return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateValueException.class , DataAccessException.class})
    public ResponseEntity<ApiResponseMessage> handleDuplicateValueException(RuntimeException e)
    {
        e.printStackTrace();
        ApiResponseMessage responseMessage = new ApiResponseMessage();
        responseMessage.setMessage(e.getMessage());
        responseMessage.setStatus(HttpStatus.BAD_REQUEST);
        responseMessage.setSuccess(false);

        return new ResponseEntity<>(responseMessage , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseMessage> handleIllegalArgument(IllegalArgumentException e)
    {
        logger.error("e.getMessage = {}" , e.getMessage()); // same as localized
        logger.error("e.getCause() ={}", e.getCause()); // useless,shows nothing
        logger.error("e.getLocalizedMessage() = {}" , e.getLocalizedMessage()); // same as getMessage()

        ApiResponseMessage responseMessage = ApiResponseMessage.builder().message(e.getMessage()).success(false).status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>( responseMessage , HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler({PropertyReferenceException.class })
    public ResponseEntity<ApiResponseMessage> handlePropertyReferenceException(PropertyReferenceException e)
    {
        ApiResponseMessage responseMessage = ApiResponseMessage.builder().message(e.getMessage())
                .success(false).build();

        return  new ResponseEntity<>(responseMessage , HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler({MissingServletRequestPartException.class })
    public ResponseEntity<BadRequestApiException> handleBadRequestApiException(MissingServletRequestPartException e)
    {
        logger.error("Bad Request exception = {}" , e.getMessage());
        BadRequestApiException responseMessage = new BadRequestApiException();
        responseMessage.setMessage(e.getMessage());
        e.printStackTrace();
        return  new ResponseEntity<>(responseMessage , HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseMessage> handleGenericExceptions(RuntimeException e)
    {
        ApiResponseMessage responseMessage = ApiResponseMessage
                .builder()
                .message(e.getMessage())
                .success(false)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        e.printStackTrace();
        return new ResponseEntity<>(responseMessage , HttpStatus.BAD_REQUEST);
    }



}
