package epam.com.setMentoringProgram.libraryProject.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DateFormattingValues {
    BOOK_DATE_VALUES("yyyy", "1000"),
    VISITOR_DATE_VALUES("dd-MM-yyyy", "01-01-1930");

    private final String dateCreatingPattern;
    private final String lowerDateRange;
}
