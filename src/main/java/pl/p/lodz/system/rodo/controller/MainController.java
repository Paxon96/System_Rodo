package pl.p.lodz.system.rodo.controller;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import pl.p.lodz.system.rodo.entity.Mark;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.MarkRepository;
import pl.p.lodz.system.rodo.repo.UserRepository;
import pl.p.lodz.system.rodo.service.MarkService;
import pl.p.lodz.system.rodo.service.SpreadsheetService;
import pl.p.lodz.system.rodo.service.UserService;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MarkService markService;
    @Autowired
    private UserService userService;
    @Autowired
    private SpreadsheetService spreadsheetService;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getMainPage(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView postMainPage(@ModelAttribute("user") @Valid User user, RedirectAttributes redirect, ModelAndView model) {

        if(user.getLogin() == null){
            model.setViewName("redirect:/");
            redirect.addFlashAttribute("user", user);
            redirect.addFlashAttribute("invalidPassword", "Niepoprawne hasło");
            return model;
        }

        if (!userService.findLoginInDatabase(user.getLogin()) || userRepository.findFirstByLogin(user.getLogin()).getPassword() == null) {
            model.setViewName("redirect:/");
            redirect.addFlashAttribute("user", user);
            //TODO Odkomentować wysyłanie maili. Wysyła na adresy politechniczne
            //userService.sendEmailToNewUser(user);
            redirect.addFlashAttribute("noUserInSystem", "Hasło dla tego nr albumu zostało wysłane e-mailem");
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
        markService.deleteMarkByUser(markId, auth);

        model.setViewName("redirect:/marks");
        return model;
    }

    @RequestMapping(value = "fileUpload", method = RequestMethod.GET)
    public String getTeacherFileUpload(Model model) {
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
            redirect.addFlashAttribute("noIntegerAsDays", "Podana wartość dni nie jest liczbą");
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
    public ModelAndView gerFile(@RequestParam("file") MultipartFile file, ModelAndView model, RedirectAttributes redirect) {

        if(!spreadsheetService.isFileFormatValid(file)){
            redirect.addFlashAttribute("errorMsg", "Błąd wczytywania pliku");
            model.setViewName("redirect:fileUpload");
            return model;
        }
        spreadsheetService.addMarks(file);

        redirect.addFlashAttribute("succesMsg", "Plik wczytany pomyślnie");
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
