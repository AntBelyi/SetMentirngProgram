package epam.com.setMentoringProgram.libraryProject.utils.customDeSerializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.SneakyThrows;

import java.util.Date;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.DATE_OF_BIRTH_CANT_BE_EARLIER_THAN;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.DATE_OF_BIRTH_CANT_BE_LATER_THAN_CURRENT_DATE;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.validateDate;

public class VisitorDateOfBirthDeSerializer extends StdDeserializer<Date> {

    public VisitorDateOfBirthDeSerializer() {
        super(Date.class);
    }

    @SneakyThrows
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        String value = jsonParser.readValueAs(String.class);
        return validateDate(value,
                            VISITOR_DATE_VALUES,
                            DATE_OF_BIRTH_CANT_BE_EARLIER_THAN,
                            DATE_OF_BIRTH_CANT_BE_LATER_THAN_CURRENT_DATE
        );
    }
}
