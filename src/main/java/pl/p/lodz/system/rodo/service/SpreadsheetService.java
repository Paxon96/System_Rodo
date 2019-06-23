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

    DataFormatter formatter = new DataFormatter();

    public void addMarks(MultipartFile file) {
        Workbook workbook;
        if (isFileFormatValid(file)) {
            workbook = initializeWorkbook(file);
            processRows(workbook);
        }
    }

    private Workbook initializeWorkbook(MultipartFile file)
    {
        try {
            if (file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
                return new XSSFWorkbook(file.getInputStream());
            } else if (file.getOriginalFilename().toLowerCase().endsWith(".xls")) {
                return new HSSFWorkbook(file.getInputStream());
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }

    private boolean isFileFormatValid(MultipartFile file)
    {
        return file.getOriginalFilename().toLowerCase().endsWith(".xlsx") || file.getOriginalFilename().toLowerCase().endsWith(".xls");
    }

    private void processRows(Workbook workbook)
    {
        double points;
        double markValue;
        String activity;
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        User user;

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            byte i = 0;
            points = 0.0;
            markValue = 2.0;
            activity = "";
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (i++) {
                    case 0:
                        if(cell.getCellType() == CellType.STRING)
                            activity = cell.getStringCellValue();
                        break;
                    case 1:
                        if(cell.getCellType() == CellType.NUMERIC)
                            markValue = tryParse(cell.toString());
                        break;
                    case 2:
                        if(cell.getCellType() == CellType.NUMERIC)
                            points = tryParse(cell.toString());
                        break;
                    case 3:
                        user = User.builder()
                                .login(formatter.formatCellValue(cell))
                                .permission("ROLE_USER")
                                .build();
                        User userToCheck = user;
                        if (!userRepository.findAll().stream().anyMatch(u -> u.getLogin().equals(userToCheck.getLogin())))
                            userRepository.save(user);
                        else
                            user = userRepository.findFirstByLogin(user.getLogin());
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
    }

    private Double tryParse(String number)
    {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        try
        {
            return format.parse(number).doubleValue();
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        return 0.0;
    }
}
