package epam.com.setMentoringProgram.libraryProject.utils.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityErrorResponse {
    private String message;
    private List<String> messageList;
    private final Timestamp timestamp;

    public EntityErrorResponse(Timestamp timestamp, String... errorMessages) {
        if(errorMessages.length > 1) {
            this.messageList = Arrays.asList(errorMessages);
        } else {
            this.message = errorMessages[0];
        }
        this.timestamp = timestamp;
    }

}
