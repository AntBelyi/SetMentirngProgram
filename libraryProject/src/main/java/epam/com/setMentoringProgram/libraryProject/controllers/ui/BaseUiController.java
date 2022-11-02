package epam.com.setMentoringProgram.libraryProject.controllers.ui;

import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.utils.validators.BookValidator;
import epam.com.setMentoringProgram.libraryProject.utils.validators.VisitorValidator;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Objects;

public abstract class BaseUiController {

    protected boolean validateEntity(BindingResult bindingResult, Object dtoEntity, Object validator, Model model) {
        validateEntity(dtoEntity, validator, bindingResult, model);
        return bindingResult.hasErrors() || Objects.nonNull(model.getAttribute("dateError"));
    }

    private <T> void validateEntity(T dtoEntity, T validator, BindingResult bindingResult, Model model) {
        if(dtoEntity instanceof VisitorDto) {
            VisitorValidator visitorValidator = (VisitorValidator) validator;
            visitorValidator.validate(dtoEntity, bindingResult, model);
        } else {
            BookValidator bookValidator = (BookValidator) validator;
            bookValidator.validate(dtoEntity, bindingResult, model);
        }
    }

}
