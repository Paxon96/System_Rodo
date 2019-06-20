package pl.p.lodz.system.rodo.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

        @ExceptionHandler(value = Exception.class)
        public String exception(Model model){
            model.addAttribute("errorMessage", "Wystąpił błąd. Skontaktuj się z administratorem.");
            return "errorPage";
        }
}
