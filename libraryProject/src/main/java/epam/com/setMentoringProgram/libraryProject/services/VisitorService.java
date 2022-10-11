package epam.com.setMentoringProgram.libraryProject.services;

import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.repositories.VisitorRepository;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static epam.com.setMentoringProgram.libraryProject.enums.ValidationMessages.VISITOR_BY_ID_NOT_FOUND;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;

@Service
@Transactional(readOnly = true)
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private static final String FIELD_FOR_SORTING = "id";

    @Autowired
    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    public List<Visitor> getVisitors() {
        return visitorRepository.findAll(Sort.by(FIELD_FOR_SORTING)).stream().peek(visitor -> visitor.setBooks(null)).collect(Collectors.toList());
    }

    public List<VisitorDto> getVisitors(Class<VisitorDto> visitorDtoClazz) {
        return getVisitors().stream().map(visitor -> convertToEntity(visitor, visitorDtoClazz)).collect(Collectors.toList());
    }

    public List<VisitorDto> getVisitors(int page, int countOfVisitorsToDisplay, Class<VisitorDto> visitorDtoClass) {
        return visitorRepository.findAll(PageRequest.of(page, countOfVisitorsToDisplay, Sort.by(FIELD_FOR_SORTING)))
                .stream().peek(visitor -> visitor.setBooks(null))
                .map(visitor -> convertToEntity(visitor, visitorDtoClass)).collect(Collectors.toList());
    }

    public Visitor getVisitorById(int visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new EntityNotFoundException(VISITOR_BY_ID_NOT_FOUND.getErrorMessage()));
        Hibernate.initialize(visitor.getBooks());
        return visitor;
    }

    public Visitor getVisitorByInitialsAndDateOfBirth(String initials, Date dateOfBirth) {
        return visitorRepository.findByInitialsAndDateOfBirth(initials, dateOfBirth);
    }

    @Transactional
    public void createVisitor(Visitor visitor) {
        visitorRepository.save(visitor);
    }

    @Transactional
    public List<VisitorDto> createVisitor(VisitorDto visitorDto) {
        createVisitor(convertToEntity(visitorDto, Visitor.class));
        return getVisitors().stream().map(visitor -> convertToEntity(visitor, VisitorDto.class)).collect(Collectors.toList());
    }

    @Transactional
    public void updatedVisitor(int updatedVisitorId, Visitor visitor) {
        if(Objects.nonNull(getVisitorById(updatedVisitorId))) {
            visitor.setId(updatedVisitorId);
            visitorRepository.save(visitor);
        }
    }

    @Transactional
    public VisitorDto updatedVisitor(int updatedVisitorId, VisitorDto visitorDto) {
        updatedVisitor(updatedVisitorId, convertToEntity(visitorDto, Visitor.class));
        return convertToEntity(getVisitorById(updatedVisitorId), VisitorDto.class);
    }

    @Transactional
    public void deleteVisitor(int id) {
        if(Objects.nonNull(getVisitorById(id))) {
            visitorRepository.deleteById(id);
        }
    }
}
