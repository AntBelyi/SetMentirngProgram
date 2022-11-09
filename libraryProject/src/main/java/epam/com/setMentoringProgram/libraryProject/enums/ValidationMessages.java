package epam.com.setMentoringProgram.libraryProject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.BOOK_DATE_VALUES;
import static epam.com.setMentoringProgram.libraryProject.enums.DateFormattingValues.VISITOR_DATE_VALUES;

@Getter
@AllArgsConstructor
public enum ValidationMessages {
    VISITOR_BY_ID_NOT_FOUND("Visitor with such id wasn't found"),
    VISITOR_IS_ALREADY_REGISTERED("Visitor is already registered."),
    BOOK_BY_ID_NOT_FOUND("Book with such id wasn't found"),
    BOOK_IS_ALREADY_EXIST("Book is already exist"),
    BOOK_YEAR_OF_WRITING_CANT_BE_GREATER_THAN_1000(String.format("YearOfWriting value can't be earlier than %s year.", BOOK_DATE_VALUES.getLowerDateRange())),
    BOOK_YEAR_OF_WRITING_CANT_BE_LATER_THAN_CURRENT_DATE("YearOfWriting value can't be later than current date."),
    BOOK_YEAR_OF_WRITING_CANT_BE_EMPTY("YearOfWriting value can't be empty."),
    PATTERN_FOR_BOOK_YEAR_OF_WRITING_VALUE(String.format("Use such pattern for yearOfWriting value: <%s>.", BOOK_DATE_VALUES.getDateCreatingPattern())),
    DATE_OF_BIRTH_CANT_BE_EARLIER_THAN(String.format("DateOfBirth value can't be earlier than <%s>.", VISITOR_DATE_VALUES.getLowerDateRange())),
    DATE_OF_BIRTH_CANT_BE_LATER_THAN_CURRENT_DATE("DateOfBirth value can't be later than current date."),
    DATE_OF_BIRTH_CANT_BE_EMPTY("DateOfBirth value can't be empty."),
    PATTERN_FOR_DATE_OF_BIRTH_VALUE(String.format("Use such pattern for dateOfBirth value: <%s>.", VISITOR_DATE_VALUES.getDateCreatingPattern()));

    private final String errorMessage;
}
