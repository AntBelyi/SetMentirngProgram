package epam.com.setMentoringProgram.libraryProject.services;

import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.repositories.VisitorRepository;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.VISITOR_BY_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class VisitorService {
    private final VisitorRepository visitorRepository;

    @Autowired
    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public List<Visitor> getVisitors() {
        return visitorRepository.findAll().stream().peek(visitor -> visitor.setBooks(null)).collect(Collectors.toList());
    }

    public Visitor getVisitorById(int visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new EntityNotFoundException(VISITOR_BY_ID_NOT_FOUND.getErrorMessage()));
        Hibernate.initialize(visitor.getBooks());
        return visitor;
    }

    public Visitor getVisitorByInitials(String initials, Date dateOfBirth) {
        return visitorRepository.findByInitialsAndDateOfBirth(initials, dateOfBirth);
    }

    @Transactional
    public void createVisitor(Visitor visitor) {
        visitorRepository.save(visitor);
    }

    @Transactional
    public void updatedVisitor(int updatedVisitorId, Visitor visitor) {
        if(Objects.nonNull(getVisitorById(updatedVisitorId))) {
            visitor.setId(updatedVisitorId);
            visitorRepository.save(visitor);
        }
    }

    @Transactional
    public void deleteVisitor(int id) {
        if(Objects.nonNull(getVisitorById(id))) {
            visitorRepository.deleteById(id);
        }
    }
}
