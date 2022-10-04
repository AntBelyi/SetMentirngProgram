package epam.com.setMentoringProgram.libraryProject.controllers;

import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.validators.VisitorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;

@RestController
@RequestMapping("/visitors")
public class VisitorsController extends BaseController {

    private final VisitorService visitorService;
    private final VisitorValidator visitorValidator;

    @Autowired
    public VisitorsController(VisitorService visitorService, VisitorValidator visitorValidator) {
        this.visitorService = visitorService;
        this.visitorValidator = visitorValidator;
    }

    @GetMapping()
    public List<VisitorDto> getVisitors() {
        return visitorService.getVisitors(VisitorDto.class);
    }

    @GetMapping(params = {"page", "countOfItems"})
    public List<VisitorDto> getVisitors(@RequestParam(value = "page") int page,
                                        @RequestParam(value = "countOfItems") int countOfItems) {
        return visitorService.getVisitors(page, countOfItems, VisitorDto.class);
    }

    @GetMapping("/{id}")
    public VisitorDto getVisitorById(@PathVariable("id") int visitorId) {
        return convertToEntity(visitorService.getVisitorById(visitorId), VisitorDto.class);
    }

    @PostMapping("/new")
    public ResponseEntity<List<VisitorDto>> createVisitor(@RequestBody @Valid VisitorDto visitorDto, BindingResult bindingResult) {
        validateEntity(bindingResult, visitorDto, visitorValidator);
        List<VisitorDto> visitorDtoList = visitorService.createVisitor(visitorDto);
        return new ResponseEntity<>(visitorDtoList, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<VisitorDto> updateVisitor(@RequestBody @Valid VisitorDto visitorDto, BindingResult bindingResult,
                                                    @PathVariable("id") int visitorId) {
        validateEntity(bindingResult, visitorDto, visitorValidator);
        VisitorDto updatedVisitor = visitorService.updatedVisitor(visitorId, visitorDto);
        return new ResponseEntity<>(updatedVisitor, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteVisitor(@PathVariable("id") int id) {
        visitorService.deleteVisitor(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
