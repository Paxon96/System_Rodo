package pl.p.lodz.system.rodo.controller;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.MarkRepository;
import pl.p.lodz.system.rodo.repo.SettingsRepository;
import pl.p.lodz.system.rodo.repo.UserRepository;
import pl.p.lodz.system.rodo.service.MarkService;
import pl.p.lodz.system.rodo.service.UserService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Controller
public class MainController {

    @Autowired private UserRepository userRepository;
    @Autowired private MarkRepository markRepository;
    @Autowired private SettingsRepository settingsRepository;
    @Autowired private MarkService markService;
    @Autowired private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getMainPage(Model model) {
        model.addAttribute("user", new User());
        //        userRepository.save(User.builder().login("t2").password(passwordEncoder.encode("t2")).permission("ROLE_USER").build());
        //
        //        markRepository.save(Mark.builder()
        //                                .points(12.5)
        //                                .evalDate(new Timestamp(System.currentTimeMillis()))
        //                                .mark(4)
        //                                .user(userRepository.findFirstByLogin("t3"))
        //                                .build());
        //        markRepository.save(Mark.builder()
        //                                .points(12)
        //                                .evalDate(new Timestamp(System.currentTimeMillis()))
        //                                .mark(5)
        //                                .user(userRepository.findFirstByLogin("t3"))
        //                                .build());
        //        Settings settings = Settings.builder().daysToDelete((short) 5).user(userRepository.findFirstByLogin("t2")).build();
        //        settingsRepository.save(settings);
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView postMainPage(@ModelAttribute("user") @Valid User user, RedirectAttributes redirect, ModelAndView model) {

        if (!userService.findLoginInDatabase(user.getLogin())) {
            model.setViewName("redirect:/");
            redirect.addFlashAttribute("noUserInSystem", "temp");
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
    public ModelAndView postPasswordPage(@ModelAttribute("user") @Valid User user, ModelAndView model) {
        return model;
    }

    @RequestMapping(value = "marks", method = RequestMethod.GET)
    public String getStudentMarks(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("marks", userRepository.findFirstByLogin(auth.getName()).getMarks());
        return "studentMarks";
    }

    @RequestMapping(value = "marks/delete", method = RequestMethod.POST)
    public ModelAndView deleteMarkByStudent(@RequestParam("markId") int markId, ModelAndView model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        markService.deleteMark(markId, auth);

        model.setViewName("redirect:/marks");
        return model;
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
            if (grantedAuthority.getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
                model.addAttribute("settings", userRepository.findFirstByLogin(auth.getName()).getSettings().get(0));
                return "teacherSettings";
            }
        }

        return "studentSettings";
    }

    @RequestMapping(value = "settings/teacher", method = RequestMethod.POST)
    public ModelAndView setTeacherSettings(@RequestParam("newPassword") String newPassword, @RequestParam("newDays") String newDays,
            ModelAndView model, RedirectAttributes redirect) {
        model.setViewName("redirect:/settings");

        if (!newDays.isEmpty() && !NumberUtils.isDigits(newDays)) {
            redirect.addFlashAttribute("noIntegerAsDays", "temp");
            return model;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userService.changeUserPassword(newPassword, auth);
        userService.changeDaysSettings(newDays, auth);

        return model;
    }


    @RequestMapping(value = "settings/student", method = RequestMethod.POST)
    public ModelAndView setStudentSettings(@RequestParam("newPassword") String newPassword, ModelAndView model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        userService.changeUserPassword(newPassword, auth);

        model.setViewName("redirect:/settings");
        return model;
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
