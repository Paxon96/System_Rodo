package pl.p.lodz.system.rodo.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.p.lodz.system.rodo.entity.Mark;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.MarkRepository;
import pl.p.lodz.system.rodo.repo.UserRepository;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Locale;

@Service
public class SpreadsheetService {
    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private UserRepository userRepository;

    public void addMarks(MultipartFile file, Authentication auth){
        try {
            //System.out.println(new String(file.getBytes(), "UTF-8"));
            Workbook workbook;
            DataFormatter formatter = new DataFormatter();
            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
            if(file.getOriginalFilename().toLowerCase().endsWith(".xlsx"))
            {
                workbook = new XSSFWorkbook(file.getInputStream());
            }else{
                workbook = new HSSFWorkbook(file.getInputStream());
            }
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                byte i = 0;
                Mark mark = new Mark();
                String activity = new String();
                double points = 0.0;
                double markValue = 0.0;
                //Timestamp evalDate;
                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    switch(i++){
                        case 0:
                            //mark.builder().activity(currentCell.getStringCellValue());
                            activity = currentCell.getStringCellValue();
                            break;
                        case 1:
                            //mark.builder().mark(format.parse(currentCell.toString()).doubleValue());
                            markValue = format.parse(currentCell.toString()).doubleValue();
                            break;
                        case 2:
                            points = format.parse(currentCell.toString()).doubleValue();
                            break;
                        case 3:
                            User user = User.builder()
                                    .login(formatter.formatCellValue(currentCell))
                                    .permission("ROLE_USER")
                                    .build();
                            if(!userRepository.findAll().stream().anyMatch(u -> u.getLogin().equals(user.getLogin())))
                                userRepository.save(user);
                            markRepository.save(Mark.builder()
                                    .activity(activity)
                                    .mark(markValue)
                                    .points(points)
                                    .evalDate(timestamp)
                                    .user(user)
                                    .build());
                            break;
                    }
                }
            }
        } catch (
                ParseException e){
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
