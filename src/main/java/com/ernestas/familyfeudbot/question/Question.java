package com.ernestas.familyfeudbot.question;

import com.ernestas.familyfeudbot.answer.Answer;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "t_question")
public class Question {

  @Id
  @GeneratedValue
  @Column(name = "question_id")
  private Long id;

  @Column(name = "question_text")
  private String questionText;

  @OneToMany(mappedBy="question",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
  private List<Answer> answerList;
}
