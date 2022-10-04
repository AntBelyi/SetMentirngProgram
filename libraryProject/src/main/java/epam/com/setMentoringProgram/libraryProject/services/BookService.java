package epam.com.setMentoringProgram.libraryProject.services;

import epam.com.setMentoringProgram.libraryProject.dto.BookDto;
import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.repositories.BookRepository;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.BOOK_BY_ID_NOT_FOUND;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final VisitorService visitorService;
    private static final String FIELD_FOR_SORTING = "id";

    @Autowired
    public BookService(BookRepository bookRepository, VisitorService visitorService) {
        this.bookRepository = bookRepository;
        this.visitorService = visitorService;
    }

    public List<Book> getBooks() {
        return bookRepository.findAll(Sort.by(FIELD_FOR_SORTING)).stream().peek(book -> book.setWhoRead(null)).collect(Collectors.toList());
    }

    public List<BookDto> getBooks(Class<BookDto> bookDtoClass) {
        return getBooks().stream().map(book -> convertToEntity(book, bookDtoClass)).collect(Collectors.toList());
    }

    public List<BookDto> getBooks(int page, int countOfBooksToDisplay, Class<BookDto> bookDtoClass) {
        return bookRepository.findAll(PageRequest.of(page, countOfBooksToDisplay, Sort.by(FIELD_FOR_SORTING)))
                .stream().peek(book -> book.setWhoRead(null))
                .map(book -> convertToEntity(book, bookDtoClass)).collect(Collectors.toList());
    }

    public Book getBookById(int bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(BOOK_BY_ID_NOT_FOUND.getErrorMessage()));
        Hibernate.initialize(book.getWhoRead());
        return book;
    }

    public Book getBookByNameAndYearOfWriting(String name, String author, Date yearOfWriting) {
        return bookRepository.findByNameAndAuthorAndYearOfWriting(name, author, yearOfWriting);
    }

    @Transactional
    public void createBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public List<BookDto> createBook(BookDto bookDto) {
        createBook(convertToEntity(bookDto, Book.class));
        List<Book> booksList = getBooks().stream().peek(book -> book.setWhoRead(null)).collect(Collectors.toList());
        return booksList.stream().map(book -> convertToEntity(book, BookDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public void updateBook(int updatedBookId, Book book) {
        Visitor visitorWhoRead = getBookById(updatedBookId).getWhoRead();
        if(Objects.nonNull(visitorWhoRead)) {
            book.setWhoRead(visitorWhoRead);
        }
        book.setId(updatedBookId);
        bookRepository.save(book);
    }

    @Transactional
    public BookDto updateBook(int updatedBookId, BookDto bookDto) {
        updateBook(updatedBookId, convertToEntity(bookDto, Book.class));
        return convertToEntity(getBookById(updatedBookId), BookDto.class);
    }

    @Transactional
    public void handInBook(int bookId) {
        Book book = getBookById(bookId);
        book.setWhoRead(null);
        bookRepository.save(book);
    }

    @Transactional
    public BookDto handInBook(int bookId, Class<BookDto> bookDtoClazz) {
        handInBook(bookId);
        return convertToEntity(getBookById(bookId), bookDtoClazz);
    }

    @Transactional
    public void deleteBook(int id) {
        if(Objects.nonNull(getBookById(id))) {
            bookRepository.deleteById(id);
        }
    }

    @Transactional
    public void assignBookToVisitor(int bookId, int visitorId) {
        Book book = getBookById(bookId);
        Visitor visitorToBeAssigned = visitorService.getVisitorById(visitorId);
        book.setWhoRead(visitorToBeAssigned);
        visitorToBeAssigned.addBook(book);
        bookRepository.save(book);
    }

    @Transactional
    public BookDto assignBookToVisitor(int bookId, int visitorId, Class<BookDto> bookDtoClazz) {
        assignBookToVisitor(bookId, visitorId);
        return convertToEntity(getBookById(bookId), bookDtoClazz);
    }
}
