package epam.com.setMentoringProgram.libraryProject.utils.customDeSerializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.SneakyThrows;

import java.util.Date;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.BOOK_YEAR_OF_WRITING_CANT_BE_GREATER_THAN_1000;
import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.BOOK_YEAR_OF_WRITING_CANT_BE_LATER_THAN_CURRENT_DATE;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.DateUtils.validateDate;

public class BookYearOfWritingDeSerializer extends StdDeserializer<Date> {

    protected BookYearOfWritingDeSerializer() {
        super(Date.class);
    }

    @SneakyThrows
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        String value = jsonParser.readValueAs(String.class);
        return validateDate(value,
                            BOOK_DATE_VALUES,
                            BOOK_YEAR_OF_WRITING_CANT_BE_GREATER_THAN_1000,
                            BOOK_YEAR_OF_WRITING_CANT_BE_LATER_THAN_CURRENT_DATE
        );
    }
}
