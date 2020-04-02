package com.ernestas.familyfeudbot.question;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.answer.AnswerRepository;
import com.ernestas.familyfeudbot.answer.AnswerType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class QuestionLoader {

  private QuestionRepository questionRepository;

  private AnswerRepository answerRepository;

  private Logger logger = LoggerFactory.getLogger(QuestionLoader.class);

  public void loadQuestions() {
    try {
      Resource resource = new ClassPathResource("questions.xlsx");
      InputStream input = resource.getInputStream();
      XSSFWorkbook xssfWorkbook = new XSSFWorkbook(input);

      for (QuestionSheet questionSheet : QuestionSheet.values()) {

        XSSFSheet xssfSheet = xssfWorkbook.getSheet(questionSheet.getName());
        Iterator<Row> rowIterator = xssfSheet.iterator();
        rowIterator.next();

        while (rowIterator.hasNext()) {
          Row row = rowIterator.next();

          Question question = Question.builder()
              .questionText(row.getCell(0).getStringCellValue())
              .answerList(new ArrayList<>())
              .build();

          for (int i = 1; i < row.getPhysicalNumberOfCells() - 1; i += 2) {
            Answer answer = Answer.builder()
                .answerText(getCellValue(row.getCell(i)))
                .points(Double.valueOf(getCellValue(row.getCell(i + 1))))
                .answerType(AnswerType.UNANSWERED)
                .question(question)
                .build();

            question.getAnswerList().add(answer);
          }

          questionRepository.saveAndFlush(question);
        }

      }
    } catch (Exception ex) {
      logger.error(ex.toString());
    }
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

  @Autowired
  public void setQuestionRepository(QuestionRepository questionRepository) {
    this.questionRepository = questionRepository;
  }

  @Autowired
  public void setAnswerRepository(AnswerRepository answerRepository) {
    this.answerRepository = answerRepository;
  }
}
