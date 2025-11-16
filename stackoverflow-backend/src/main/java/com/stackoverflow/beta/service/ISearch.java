package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.ResultQuery;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public interface ISearch {
    List<Question> searchFromQuery(String query) throws IOException;

}
