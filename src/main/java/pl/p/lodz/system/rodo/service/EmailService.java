package pl.p.lodz.system.rodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    private static String EMAIL = "@edu.p.lodz.pl";

    public void sendEmail(String to, String content) {
        MimeMessage mail = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(to + EMAIL);
            helper.setFrom("wwwprojectplodz@outlook.com");
            helper.setSubject("Pierwsze hasło do systemu RODO");
            helper.setText(content, false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try{
        javaMailSender.send(mail);
    }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
