package epam.com.setMentoringProgram.libraryProject.controllers;

import epam.com.setMentoringProgram.libraryProject.dto.BookDto;
import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityErrorResponse;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import epam.com.setMentoringProgram.libraryProject.utils.validators.BookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ValidatorUtils.validateEntityFields;

@RestController
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BookService bookService, BookValidator bookValidator) {
        this.bookService = bookService;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public List<BookDto> getBooks() {
        List<Book> booksList = bookService.getBooks().stream().peek(book -> book.setWhoRead(null)).collect(Collectors.toList());
        return booksList.stream().map(book -> convertToEntity(book, BookDto.class)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable("id") int bookId) {
        return convertToEntity(bookService.getBookById(bookId), BookDto.class);
    }

    @PostMapping("/new")
    public ResponseEntity<List<BookDto>> createBook(@RequestBody @Valid BookDto bookDto, BindingResult bindingResult) {
        bookValidator.validate(bookDto, bindingResult);
        validateEntityFields(bindingResult);
        bookService.createBook(convertToEntity(bookDto, Book.class));
        List<Book> booksList = bookService.getBooks().stream().peek(book -> book.setWhoRead(null)).collect(Collectors.toList());
        List<BookDto> bookDtoList = booksList.stream().map(book -> convertToEntity(book, BookDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(bookDtoList, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDto> updateBook(@RequestBody @Valid BookDto bookDto,
                                              BindingResult bindingResult,
                                              @PathVariable("id") int bookId) {
        bookValidator.validate(bookDto, bindingResult);
        validateEntityFields(bindingResult);
        bookService.updateBook(bookId, convertToEntity(bookDto, Book.class));
        BookDto updatedBook = convertToEntity(bookService.getBookById(bookId), BookDto.class);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @PatchMapping("/assign/{id}")
    public ResponseEntity<BookDto> assignBookToVisitor(@RequestBody VisitorDto visitorDto, @PathVariable("id") int bookId) {
        bookService.assignBookToVisitor(bookId, visitorDto.getId());
        BookDto updatedBook = convertToEntity(bookService.getBookById(bookId), BookDto.class);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @PatchMapping("/handIn/{id}")
    public ResponseEntity<BookDto> handInBook(@PathVariable("id") int bookId) {
        bookService.handInBook(bookId);
        BookDto updatedBook = convertToEntity(bookService.getBookById(bookId), BookDto.class);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") int id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<EntityErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        EntityErrorResponse entityErrorResponse = new EntityErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                exception.getMessage()
        );
        return new ResponseEntity<>(entityErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<EntityErrorResponse> handleEntityNotCreatedException(EntityValidationException exception) {
        EntityErrorResponse exceptionResponse = new EntityErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                exception.getMessage().split("\\.")
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
