package com.ernestas.familyfeudbot.question;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.answer.AnswerType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class QuestionLoader {

  public List<Question> getQuestions() {
    try {
      List<Question> questionList = new ArrayList<>();
      Resource resource = new ClassPathResource("questions.xlsx");
      InputStream input = resource.getInputStream();
      XSSFWorkbook xssfWorkbook = new XSSFWorkbook(input);

      XSSFSheet xssfSheet = xssfWorkbook.getSheet("7 answers");

      Iterator<Row> rowIterator = xssfSheet.iterator();

      rowIterator.next();

      int answers = 7;

      while (rowIterator.hasNext()) {
        Row row = rowIterator.next();

        Question question = new Question();
        question.setQuestionText(row.getCell(0).getStringCellValue());

        for (int i = 1; i < answers * 2; i += 2) {
          Answer answer = new Answer();
          answer.setAnswerText(getCellValue(row.getCell(i)));
          answer.setPoints(Double.valueOf(getCellValue(row.getCell(i + 1))));
          answer.setAnswerType(AnswerType.UNANSWERED);
          question.getAnswerList().add(answer);
        }
        questionList.add(question);
      }

      return questionList;
    } catch (Exception ex) {
      Logger.getGlobal().log(Level.FINE, ex.toString());
    }
    return Collections.emptyList();
  }

  private String getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case NUMERIC:
        return String.valueOf(cell.getNumericCellValue());
      case STRING:
        return cell.getStringCellValue();
      default:
        return "";
    }
  }

}
