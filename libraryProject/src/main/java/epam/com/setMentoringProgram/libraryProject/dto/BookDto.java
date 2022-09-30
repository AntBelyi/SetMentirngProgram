package epam.com.setMentoringProgram.libraryProject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.utils.customDeSerializers.BookYearOfWritingDeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.Objects;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDto {

    private int id;

    @Pattern(regexp = "[A-Z].*", message = "Book name have to begin from the capital letter")
    private String name;

    @Pattern(regexp = "[A-Z][a-z]{2,15} [A-Z][a-z]{2,15}", message = "Enter author name in such format, please: <Taras Shevchenko>")
    private String author;

    @JsonDeserialize(using = BookYearOfWritingDeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy", timezone = "Europe/Kiev")
    private Date yearOfWriting;

    private VisitorDto whoRead;

    public BookDto setName(String name) {
        this.name = name.trim();
        return this;
    }

    public void setWhoRead(Visitor whoRead) {
        if (Objects.nonNull(whoRead)) {
            this.whoRead = new VisitorDto()
                          .setId(whoRead.getId())
                          .setInitials(whoRead.getInitials())
                          .setAge(whoRead.getAge())
                          .setEmail(whoRead.getEmail())
                          .setDateOfBirth(whoRead.getDateOfBirth());
        }
    }
}
