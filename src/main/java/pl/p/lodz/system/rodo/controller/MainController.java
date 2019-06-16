package pl.p.lodz.system.rodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.MarkRepository;
import pl.p.lodz.system.rodo.repo.UserRepository;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Controller
public class MainController {

    @Autowired private UserRepository userRepository;

    @Autowired private MarkRepository markRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getMainPage(Model model) {
        System.out.println(userRepository.findFirstByLogin("temp1"));
        model.addAttribute("user", new User());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("test");
        System.out.println(encodedPassword);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        auth.getAuthorities();
        String name = auth.getName();
        System.out.println(name);
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView postMainPage(@ModelAttribute("user") @Valid User user, RedirectAttributes redirect, ModelAndView model) {

        if (user.getLogin().equalsIgnoreCase("a")) {
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
        User user = (User) modelMap.get("user");
        System.out.println(user);
        model.addAttribute("user", user);
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
        if (user.getLogin().equalsIgnoreCase("N")) {
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("marks", markRepository.findFirstByUser(userRepository.findFirstByLogin(auth.getName())));
        return "studentMarks";
    }

    @RequestMapping(value = "fileUpload", method = RequestMethod.GET)
    public String getTeacherFileUpload(Model model) {
        System.out.println(model);
        return "teacherFileUpload";
    }

    @RequestMapping(value = "settings", method = RequestMethod.GET)
    public String getTeacherSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorityList = auth.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorityList) {
            if (grantedAuthority.getAuthority().equalsIgnoreCase("ADMIN"))
                return "teacherSettings";
        }

        return "studentSettings";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView gerFile(@RequestParam("file") MultipartFile file, ModelAndView model) {
        System.out.println(file);
        try {
            System.out.println(new String(file.getBytes(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.setViewName("redirect:fileUpload");
        return model;
    }

    @PostMapping("/logout")
    public String logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        System.out.println(name);
        return "index";
    }
}
