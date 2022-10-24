package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.*;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.validateDate;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.validateDateRanges;

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
        Visitor visitorFromDB = visitorService.getVisitorByInitialsAndDateOfBirth(visitor.getInitials(), visitor.getDateOfBirth());
        if(Objects.nonNull(visitorFromDB)) {
            throw new EntityValidationException(VISITOR_IS_ALREADY_REGISTERED.getErrorMessage());
        }
    }

    public void validate(Object target, Errors errors, Model model) {
        dateOfBirthValidation(target, errors, model);
        userUniqueValidation(target, errors, model);
    }

    private void userUniqueValidation(Object target, Errors errors, Model model) {
        Visitor visitor = convertToEntity(target, Visitor.class);
        Visitor visitorFromDB = visitorService.getVisitorByInitialsAndDateOfBirth(visitor.getInitials(), visitor.getDateOfBirth());
        if(Objects.nonNull(visitorFromDB)) {
            errors.rejectValue("initials", "", VISITOR_IS_ALREADY_REGISTERED.getErrorMessage());
            model.addAttribute("dateError", VISITOR_IS_ALREADY_REGISTERED.getErrorMessage());
        }
    }

    private void dateOfBirthValidation(Object target, Errors errors, Model model) {
        Visitor visitor = convertToEntity(target, Visitor.class);
        Optional<FieldError> fieldError = errors.getFieldErrors().stream().filter(fieldEr -> fieldEr.getField()
                .equals("dateOfBirth")).findFirst();
        try {
            if(Objects.nonNull(visitor.getDateOfBirth())) {
                validateDateRanges(visitor.getDateOfBirth(), VISITOR_DATE_VALUES, DATE_OF_BIRTH_CANT_BE_EARLIER_THAN, DATE_OF_BIRTH_CANT_BE_LATER_THAN_CURRENT_DATE);
            } else {
                fieldError.ifPresent(error -> validateDate(String.valueOf(error.getRejectedValue()), VISITOR_DATE_VALUES, DATE_OF_BIRTH_CANT_BE_EARLIER_THAN, DATE_OF_BIRTH_CANT_BE_LATER_THAN_CURRENT_DATE));
            }
        } catch (EntityValidationException ex) {
            model.addAttribute("dateError", ex.getMessage());
        }
    }
}
