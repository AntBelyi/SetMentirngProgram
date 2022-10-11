package epam.com.setMentoringProgram.libraryProject.utils.validators;

import org.springframework.validation.Errors;

import static org.mockito.Mockito.mock;

public abstract class BaseValidatorTestClass {
    protected static final Errors ERRORS = mock(Errors.class);
}
