package epam.com.setMentoringProgram.libraryProject.unitTests.services;

import epam.com.setMentoringProgram.libraryProject.BaseBookAbstractTest;
import epam.com.setMentoringProgram.libraryProject.dto.BookDto;
import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.repositories.BookRepository;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.BOOK_BY_ID_NOT_FOUND;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest extends BaseBookAbstractTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private VisitorService visitorService;

    @InjectMocks
    private BookService bookService;

    @Test
    void getBooksTest() {
        when(bookRepository.findAll(Sort.by(ID_FIELD))).thenReturn(initBookList);

        List<Book> bookList = bookService.getBooks();

        assertThat(bookList).isNotNull().hasSameSizeAs(initBookList);

        for (int i = 0; i < bookList.size(); i++) {
            assertThat(bookList.get(i).getId()).isEqualTo(initBookList.get(i).getId());
            assertThat(bookList.get(i).getName()).isEqualTo(initBookList.get(i).getName());
            assertThat(bookList.get(i).getAuthor()).isEqualTo(initBookList.get(i).getAuthor());
            assertThat(bookList.get(i).getYearOfWriting()).isEqualTo(initBookList.get(i).getYearOfWriting());
        }
    }

    @Test
    void getBooksDtoTest() {
        when(bookRepository.findAll(Sort.by(ID_FIELD))).thenReturn(initBookList);

        List<BookDto> bookDtoList = bookService.getBooks(BookDto.class);

        assertThat(bookDtoList).isNotNull().hasSameSizeAs(initBookList);

        for (int i = 0; i < bookDtoList.size(); i++) {
            assertThat(bookDtoList.get(i).getId()).isEqualTo(initBookList.get(i).getId());
            assertThat(bookDtoList.get(i).getName()).isEqualTo(initBookList.get(i).getName());
            assertThat(bookDtoList.get(i).getAuthor()).isEqualTo(initBookList.get(i).getAuthor());
            assertThat(bookDtoList.get(i).getYearOfWriting()).isEqualTo(initBookList.get(i).getYearOfWriting());
        }
    }

    @Test
    void getBookById() {
        when(bookRepository.findById(JAVA_BOOK_ID)).thenReturn(Optional.of(javaBook));
        Book obtainedBookById = bookService.getBookById(JAVA_BOOK_ID);
        assertThat(obtainedBookById)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID_FIELD, JAVA_BOOK_ID);
    }

    @Test
    void getBookByNameAndYearOfWriting() {
        when(bookRepository.findByNameAndAuthorAndYearOfWriting(CLEAN_CODE_BOOK_NAME, CLEAN_CODE_BOOK_AUTHOR, CLEAN_CODE_BOOK_YEAR_OF_WRITING))
                .thenReturn(cleanCodeBook);
        Book obtainedBook = bookService.getBookByNameAndAuthorAndYearOfWriting(
                CLEAN_CODE_BOOK_NAME, CLEAN_CODE_BOOK_AUTHOR, CLEAN_CODE_BOOK_YEAR_OF_WRITING
        );
        assertThat(obtainedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", CLEAN_CODE_BOOK_NAME)
                .hasFieldOrPropertyWithValue("author", CLEAN_CODE_BOOK_AUTHOR)
                .hasFieldOrPropertyWithValue("yearOfWriting", CLEAN_CODE_BOOK_YEAR_OF_WRITING);
    }

    @Test
    void createBookTest() {
        Book book = mock(Book.class);
        bookService.createBook(book);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void createBookDtoTest() {
        BookDto bookDto = mock(BookDto.class);

        when(bookRepository.findAll(Sort.by(ID_FIELD))).thenReturn(initBookList);

        List<BookDto> bookDtoList = bookService.createBook(bookDto);

        verify(bookRepository, times(1)).save(convertToEntity(bookDto, Book.class));
        verify(bookRepository, times(1)).findAll(Sort.by(ID_FIELD));
        assertThat(bookDtoList).isNotNull().hasSize(initBookList.size());
        for (int i = 0; i < bookDtoList.size(); i++) {
            assertThat(bookDtoList.get(i).getId()).isEqualTo(initBookList.get(i).getId());
            assertThat(bookDtoList.get(i).getName()).isEqualTo(initBookList.get(i).getName());
            assertThat(bookDtoList.get(i).getAuthor()).isEqualTo(initBookList.get(i).getAuthor());
            assertThat(bookDtoList.get(i).getYearOfWriting()).isEqualTo(initBookList.get(i).getYearOfWriting());
        }
    }

    @Test
    void updateBookTest() {
        int idOfBookThatGonnaBeUpdated = 1;
        Book updatedBook = new Book().setName(CLEAN_CODE_BOOK_NAME);
        Visitor visitorWhoReadBookFromDB = mock(Visitor.class);
        Book bookFromDB = new Book().setName(JAVA_BOOK_NAME).setWhoRead(visitorWhoReadBookFromDB);

        when(bookRepository.findById(idOfBookThatGonnaBeUpdated)).thenReturn(Optional.of(bookFromDB));

        bookService.updateBook(idOfBookThatGonnaBeUpdated, updatedBook);

        verify(bookRepository, times(1)).save(updatedBook);
        verify(bookRepository, times(1)).findById(idOfBookThatGonnaBeUpdated);
        assertThat(updatedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID_FIELD, idOfBookThatGonnaBeUpdated)
                .hasFieldOrPropertyWithValue("name", CLEAN_CODE_BOOK_NAME)
                .hasFieldOrPropertyWithValue("whoRead", visitorWhoReadBookFromDB);
    }

    @Test
    void handInBook() {
        Visitor visitor = new Visitor().setInitials("William Jefferson Clinton");
        Book book = new Book().setName("Python").setWhoRead(visitor);

        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));

        bookService.handInBook(anyInt());

        verify(bookRepository, times(1)).save(book);
        assertThat(book.getWhoRead()).isNull();
    }

    @Test
    void handInBookDto() {
        int idOfHandedInBook = 1;
        Visitor visitor = new Visitor().setInitials("William Jefferson Clinton");
        Book bookIsGonnaBeHandedIn = new Book().setId(idOfHandedInBook).setName("Python").setWhoRead(visitor);

        when(bookRepository.findById(idOfHandedInBook)).thenReturn(Optional.of(bookIsGonnaBeHandedIn));

        BookDto bookDto = bookService.handInBook(idOfHandedInBook, BookDto.class);

        verify(bookRepository, times(1)).save(bookIsGonnaBeHandedIn);
        verify(bookRepository, times(2)).findById(idOfHandedInBook);
        assertThat(bookDto)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID_FIELD, bookIsGonnaBeHandedIn.getId())
                .hasFieldOrPropertyWithValue("whoRead", null);
    }

    @Test
    void deleteBook() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(mock(Book.class)));

        bookService.deleteBook(anyInt());

        verify(bookRepository, times(1)).deleteById(anyInt());
    }

    @Test()
    void checkThatVisitorCantBeDeletedByNonExistedUser() {
        when(bookRepository.findById(anyInt())).thenThrow(
                new EntityNotFoundException(BOOK_BY_ID_NOT_FOUND.getErrorMessage())
        );

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class, () -> bookService.deleteBook(anyInt())
        );

        verify(bookRepository, never()).deleteById(anyInt());
        assertThat(exception.getMessage()).isEqualTo(BOOK_BY_ID_NOT_FOUND.getErrorMessage());
    }

    @Test
    void assignBookToVisitor() {
        Book book = mock(Book.class);
        int anyInt = anyInt();

        when(bookRepository.findById(anyInt)).thenReturn(Optional.of(book));
        when(visitorService.getVisitorById(anyInt)).thenReturn(mock(Visitor.class));

        bookService.assignBookToVisitor(anyInt, anyInt);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void assignBookToVisitorViaBookDto() {
        int bookId = 1;
        int visitorId = 2;
        Visitor visitor = new Visitor().setId(visitorId).setInitials("William Jefferson Clinton");
        Book book = new Book().setId(bookId).setName("Python");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(visitorService.getVisitorById(visitorId)).thenReturn(visitor);

        BookDto bookDto = bookService.assignBookToVisitor(bookId, visitorId, BookDto.class);

        assertThat(bookDto)
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID_FIELD, bookId)
                .hasFieldOrPropertyWithValue("name", book.getName())
                .hasFieldOrPropertyWithValue("author", book.getAuthor())
                .hasFieldOrPropertyWithValue("yearOfWriting", book.getYearOfWriting());
        assertThat(bookDto.getWhoRead())
                .isNotNull()
                .hasFieldOrPropertyWithValue(ID_FIELD, visitor.getId())
                .hasFieldOrPropertyWithValue("age", visitor.getAge())
                .hasFieldOrPropertyWithValue("initials", visitor.getInitials())
                .hasFieldOrPropertyWithValue("email", visitor.getEmail())
                .hasFieldOrPropertyWithValue("dateOfBirth", visitor.getDateOfBirth());
    }
}