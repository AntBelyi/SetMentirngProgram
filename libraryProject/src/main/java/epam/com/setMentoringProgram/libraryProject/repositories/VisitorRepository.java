package epam.com.setMentoringProgram.libraryProject.repositories;

import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@Component
public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    Visitor findByInitialsAndDateOfBirth(String initials, Date dateOfBirth);
}
