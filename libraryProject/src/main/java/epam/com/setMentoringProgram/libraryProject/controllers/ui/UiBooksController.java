package epam.com.setMentoringProgram.libraryProject.controllers.ui;

import epam.com.setMentoringProgram.libraryProject.dto.BookDto;
import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.services.BookService;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.validators.BookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;

@Controller
@RequestMapping("/ui/books")
public class UiBooksController extends BaseUiController {

    private final BookService bookService;
    private final VisitorService visitorService;
    private final BookValidator bookValidator;

    @Autowired
    public UiBooksController(BookService bookService, VisitorService visitorService, BookValidator bookValidator) {
        this.bookService = bookService;
        this.visitorService = visitorService;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public String getBooks(Model model) {
        model.addAttribute("books", bookService.getBooks(BookDto.class));
        return "books/showAllBooksPage";
    }

    @GetMapping("/{id}")
    public String getBookById(@ModelAttribute("emptyVisitorObject") VisitorDto visitorDto,
                              @PathVariable("id") int id,
                              Model model) {
        model.addAttribute("book", convertToEntity(bookService.getBookById(id), BookDto.class));
        model.addAttribute("visitors", visitorService.getVisitors(VisitorDto.class));
        return "books/showBookByIdPage";
    }

    @GetMapping("/new")
    public String getFormForCreatingBook(@ModelAttribute("book") BookDto bookDto) {
        return "books/createBookPage";
    }

    @PostMapping()
    public String addBookToDBS(@ModelAttribute("book") @Valid BookDto bookDto, BindingResult bindingResult, Model model) {
        if(validateEntity(bindingResult, bookDto, bookValidator, model)) {
            return "books/createBookPage";
        } else {
            bookService.createBook(bookDto);
            return "redirect:/ui/books";
        }
    }

    @GetMapping("/update/{id}")
    public String getFormForUpdatingBook(@PathVariable("id") int id, Model model) {
        model.addAttribute("bookById", convertToEntity(bookService.getBookById(id), BookDto.class));
        return "books/updateBookPage";
    }

    @PutMapping("/update/{id}")
    public String updateBook(@ModelAttribute("bookById") @Valid BookDto bookDto,
                             BindingResult bindingResult,
                             Model model,
                             @PathVariable("id") int bookId) {
        if(validateEntity(bindingResult, bookDto, bookValidator, model)) {
           return "books/updateBookPage";
        } else {
            bookService.updateBook(bookId, bookDto);
            return "redirect:/ui/books";
        }
    }

    @PatchMapping("/assign/{id}")
    public String assignBookToVisitor(VisitorDto visitorDto, @PathVariable("id") int bookId) {
        bookService.assignBookToVisitor(bookId, visitorDto.getId(), BookDto.class);
        return "redirect:/ui/books";
    }

    @PatchMapping("/handIn/{id}")
    public String handInBook(@PathVariable("id") int bookId) {
        bookService.handInBook(bookId, BookDto.class);
        return "redirect:/ui/books";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookService.deleteBook(id);
        return "redirect:/ui/books";
    }

}
