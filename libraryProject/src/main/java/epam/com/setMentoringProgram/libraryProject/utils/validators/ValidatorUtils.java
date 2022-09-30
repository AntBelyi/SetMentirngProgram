package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ValidatorUtils {

    public static void validateEntityFields(BindingResult bindingResult) {
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

}
