package pl.p.lodz.system.rodo.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import pl.p.lodz.system.rodo.entity.Mark;
import pl.p.lodz.system.rodo.entity.User;
import pl.p.lodz.system.rodo.repo.MarkRepository;
import pl.p.lodz.system.rodo.repo.UserRepository;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Locale;

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
                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    switch(i++){
                        case 0:
                            mark.builder().activity(currentCell.getStringCellValue());
                            break;
                        case 1:
                            mark.builder().mark(format.parse(currentCell.toString()).doubleValue());
                            break;
                        case 2:
                            mark.builder().evalDate(timestamp);
                            break;
                        case 3:
                            User user = User.builder()
                                    .login(formatter.formatCellValue(currentCell))
                                    .permission("ROLE_USER")
                                    .build();
                            userRepository.save(user);
                            mark.builder().user(user).build();
                            markRepository.save(mark);
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
