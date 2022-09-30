package epam.com.setMentoringProgram.libraryProject.repositories;

import epam.com.setMentoringProgram.libraryProject.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@Component
public interface BookRepository extends JpaRepository<Book, Integer> {
    Book findByNameAndAuthorAndYearOfWriting(String name, String author, Date yearOfWriting);
}
