package epam.com.setMentoringProgram.libraryProject.controllers;

import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.models.Visitor;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityErrorResponse;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityNotFoundException;
import epam.com.setMentoringProgram.libraryProject.utils.exceptions.EntityValidationException;
import epam.com.setMentoringProgram.libraryProject.utils.validators.VisitorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;
import static epam.com.setMentoringProgram.libraryProject.utils.validators.ValidatorUtils.validateEntityFields;

@RestController
@RequestMapping("/visitors")
public class VisitorsController {

    private final VisitorService visitorService;
    private final VisitorValidator visitorValidator;

    @Autowired
    public VisitorsController(VisitorService visitorService, VisitorValidator visitorValidator) {
        this.visitorService = visitorService;
        this.visitorValidator = visitorValidator;
    }

    @GetMapping()
    public List<VisitorDto> getVisitors() {
        return visitorService.getVisitors().stream()
                .map(visitor -> convertToEntity(visitor, VisitorDto.class)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public VisitorDto getVisitorById(@PathVariable("id") int visitorId) {
        return convertToEntity(visitorService.getVisitorById(visitorId), VisitorDto.class);
    }

    @PostMapping("/new")
    public ResponseEntity<List<VisitorDto>> createVisitor(@RequestBody @Valid VisitorDto visitorDto, BindingResult bindingResult) {
        visitorValidator.validate(visitorDto, bindingResult);
        validateEntityFields(bindingResult);
        visitorService.createVisitor(convertToEntity(visitorDto, Visitor.class));
        List<VisitorDto> visitorDtoList = visitorService.getVisitors().stream().map(v -> convertToEntity(v, VisitorDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(visitorDtoList, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<VisitorDto> updateVisitor(@RequestBody @Valid VisitorDto visitorDto,
                                                    BindingResult bindingResult,
                                                    @PathVariable("id") int visitorId) {
        visitorValidator.validate(visitorDto, bindingResult);
        validateEntityFields(bindingResult);
        visitorService.updatedVisitor(visitorId, convertToEntity(visitorDto, Visitor.class));
        VisitorDto updatedVisitor = convertToEntity(visitorService.getVisitorById(visitorId), VisitorDto.class);
        return new ResponseEntity<>(updatedVisitor, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteVisitor(@PathVariable("id") int id) {
        visitorService.deleteVisitor(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<EntityErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        EntityErrorResponse exceptionResponse = new EntityErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                exception.getMessage()
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<EntityErrorResponse> handleEntityNotCreatedException(EntityValidationException exception) {
        EntityErrorResponse exceptionResponse = new EntityErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                exception.getMessage().split("\\.")
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
