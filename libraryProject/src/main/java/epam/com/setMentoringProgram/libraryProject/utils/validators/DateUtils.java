package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues;
import epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.PATTERN_FOR_BOOK_YEAR_OF_WRITING_VALUE;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.PATTERN_FOR_DATE_OF_BIRTH_VALUE;

public class DateUtils {

    @SneakyThrows
    public static Date validateDate(String dateString, DateFormattingValues formattingValues, ValidationMessages lowerDateRangeException, ValidationMessages higherDateRangeException) {
        try {
            Date dateOfBirth = getDateBySpecificFormat(formattingValues, dateString);
            return validateDateRanges(dateOfBirth, formattingValues, lowerDateRangeException, higherDateRangeException);
        } catch (EntityValidationException ex) {
            throw new EntityValidationException(ex.getMessage());
        }
    }

    public static Date getDateBySpecificFormat(DateFormattingValues dateFormat, String date) {
        String errorMessage = dateFormat.equals(BOOK_DATE_VALUES)
                            ? PATTERN_FOR_BOOK_YEAR_OF_WRITING_VALUE.getErrorMessage()
                            : PATTERN_FOR_DATE_OF_BIRTH_VALUE.getErrorMessage();
        try {
            return new SimpleDateFormat(dateFormat.getDateCreatingPattern()).parse(date);
        } catch (ParseException ex) {
            throw new EntityValidationException(errorMessage);
        }
    }

    @SneakyThrows
    public static Date  validateDateRanges(Date dateOfBirth, DateFormattingValues formattingValues, ValidationMessages lowerDateRangeException, ValidationMessages higherDateRangeException) {
        if(dateOfBirth.before(new SimpleDateFormat(formattingValues.getDateCreatingPattern()).parse(formattingValues.getLowerDateRange()))) {
            throw new EntityValidationException(lowerDateRangeException.getErrorMessage());
        } else if(dateOfBirth.after(new Date())) {
            throw new EntityValidationException(higherDateRangeException.getErrorMessage());
        } else {
            return dateOfBirth;
        }
    }

    public static String getDateBySpecificFormatAsString(String dateFormat, Date date) {
        return new SimpleDateFormat(dateFormat).format(date);
    }
}
