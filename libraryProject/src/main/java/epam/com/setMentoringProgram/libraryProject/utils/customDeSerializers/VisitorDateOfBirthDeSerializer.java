package epam.com.setMentoringProgram.libraryProject.utils.customDeSerializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.*;

public class VisitorDateOfBirthDeSerializer extends StdDeserializer<Date> {

    protected VisitorDateOfBirthDeSerializer() {
        super(Date.class);
    }

    @SneakyThrows
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        String value = jsonParser.readValueAs(String.class);
        try {
            Date dateOfBirth = new SimpleDateFormat(VISITOR_DATE_VALUES.getDateCreatingPattern()).parse(value);
            if(dateOfBirth.before(new SimpleDateFormat(VISITOR_DATE_VALUES.getDateCreatingPattern()).parse(VISITOR_DATE_VALUES.getLowerDateRange()))) {
                throw new EntityValidationException(DATE_OF_BIRTH_CANT_BE_EARLIER_THAN.getErrorMessage());
            } else if(dateOfBirth.after(new Date())) {
                throw new EntityValidationException(DATE_OF_BIRTH_CANT_BE_LATER_THAN_CURRENT_DATE.getErrorMessage());
            } else {
                return dateOfBirth;
            }
        } catch (ParseException e) {
            throw new EntityValidationException(PATTERN_FOR_DATE_OF_BIRTH_VALUE.getErrorMessage());
        }
    }
}
