package epam.com.setMentoringProgram.libraryProject.controllers.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomePageController {

    @GetMapping()
    public String getWelcomePage() {
        return "welcome/welcomePage";
    }

}
