package epam.com.setMentoringProgram.libraryProject;

import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import org.junit.jupiter.api.BeforeAll;

import java.util.Date;
import java.util.List;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;

public abstract class BaseVisitorAbstractTest extends BaseTest {

    private static final String PATH_TO_VISITORS_CONTROLLER = "/visitors";

    protected static List<Visitor> initVisitorList;
    protected static Visitor steveVisitor;
    protected static Visitor williamVisitor;

    protected static final int STEVE_ID = 7;
    protected static final int STEVE_AGE = 43;
    protected static final String STEVE_INITIALS = "Steven Paul Jobs";
    protected static final String STEVE_EMAIL = "steveJobs@gmail.com";
    protected static final Date STEVE_DATE_OF_BIRTH = getDateBySpecificFormat(VISITOR_DATE_VALUES.getDateCreatingPattern(), "22-03-1987");

    protected static final int WILLIAM_ID = 9;
    protected static final int WILLIAM_AGE = 57;
    protected static final String WILLIAM_INITIALS = "William Clay Ford";
    protected static final String WILLIAM_EMAIL = "williamFord@gmail.com";
    protected static final Date WILLIAM_DATE_OF_BIRTH = getDateBySpecificFormat(VISITOR_DATE_VALUES.getDateCreatingPattern(), "17-06-1937");

    @BeforeAll
    public static void setUp() {
        steveVisitor = new Visitor().setId(STEVE_ID).setAge(STEVE_AGE).setInitials(STEVE_INITIALS)
                .setEmail(STEVE_EMAIL).setDateOfBirth(STEVE_DATE_OF_BIRTH);
        williamVisitor = new Visitor().setId(WILLIAM_ID).setAge(WILLIAM_AGE).setInitials(WILLIAM_INITIALS)
                .setEmail(WILLIAM_EMAIL).setDateOfBirth(WILLIAM_DATE_OF_BIRTH);
        initVisitorList = List.of(steveVisitor, williamVisitor);
    }

    protected String getLinkForGettingVisitors() {
        return PATH_TO_VISITORS_CONTROLLER;
    }

    protected String getLinkForGettingVisitorById(int visitorIdThatIsGonnaBeGotFromBD) {
        return String.format("%s/%s", PATH_TO_VISITORS_CONTROLLER, visitorIdThatIsGonnaBeGotFromBD);
    }

    protected String getLinkForCreatingVisitor() {
        return String.format("%s/new", PATH_TO_VISITORS_CONTROLLER);
    }

    protected String getLinkForUpdatingVisitor(int visitorIdThatIsGonnaBeUpdated) {
        return String.format("%s/update/%s", PATH_TO_VISITORS_CONTROLLER, visitorIdThatIsGonnaBeUpdated);
    }

    protected String getLinkForDeletingVisitor(int visitorIdThatIsGonnaBeDeleted) {
        return String.format("%s/delete/%s", PATH_TO_VISITORS_CONTROLLER, visitorIdThatIsGonnaBeDeleted);
    }

}
