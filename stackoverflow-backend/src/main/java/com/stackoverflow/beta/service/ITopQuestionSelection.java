package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Question;

import java.util.List;

public interface ITopQuestionSelection {
    List<Question> selectTopQuestions(List<Question> questions, int searchCount);
}
