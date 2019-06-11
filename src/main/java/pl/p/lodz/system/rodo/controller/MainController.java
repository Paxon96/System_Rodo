package pl.p.lodz.system.rodo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.p.lodz.system.rodo.entity.User;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class MainController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getMainPage(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView postMainPage(@ModelAttribute("user") @Valid User user, RedirectAttributes redirect, ModelAndView model) {

        if (user.getId().equalsIgnoreCase("a")) {
            model.setViewName("redirect:/");
            redirect.addFlashAttribute("errorMsg", "temp");
            redirect.addFlashAttribute("user", user);
            return model;
        }

        model.setViewName("redirect:password");
        redirect.addFlashAttribute("user", user);

        return model;
    }

    @RequestMapping(value = "/password", method = RequestMethod.GET)
    public String getPasswordPage(Model model) {
        Map<String, Object> modelMap = model.asMap();
        model.addAttribute("user", modelMap.get("user"));
        return "passwordPage";
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public ModelAndView postPasswordPage(@ModelAttribute("user") @Valid User user, RedirectAttributes redirect, ModelAndView model) {
        if (user.getPassword().equalsIgnoreCase("a")) {
            model.setViewName("redirect:password");
            redirect.addFlashAttribute("errorMsg", "temp");
            redirect.addFlashAttribute("user", user);
            return model;
        }
        if (user.getId().equalsIgnoreCase("N")) {
            model.setViewName("redirect:fileUpload");
            redirect.addFlashAttribute("user", user);
            return model;
        } else {
            model.setViewName("redirect:marks");
            redirect.addFlashAttribute("user", user);
            return model;
        }
    }

    @RequestMapping(value = "marks", method = RequestMethod.GET)
    public String getStudentMarks(Model model) {
        System.out.println(model);
        return "studentMarks";
    }

    @RequestMapping(value = "fileUpload", method = RequestMethod.GET)
    public String getTeacherFileUpload(Model model) {
        System.out.println(model);
        return "teacherFileUpload";
    }

    @RequestMapping(value = "teacherSettings", method = RequestMethod.GET)
    public String getTeacherSettings(Model model){
        return "teacherSettings";
    }

    @RequestMapping(value = "studentSettings", method = RequestMethod.GET)
    public String getStudentSettings(Model model){
        return "studentSettings";
    }
}
