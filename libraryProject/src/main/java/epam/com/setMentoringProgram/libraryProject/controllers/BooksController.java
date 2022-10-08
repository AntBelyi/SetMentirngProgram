package epam.com.setMentoringProgram.libraryProject.controllers;

import epam.com.setMentoringProgram.libraryProject.dto.BookDto;
import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.utils.validators.BookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;

@RestController
@RequestMapping("/books")
public class BooksController extends BaseController {

    private final BookService bookService;
    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BookService bookService, BookValidator bookValidator) {
        this.bookService = bookService;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public List<BookDto> getBooks() {
        return bookService.getBooks(BookDto.class);
    }

    @GetMapping(params = {"page", "countOfItems"})
    public List<BookDto> getBooks(@RequestParam(value = "page") int page,
                                  @RequestParam(value = "countOfItems") int countOfItems) {
        return bookService.getBooks(page, countOfItems, BookDto.class);
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable("id") int bookId) {
        return convertToEntity(bookService.getBookById(bookId), BookDto.class);
    }

    @PostMapping("/new")
    public ResponseEntity<List<BookDto>> createBook(@RequestBody @Valid BookDto bookDto, BindingResult bindingResult) {
        validateEntity(bindingResult, bookDto, bookValidator);
        List<BookDto> bookDtoList = bookService.createBook(bookDto);
        return new ResponseEntity<>(bookDtoList, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDto> updateBook(@RequestBody @Valid BookDto bookDto, BindingResult bindingResult,
                                              @PathVariable("id") int bookId) {
        validateEntity(bindingResult, bookDto, bookValidator);
        BookDto updatedBook = bookService.updateBook(bookId, bookDto);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @PatchMapping("/assign/{id}")
    public ResponseEntity<BookDto> assignBookToVisitor(@RequestBody VisitorDto visitorDto, @PathVariable("id") int bookId) {
        BookDto updatedBook = bookService.assignBookToVisitor(bookId, visitorDto.getId(), BookDto.class);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @PatchMapping("/handIn/{id}")
    public ResponseEntity<BookDto> handInBook(@PathVariable("id") int bookId) {
        BookDto updatedBook = bookService.handInBook(bookId, BookDto.class);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") int id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
