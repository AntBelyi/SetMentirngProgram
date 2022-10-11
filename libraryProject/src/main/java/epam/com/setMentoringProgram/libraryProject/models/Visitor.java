package epam.com.setMentoringProgram.libraryProject.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import epam.com.setMentoringProgram.libraryProject.utils.customDeSerializers.VisitorDateOfBirthDeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "Visitor")
@Accessors(chain = true)
public class Visitor {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "initials")
    @Pattern(regexp = "[A-Z][a-z]{2,15} [A-Z][a-z]{2,15} [A-Z][a-z]{2,15}", message = "Enter name in such format, please: <Shevchenko Taras Hryhorovych>")
    private String initials;

    @Column(name = "age")
    @Min(value = 0, message = "Age should be greater than 0")
    @Max(value = 130, message = "Age should be greater than 150")
    private int age;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = VisitorDateOfBirthDeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Europe/Kiev")
    private Date dateOfBirth;

    @Column(name = "email")
    @Email
    @NotBlank(message = "Email shouldn't be empty")
    private String email;

    @OneToMany(mappedBy = "whoRead")
    private List<Book> books;

    public void addBook(Book book) {
        if(Objects.isNull(books)) {
            this.books = new ArrayList<>();
        }
        this.books.add(book);
    }

}
