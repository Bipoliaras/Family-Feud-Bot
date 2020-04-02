package com.ernestas.familyfeudbot.answer;


import com.ernestas.familyfeudbot.question.Question;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "t_answer")
public class Answer {

  @Id
  @Column(name = "answer_id")
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name="question_id", nullable=false)
  private Question question;

  @Column(name = "answer_text")
  private String answerText;

  @Column(name = "answer_points")
  private Double points;

  private AnswerType answerType;
}
