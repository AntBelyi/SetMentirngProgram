package epam.com.setMentoringProgram.libraryProject.unitTests.controllers;

import epam.com.setMentoringProgram.libraryProject.BaseBookAbstractTest;
import epam.com.setMentoringProgram.libraryProject.controllers.api.ApiBooksController;
import epam.com.setMentoringProgram.libraryProject.dto.BookDto;
import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.utils.validators.BookValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.getJsonFromObject;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormatAsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiBooksController.class)
class BooksControllerTest extends BaseBookAbstractTest {

    @MockBean
    private BookService bookService;

    @MockBean
    private BookValidator bookValidator;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static final List<BookDto> initBookDtoList = initBookList.stream()
            .map(book -> convertToEntity(book, BookDto.class)).collect(Collectors.toList());

    private static final BookDto cleanCodeBookDto = convertToEntity(cleanCodeBook, BookDto.class);

    private static final BookDto javaBookDto = convertToEntity(javaBook, BookDto.class);

    @BeforeEach
    public void mockMvcSetUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getBooks() throws Exception {
        when(bookService.getBooks(BookDto.class)).thenReturn(initBookDtoList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingBooks()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(initBookDtoList.size())))
                .andExpectAll(jsonPath("$.[0].id", is(CLEAN_CODE_BOOK_ID)));

        verify(bookService, times(1)).getBooks(BookDto.class);
    }

    @Test
    void getBooksWithPagination() throws Exception {
        int page = 1;
        int countOfItems = 2;
        when(bookService.getBooks(page, countOfItems, BookDto.class)).thenReturn(initBookDtoList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingBooks())
                        .param("page", String.valueOf(page))
                        .param("countOfItems", String.valueOf(countOfItems)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(initBookDtoList.size())))
                .andExpectAll(jsonPath("$.[0].id", is(CLEAN_CODE_BOOK_ID)))
                .andExpectAll(jsonPath("$.[1].id", is(JAVA_BOOK_ID)));

        verify(bookService, times(1)).getBooks(page, countOfItems, BookDto.class);
    }

    @Test
    void getBookById() throws Exception {
        when(bookService.getBookById(anyInt())).thenReturn(convertToEntity(javaBookDto, Book.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(getLinkForGettingBookById(JAVA_BOOK_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(JAVA_BOOK_ID)))
                .andExpect(jsonPath("$.name", is(JAVA_BOOK_NAME)))
                .andExpect(jsonPath("$.author", is(JAVA_BOOK_AUTHOR)))
                .andExpect(jsonPath("$.yearOfWriting", is(getDateBySpecificFormatAsString(BOOK_DATE_VALUES.getDateCreatingPattern(), JAVA_BOOK_YEAR_OF_WRITING))));

        verify(bookService, times(1)).getBookById(anyInt());
    }

    @Test
    void createBook() throws Exception {
        when(bookService.createBook(javaBookDto)).thenReturn(initBookDtoList);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(getLinkForCreatingBook())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(javaBookDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(initBookDtoList.size())))
                .andExpect(jsonPath("$.[1].id", is(JAVA_BOOK_ID)))
                .andExpect(jsonPath("$.[1].name", is(JAVA_BOOK_NAME)))
                .andExpect(jsonPath("$.[1].author", is(JAVA_BOOK_AUTHOR)))
                .andExpect(jsonPath("$.[1].yearOfWriting", is(getDateBySpecificFormatAsString(BOOK_DATE_VALUES.getDateCreatingPattern(), JAVA_BOOK_YEAR_OF_WRITING))));
        verify(bookService, times(1)).createBook(javaBookDto);
    }

    @Test
    void updateBook() throws Exception {
        BookDto updatedBookDto = new BookDto().setName("Python").setAuthor("Just Author")
                .setYearOfWriting(getDateBySpecificFormat(BOOK_DATE_VALUES, "2005"));
        when(bookService.updateBook(CLEAN_CODE_BOOK_ID, updatedBookDto)).thenReturn(cleanCodeBookDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(getLinkForUpdatingBook(CLEAN_CODE_BOOK_ID))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(updatedBookDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(CLEAN_CODE_BOOK_ID)))
                .andExpect(jsonPath("$.name", is(CLEAN_CODE_BOOK_NAME)))
                .andExpect(jsonPath("$.author", is(CLEAN_CODE_BOOK_AUTHOR)))
                .andExpect(jsonPath("$.yearOfWriting", is(getDateBySpecificFormatAsString(BOOK_DATE_VALUES.getDateCreatingPattern(), CLEAN_CODE_BOOK_YEAR_OF_WRITING))));

        verify(bookService, times(1)).updateBook(CLEAN_CODE_BOOK_ID, updatedBookDto);
    }

    @Test
    void assignBookToVisitor() throws Exception {
        int visitorWhoReadId = 2;
        Visitor visitorFromDbById = new Visitor().setId(visitorWhoReadId).setAge(27)
                .setInitials("William Jefferson Clinton").setEmail("clinton@gmail.com")
                .setDateOfBirth(getDateBySpecificFormat(VISITOR_DATE_VALUES, "23-06-2020"));
        BookDto bookIsGonnaBeAssigned = new BookDto().setId(3).setName("Python").setAuthor("Just Author")
                .setYearOfWriting(getDateBySpecificFormat(BOOK_DATE_VALUES, "2005"));
        bookIsGonnaBeAssigned.setWhoRead(visitorFromDbById);

        when(bookService.assignBookToVisitor(JAVA_BOOK_ID, visitorWhoReadId, BookDto.class))
                .thenReturn(bookIsGonnaBeAssigned);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(getLinkForAssigningBookToVisitor(JAVA_BOOK_ID))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(getJsonFromObject(new VisitorDto().setId(visitorWhoReadId))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookIsGonnaBeAssigned.getId())))
                .andExpect(jsonPath("$.name", is(bookIsGonnaBeAssigned.getName())))
                .andExpect(jsonPath("$.author", is(bookIsGonnaBeAssigned.getAuthor())))
                .andExpect(jsonPath("$.yearOfWriting", is(getDateBySpecificFormatAsString(BOOK_DATE_VALUES.getDateCreatingPattern(), bookIsGonnaBeAssigned.getYearOfWriting()))))
                .andExpect(jsonPath("$.whoRead.id", is(visitorFromDbById.getId())))
                .andExpect(jsonPath("$.whoRead.age", is(visitorFromDbById.getAge())))
                .andExpect(jsonPath("$.whoRead.initials", is(visitorFromDbById.getInitials())))
                .andExpect(jsonPath("$.whoRead.email", is(visitorFromDbById.getEmail())))
                .andExpect(jsonPath("$.whoRead.dateOfBirth", is(getDateBySpecificFormatAsString(VISITOR_DATE_VALUES.getDateCreatingPattern(), visitorFromDbById.getDateOfBirth()))));

    }

    @Test
    void handInBook() throws Exception {
        when(bookService.handInBook(JAVA_BOOK_ID, BookDto.class)).thenReturn(javaBookDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch(getLinkForHandingInBook(JAVA_BOOK_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(JAVA_BOOK_ID)))
                .andExpect(jsonPath("$.name", is(JAVA_BOOK_NAME)))
                .andExpect(jsonPath("$.author", is(JAVA_BOOK_AUTHOR)))
                .andExpect(jsonPath("$.yearOfWriting", is(getDateBySpecificFormatAsString(BOOK_DATE_VALUES.getDateCreatingPattern(), JAVA_BOOK_YEAR_OF_WRITING))));
    }

    @Test
    void deleteBook() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(getLinkForDeletingBook(CLEAN_CODE_BOOK_ID)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}