package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.VISITOR_IS_ALREADY_REGISTERED;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;

@Component
public class VisitorValidator implements Validator {

    private final VisitorService visitorService;

    @Autowired
    public VisitorValidator(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Visitor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Visitor visitor = convertToEntity(target, Visitor.class);
        Visitor visitorFromDB = visitorService.getVisitorByInitials(visitor.getInitials(), visitor.getDateOfBirth());
        if(Objects.nonNull(visitorFromDB)) {
            throw new EntityValidationException(VISITOR_IS_ALREADY_REGISTERED.getErrorMessage());
        }
    }
}
