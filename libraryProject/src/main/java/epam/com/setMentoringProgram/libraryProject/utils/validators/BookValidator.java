package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.*;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.validateDate;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.validateDateRanges;

@Component
public class BookValidator implements Validator {

    private final BookService bookService;

    @Autowired
    public BookValidator(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = convertToEntity(target, Book.class);
        Book bookFromDB = bookService.getBookByNameAndAuthorAndYearOfWriting(book.getName(), book.getAuthor(), book.getYearOfWriting());
        if(Objects.nonNull(bookFromDB)) {
            throw new EntityValidationException(BOOK_IS_ALREADY_EXIST.getErrorMessage());
        }
    }

    public void validate(Object target, Errors errors, Model model) {
        yearOfWritingValidation(target, errors, model);
        bookUniqueValidation(target, errors, model);
    }

    private void bookUniqueValidation(Object target, Errors errors, Model model) {
        Book book = convertToEntity(target, Book.class);
        Book bookFromDB = bookService.getBookByNameAndAuthorAndYearOfWriting(book.getName(), book.getAuthor(), book.getYearOfWriting());
        if(Objects.nonNull(bookFromDB)) {
            errors.rejectValue("name", "", BOOK_IS_ALREADY_EXIST.getErrorMessage());
            errors.rejectValue("author", "", BOOK_IS_ALREADY_EXIST.getErrorMessage());
            model.addAttribute("dateError", BOOK_IS_ALREADY_EXIST.getErrorMessage());
        }
    }

    private void yearOfWritingValidation(Object target, Errors errors, Model model) {
        Book book = convertToEntity(target, Book.class);
        Optional<FieldError> fieldError = errors.getFieldErrors().stream().filter(fieldEr -> fieldEr.getField()
                .equals("yearOfWriting")).findFirst();
        try {
            if(Objects.nonNull(book.getYearOfWriting())) {
                validateDateRanges(book.getYearOfWriting(), BOOK_DATE_VALUES, BOOK_YEAR_OF_WRITING_CANT_BE_GREATER_THAN_1000, BOOK_YEAR_OF_WRITING_CANT_BE_LATER_THAN_CURRENT_DATE);
            } else {
                fieldError.ifPresent(error -> validateDate(String.valueOf(error.getRejectedValue()), BOOK_DATE_VALUES, BOOK_YEAR_OF_WRITING_CANT_BE_GREATER_THAN_1000, BOOK_YEAR_OF_WRITING_CANT_BE_LATER_THAN_CURRENT_DATE));
            }
        } catch (EntityValidationException ex) {
            model.addAttribute("dateError", ex.getMessage());
        }
    }
}
