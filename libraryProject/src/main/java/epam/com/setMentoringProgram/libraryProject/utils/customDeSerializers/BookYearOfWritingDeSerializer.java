package epam.com.setMentoringProgram.libraryProject.utils.customDeSerializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.*;

public class BookYearOfWritingDeSerializer extends StdDeserializer<Date> {

    protected BookYearOfWritingDeSerializer() {
        super(Date.class);
    }

    @SneakyThrows
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        String value = jsonParser.readValueAs(String.class);
        try {
            Date yearOfWriting = new SimpleDateFormat(BOOK_DATE_VALUES.getDateCreatingPattern()).parse(value);
            if(yearOfWriting.before(new SimpleDateFormat(BOOK_DATE_VALUES.getDateCreatingPattern()).parse(BOOK_DATE_VALUES.getLowerDateRange()))) {
                throw new EntityValidationException(BOOK_YEAR_OF_WRITING_CANT_BE_GREATER_THAN_1000.getErrorMessage());
            } else if (yearOfWriting.after(new Date())) {
                throw new EntityValidationException(BOOK_YEAR_OF_WRITING_CANT_BE_LATER_THAN_CURRENT_DATE.getErrorMessage());
            } else {
                return yearOfWriting;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new EntityValidationException(PATTERN_FOR_BOOK_YEAR_OF_WRITING_VALUE.getErrorMessage());
        }
    }
}
