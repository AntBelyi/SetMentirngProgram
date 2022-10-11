package epam.com.setMentoringProgram.libraryProject.utils.validators;

import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.PATTERN_FOR_DATE_OF_BIRTH_VALUE;

public class DateUtils {
    public static Date getDateBySpecificFormat(String dateFormat, String date) {
        try {
            return new SimpleDateFormat(dateFormat).parse(date);
        } catch (ParseException ex) {
            throw new EntityValidationException(PATTERN_FOR_DATE_OF_BIRTH_VALUE.getErrorMessage());
        }
    }

    public static String getDateBySpecificFormatAsString(String dateFormat, Date date) {
        return new SimpleDateFormat(dateFormat).format(date);
    }
}
