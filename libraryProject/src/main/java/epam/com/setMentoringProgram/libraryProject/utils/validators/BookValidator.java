package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.BOOK_IS_ALREADY_EXIST;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;

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
        Book bookFromDB = bookService.getBookByNameAndYearOfWriting(book.getName(), book.getAuthor(), book.getYearOfWriting());
        if(Objects.nonNull(bookFromDB)) {
            throw new EntityValidationException(BOOK_IS_ALREADY_EXIST.getErrorMessage());
        }
    }
}
