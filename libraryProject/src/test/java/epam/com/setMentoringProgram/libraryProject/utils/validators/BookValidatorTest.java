package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.dto.BookDto;
import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookValidatorTest extends BaseValidatorTestClass {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookValidator bookValidator;

    private static final BookDto BOOK_DTO = mock(BookDto.class);
    private static final String BOOK_NAME = "Clean Code";
    private static final String AUTHOR_NAME = "Robert Martin";
    private static final Date YEAR_OF_WRITING = getDateBySpecificFormat(BOOK_DATE_VALUES.getDateCreatingPattern(), "1100");;

    @BeforeAll
    public static void setUp() {
        when(BOOK_DTO.getName()).thenReturn(BOOK_NAME);
        when(BOOK_DTO.getAuthor()).thenReturn(AUTHOR_NAME);
        when(BOOK_DTO.getYearOfWriting()).thenReturn(YEAR_OF_WRITING);
    }

    @Test
    public void checkThatBookValidatorCanWorkWithBookClass() {
        boolean isVisitorClassValidated = bookValidator.supports(Book.class);
        assertThat(isVisitorClassValidated).isTrue();
    }

    @Test
    public void checkThatBookValidatorCantWorkWithNotBookClass() {
        boolean isVisitorClassValidatorWorksWithBookDtoClass = bookValidator.supports(BookDto.class);
        boolean isVisitorClassValidatorWorksWithVisitorDtoClass = bookValidator.supports(Visitor.class);
        assertThat(isVisitorClassValidatorWorksWithBookDtoClass).isFalse();
        assertThat(isVisitorClassValidatorWorksWithVisitorDtoClass).isFalse();
    }

    @Test
    void checkBookCanBeCreatedAsItDoesntExistYet() {
        when(bookService.getBookByNameAndAuthorAndYearOfWriting(BOOK_NAME, AUTHOR_NAME, YEAR_OF_WRITING)).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> bookValidator.validate(BOOK_DTO, ERRORS));
    }

    @Test
    void checkBookCantBeCreatedAsItAlreadyExists() {
        when(bookService.getBookByNameAndAuthorAndYearOfWriting(BOOK_NAME, AUTHOR_NAME, YEAR_OF_WRITING)).thenReturn(mock(Book.class));

        Assertions.assertThrows(EntityValidationException.class, () -> bookValidator.validate(BOOK_DTO, ERRORS));
    }
}