package epam.com.setMentoringProgram.libraryProject.integrationTests;

import epam.com.setMentoringProgram.libraryProject.BaseBookAbstractTest;
import epam.com.setMentoringProgram.libraryProject.dto.BookDto;
import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.repositories.BookRepository;
import epam.com.setMentoringProgram.libraryProject.repositories.VisitorRepository;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.BOOK_BY_ID_NOT_FOUND;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.getJsonFromObject;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormatAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-integration-test.properties")
@SqlGroup({
        @Sql(value = {"classpath:h2FillOutDBWithData.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"classpath:h2ClearFixturesCommand.sql"}, executionPhase = AFTER_TEST_METHOD)
})
class BooksControllerTest extends BaseBookAbstractTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void mockMvcSetUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getBooks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingBooks()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getBooksViaPaginationTest() throws Exception {
        int page = 1;
        int countOfItems = 1;
        Book lastBookFromDB = bookRepository.findAll().stream().reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting books from h2 DB"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingBooks())
                        .param("page", String.valueOf(page))
                        .param("countOfItems", String.valueOf(countOfItems)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(lastBookFromDB.getId())))
                .andExpect(jsonPath("$.[0].name", is(lastBookFromDB.getName())))
                .andExpect(jsonPath("$.[0].author", is(lastBookFromDB.getAuthor())))
                .andExpect(jsonPath("$.[0].yearOfWriting", is(getDateBySpecificFormatAsString(BOOK_DATE_VALUES.getDateCreatingPattern(), lastBookFromDB.getYearOfWriting()))));
    }

    @Test
    void getBookByIdTest() throws Exception {
        Book bookFromDBThatIsGonnaBeGot = bookRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting books from h2 DB"));
        int bookIdThatIsGonnaBeGotFromBD = bookFromDBThatIsGonnaBeGot.getId();

        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingBookById(bookIdThatIsGonnaBeGotFromBD)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", is(bookIdThatIsGonnaBeGotFromBD)))
                .andExpect(jsonPath("$.name", is(bookFromDBThatIsGonnaBeGot.getName())))
                .andExpect(jsonPath("$.author", is(bookFromDBThatIsGonnaBeGot.getAuthor())))
                .andExpect(jsonPath("$.yearOfWriting", is(getDateBySpecificFormatAsString(BOOK_DATE_VALUES.getDateCreatingPattern(), bookFromDBThatIsGonnaBeGot.getYearOfWriting()))));
    }

    @Test
    void createBookTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(getLinkForCreatingBook())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(javaBook.setId(null))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));

        assertThat(bookRepository.findByNameAndAuthorAndYearOfWriting(JAVA_BOOK_NAME, JAVA_BOOK_AUTHOR, JAVA_BOOK_YEAR_OF_WRITING).setWhoRead(null))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", JAVA_BOOK_NAME)
                .hasFieldOrPropertyWithValue("author", JAVA_BOOK_AUTHOR)
                .hasFieldOrPropertyWithValue("yearOfWriting", JAVA_BOOK_YEAR_OF_WRITING);
    }

    @Test
    void updateBookTest() throws Exception {
        int bookIdThatIsGonnaBeUpdated = bookRepository.findAll().stream().reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting books from h2 DB")).getId();

        Book newBookForUpdating = new Book().setName("Thinking in Java").setAuthor("Bruce Eckel")
                .setYearOfWriting(getDateBySpecificFormat(BOOK_DATE_VALUES.getDateCreatingPattern(), "2013"));

        mockMvc.perform(MockMvcRequestBuilders
                        .put(getLinkForUpdatingBook(bookIdThatIsGonnaBeUpdated))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(newBookForUpdating)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookIdThatIsGonnaBeUpdated)))
                .andExpect(jsonPath("$.name", is(newBookForUpdating.getName())))
                .andExpect(jsonPath("$.author", is(newBookForUpdating.getAuthor())))
                .andExpect(jsonPath("$.yearOfWriting", is(getDateBySpecificFormatAsString(BOOK_DATE_VALUES.getDateCreatingPattern(), newBookForUpdating.getYearOfWriting()))));

        assertThat(bookRepository.findById(bookIdThatIsGonnaBeUpdated).orElseThrow(() -> new EntityNotFoundException(BOOK_BY_ID_NOT_FOUND.getErrorMessage())))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", bookIdThatIsGonnaBeUpdated)
                .hasFieldOrPropertyWithValue("author", newBookForUpdating.getAuthor())
                .hasFieldOrPropertyWithValue("yearOfWriting", newBookForUpdating.getYearOfWriting());
    }

    @Test
    void assignBookToVisitorTest() throws Exception {
        int bookIdThatIsGonnaBeAssigned = bookRepository.findAll().stream().reduce((first, second) -> second)
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting books from h2 DB")).getId();
        int visitorIdThatIsGonnaReadTheBook = visitorRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting visitors from h2 DB")).getId();

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(getLinkForAssigningBookToVisitor(bookIdThatIsGonnaBeAssigned))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(new VisitorDto().setId(visitorIdThatIsGonnaReadTheBook))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(bookIdThatIsGonnaBeAssigned)))
                .andExpect(jsonPath("$.whoRead.id", is(visitorIdThatIsGonnaReadTheBook)));
    }

    @Test
    void deleteBookTest() throws Exception {
        int bookIdThatIsGonnaBeDeleted = bookRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Something's come up during getting books from h2 DB")).getId();

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(getLinkForDeletingBook(bookIdThatIsGonnaBeDeleted)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        List<Book> booksFromDBAfterDeletingOneBook = bookRepository.findAll();

        assertThat(booksFromDBAfterDeletingOneBook).isNotNull().hasSize(1);
        assertThat(booksFromDBAfterDeletingOneBook.get(0).getId()).isNotEqualTo(bookIdThatIsGonnaBeDeleted);
    }

    @Test
    void handInBookTest() throws Exception {
        int visitorAge = 23;
        String visitorInitials = "William Jefferson Clinton";
        String visitorEmail = "clinton@gmail.com";
        Date visitorDateOfBirth = getDateBySpecificFormat(VISITOR_DATE_VALUES.getDateCreatingPattern(), "21-09-1994");
        Visitor visitor = new Visitor().setAge(visitorAge).setInitials(visitorInitials).setEmail(visitorEmail).setDateOfBirth(visitorDateOfBirth);

        String bookName = "Just book";
        String bookAuthor = "Just Author";
        Date bookYearOfWriting = getDateBySpecificFormat(BOOK_DATE_VALUES.getDateCreatingPattern(), "2013");
        BookDto bookDto = new BookDto().setName(bookName).setAuthor(bookAuthor).setYearOfWriting(bookYearOfWriting);

        visitorRepository.save(visitor);
        int createdVisitorId = visitorRepository.findByInitialsAndDateOfBirth(visitorInitials, visitorDateOfBirth).getId();

        bookDto.setWhoRead(visitor.setId(createdVisitorId));

        bookRepository.save(convertToEntity(bookDto, Book.class));

        int bookId = bookRepository.findByNameAndAuthorAndYearOfWriting(bookName, bookAuthor, bookYearOfWriting).getId();

        Book book = bookService.getBookById(bookId);

        assertThat(book.getWhoRead())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", createdVisitorId)
                .hasFieldOrPropertyWithValue("age", visitorAge)
                .hasFieldOrPropertyWithValue("initials", visitorInitials)
                .hasFieldOrPropertyWithValue("email", visitorEmail)
                .hasFieldOrPropertyWithValue("dateOfBirth", visitorDateOfBirth);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(getLinkForHandingInBook(bookId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookId)))
                .andExpect(jsonPath("$.whoRead").doesNotExist());
    }
}