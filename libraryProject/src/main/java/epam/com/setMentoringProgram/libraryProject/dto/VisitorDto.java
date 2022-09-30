package epam.com.setMentoringProgram.libraryProject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import epam.com.setMentoringProgram.libraryProject.models.Book;
import epam.com.setMentoringProgram.libraryProject.utils.customDeSerializers.VisitorDateOfBirthDeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitorDto {

    private int id;

    @Pattern(regexp = "[A-Z][a-z]{2,15} [A-Z][a-z]{2,15} [A-Z][a-z]{2,15}", message = "Enter name in such format, please: <Shevchenko Taras Hryhorovych>")
    private String initials;

    @Min(value = 0, message = "Age should be greater than 0")
    @Max(value = 130, message = "Age should be greater than 130")
    private int age;

    @JsonDeserialize(using = VisitorDateOfBirthDeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Europe/Kiev")
    private Date dateOfBirth;

    @Email
    @NotBlank(message = "Email shouldn't be empty")
    private String email;

    private List<BookDto> books;

    public VisitorDto setInitials(String initials) {
        this.initials = initials.trim();
        return this;
    }

    public void setBooks(List<Book> books) {
        if (Objects.nonNull(books)) {
            List<BookDto> bookDtoList = new ArrayList<>();
            books.forEach(book -> {
                BookDto bookDto = new BookDto()
                                 .setAuthor(book.getAuthor())
                                 .setId(book.getId())
                                 .setName(book.getName())
                                 .setYearOfWriting(book.getYearOfWriting());
                bookDtoList.add(bookDto);
            });
            this.books = bookDtoList;
        }
    }
}
