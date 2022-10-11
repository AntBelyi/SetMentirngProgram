package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.getDateBySpecificFormat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitorValidatorTest extends BaseValidatorTestClass {

    @Mock
    private VisitorService visitorService;

    @InjectMocks
    private VisitorValidator visitorValidator;

    private static final VisitorDto VISITOR_DTO = mock(VisitorDto.class);
    private static final String VISITOR_INITIALS = "Franko Ivan Yakovlevich";
    private static final Date DATE_OF_BIRTH = getDateBySpecificFormat(VISITOR_DATE_VALUES.getDateCreatingPattern(), "17-01-1997");

    @BeforeAll
    public static void setUp() {
        when(VISITOR_DTO.getInitials()).thenReturn(VISITOR_INITIALS);
        when(VISITOR_DTO.getDateOfBirth()).thenReturn(DATE_OF_BIRTH);
    }

    @Test
    public void checkThatVisitorValidatorCanWorkWithVisitorClass() {
        boolean isVisitorClassValidated = visitorValidator.supports(Visitor.class);
        assertThat(isVisitorClassValidated).isTrue();
    }

    @Test
    public void checkThatVisitorValidatorCantWorkWithNotVisitorClass() {
        boolean isVisitorClassValidatorWorksWithVisitorDtoClass = visitorValidator.supports(VisitorDto.class);
        boolean isVisitorClassValidatorWorksWithBookClass = visitorValidator.supports(Book.class);
        assertThat(isVisitorClassValidatorWorksWithVisitorDtoClass).isFalse();
        assertThat(isVisitorClassValidatorWorksWithBookClass).isFalse();
    }

    @Test
    public void checkVisitorCanBeCreatedAsItDoesntExistYet() {
        when(visitorService.getVisitorByInitialsAndDateOfBirth(VISITOR_INITIALS, DATE_OF_BIRTH)).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> visitorValidator.validate(VISITOR_DTO, ERRORS));
    }

    @Test
    public void checkVisitorCantBeCreatedAsItAlreadyExists() {
        when(visitorService.getVisitorByInitialsAndDateOfBirth(VISITOR_INITIALS, DATE_OF_BIRTH)).thenReturn(mock(Visitor.class));

        Assertions.assertThrows(EntityValidationException.class, () -> visitorValidator.validate(VISITOR_DTO, ERRORS));
    }
}