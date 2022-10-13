package epam.com.setMentoringProgram.libraryProject;

import epam.com.setMentoringProgram.libraryProject.models.Book;
import org.junit.jupiter.api.BeforeAll;

import java.util.Date;
import java.util.List;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;

public abstract class BaseBookAbstractTest extends BaseTest {

    private static final String PATH_TO_BOOKS_CONTROLLER = "/books";

    protected static List<Book> initBookList;
    protected static Book cleanCodeBook;
    protected static Book javaBook;

    protected static final int CLEAN_CODE_BOOK_ID = 1;
    protected static final String CLEAN_CODE_BOOK_NAME = "Clean code";
    protected static final String CLEAN_CODE_BOOK_AUTHOR = "Robert Martin";
    protected static final Date CLEAN_CODE_BOOK_YEAR_OF_WRITING = getDateBySpecificFormat(BOOK_DATE_VALUES.getDateCreatingPattern(), "2009");

    protected static final int JAVA_BOOK_ID = 2;
    protected static final String JAVA_BOOK_NAME = "Java";
    protected static final String JAVA_BOOK_AUTHOR = "Joshua Bloch";
    protected static final Date JAVA_BOOK_YEAR_OF_WRITING = getDateBySpecificFormat(BOOK_DATE_VALUES.getDateCreatingPattern(), "2005");

    @BeforeAll
    public static void setUp() {
        cleanCodeBook = new Book().setId(CLEAN_CODE_BOOK_ID).setName(CLEAN_CODE_BOOK_NAME).setAuthor(CLEAN_CODE_BOOK_AUTHOR)
                .setYearOfWriting(CLEAN_CODE_BOOK_YEAR_OF_WRITING);
        javaBook = new Book().setId(JAVA_BOOK_ID).setName(JAVA_BOOK_NAME).setAuthor(JAVA_BOOK_AUTHOR)
                .setYearOfWriting(JAVA_BOOK_YEAR_OF_WRITING);
        initBookList = List.of(cleanCodeBook, javaBook);
    }

    protected String getLinkForGettingBooks() {
        return PATH_TO_BOOKS_CONTROLLER;
    }

    protected String getLinkForGettingBookById(int bookIdThatIsGonnaBeGotFromBD) {
        return String.format("%s/%s", PATH_TO_BOOKS_CONTROLLER, bookIdThatIsGonnaBeGotFromBD);
    }

    protected String getLinkForCreatingBook() {
        return String.format("%s/new", PATH_TO_BOOKS_CONTROLLER);
    }

    protected String getLinkForUpdatingBook(int bookIdThatIsGonnaBeUpdated) {
        return String.format("%s/update/%s", PATH_TO_BOOKS_CONTROLLER, bookIdThatIsGonnaBeUpdated);
    }

    protected String getLinkForAssigningBookToVisitor(int bookIdThatIsGonnaBeAssigned) {
        return String.format("%s/assign/%s", PATH_TO_BOOKS_CONTROLLER, bookIdThatIsGonnaBeAssigned);
    }

    protected String getLinkForDeletingBook(int bookIdThatIsGonnaBeDeleted) {
        return String.format("%s/delete/%s", PATH_TO_BOOKS_CONTROLLER, bookIdThatIsGonnaBeDeleted);
    }

    protected String getLinkForHandingInBook(int bookId) {
        return String.format("%s/handIn/%s", PATH_TO_BOOKS_CONTROLLER, bookId);
    }

}
