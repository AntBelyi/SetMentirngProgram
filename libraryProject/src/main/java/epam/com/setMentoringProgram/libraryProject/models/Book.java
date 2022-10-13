package epam.com.setMentoringProgram.libraryProject.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import epam.com.setMentoringProgram.libraryProject.utils.customDeSerializers.BookYearOfWritingDeSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Entity
@Table(name = "Book")
@Accessors(chain = true)
public class Book {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotEmpty(message = "Enter book name, please")
    private String name;

    @Column(name = "author")
    @Pattern(regexp = "[A-Z][a-z]{2,15} [A-Z][a-z]{2,15}", message = "Enter author name in such format, please: <Taras Shevchenko>")
    private String author;

    @Column(name = "year_of_writing")
    @Temporal(TemporalType.DATE)
    @JsonDeserialize(using = BookYearOfWritingDeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy", timezone = "Europe/Kiev")
    private Date yearOfWriting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id", referencedColumnName = "id")
    private Visitor whoRead;

}
