package epam.com.setMentoringProgram.libraryProject.controllers.api;

import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityErrorResponse;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import epam.com.setMentoringProgram.libraryProject.utils.validators.BookValidator;
import epam.com.setMentoringProgram.libraryProject.utils.validators.VisitorValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;
import java.util.List;

public abstract class BaseApiController {

    @ExceptionHandler
    private ResponseEntity<EntityErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        EntityErrorResponse entityErrorResponse = new EntityErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                exception.getMessage()
        );
        return new ResponseEntity<>(entityErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<EntityErrorResponse> handleEntityNotCreatedException(EntityValidationException exception) {
        EntityErrorResponse exceptionResponse = new EntityErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                exception.getMessage().split("\\.")
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    public void validateEntity(BindingResult bindingResult, Object dtoEntity, Object validator) {
        validateEntity(dtoEntity, validator, bindingResult);
        if(bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errorList = bindingResult.getFieldErrors();
            for(FieldError fieldError : errorList) {
                errorMessage.append(fieldError.getField())
                        .append(" - ")
                        .append(fieldError.getDefaultMessage())
                        .append(".");
            }
            throw new EntityValidationException(errorMessage.toString());
        }
    }

    private <T> void validateEntity(T dtoEntity, T validator, BindingResult bindingResult) {
        if(dtoEntity instanceof VisitorDto) {
            VisitorValidator visitorValidator = (VisitorValidator) validator;
            visitorValidator.validate(dtoEntity, bindingResult);
        } else {
            BookValidator bookValidator = (BookValidator) validator;
            bookValidator.validate(dtoEntity, bindingResult);
        }
    }

}
