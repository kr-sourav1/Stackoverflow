package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.service.ITopQuestionSelection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopQuestionsByVotes implements ITopQuestionSelection {
    @Override
    public List<Question> selectTopQuestions(List<Question> questions, int searchCount) {
        questions.sort((b, a) -> (a.getVotes() - b.getVotes()));
        return questions.subList(0, Math.min(questions.size(), searchCount));
    }
}
