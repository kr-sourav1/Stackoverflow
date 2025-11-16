package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.service.ISearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SearchService implements ISearch {
    private final QuestionRepository questionRepository;

    @Autowired
    public SearchService(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }
    @Override
    public List<Question> searchFromQuery(String query) throws IOException {
        // fetch all questionIds which either linked with question/answer/comment
        List<Integer> ids = questionRepository.searchQuestion(query);

        // fetch all the questions linked with ids
        return questionRepository.fetchQuestionList(ids);
    }
}
