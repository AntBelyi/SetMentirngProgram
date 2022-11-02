package epam.com.setMentoringProgram.libraryProject.controllers.ui;

import epam.com.setMentoringProgram.libraryProject.dto.VisitorDto;
import epam.com.setMentoringProgram.libraryProject.services.VisitorService;
import epam.com.setMentoringProgram.libraryProject.utils.validators.VisitorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static epam.com.setMentoringProgram.libraryProject.utils.validators.ConverterUtils.convertToEntity;

@Controller
@RequestMapping("/ui/visitors")
public class UiVisitorsController extends BaseUiController {

    private final VisitorService visitorService;
    private final VisitorValidator visitorValidator;

    @Autowired
    public UiVisitorsController(VisitorService visitorService, VisitorValidator visitorValidator) {
        this.visitorService = visitorService;
        this.visitorValidator = visitorValidator;
    }

    @GetMapping()
    public String getVisitors(Model model) {
        model.addAttribute("visitors", visitorService.getVisitors(VisitorDto.class));
        return "visitors/showAllVisitorsPage";
    }

    @GetMapping("/{id}")
    public String getVisitorById(@PathVariable("id") int id, Model model) {
        VisitorDto visitorDto = convertToEntity(visitorService.getVisitorById(id), VisitorDto.class);
        model.addAttribute("visitor", visitorDto);
        return "visitors/showVisitorByIdPage";
    }

    @GetMapping("/new")
    public String getFormForCreatingVisitor(@ModelAttribute("visitor") VisitorDto visitorDto) {
        return "visitors/createVisitorPage";
    }

    @PostMapping()
    public String addVisitorToDB(@ModelAttribute("visitor") @Valid VisitorDto visitorDto, BindingResult bindingResult, Model model) {
        if(validateEntity(bindingResult, visitorDto, visitorValidator, model)) {
            return "visitors/createVisitorPage";
        } else {
            visitorService.createVisitor(visitorDto);
            return "redirect:/ui/visitors";
        }
    }

    @GetMapping("/update/{id}")
    public String getFormForUpdatingVisitor(@PathVariable("id") int id, Model model) {
        model.addAttribute("visitorById", convertToEntity(visitorService.getVisitorById(id), VisitorDto.class));
        return "visitors/updateVisitorPage";
    }

    @PutMapping("/{id}")
    public String updateVisitor(@ModelAttribute("visitorById") @Valid VisitorDto visitorDto,
                                BindingResult bindingResult,
                                Model model,
                                @PathVariable("id") int id) {
        if(validateEntity(bindingResult, visitorDto, visitorValidator, model)) {
            return "visitors/updateVisitorPage";
        } else {
            visitorService.updatedVisitor(id, visitorDto);
            return "redirect:/ui/visitors";
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteVisitor(@PathVariable("id") int id) {
        visitorService.deleteVisitor(id);
        return "redirect:/ui/visitors";
    }

}
