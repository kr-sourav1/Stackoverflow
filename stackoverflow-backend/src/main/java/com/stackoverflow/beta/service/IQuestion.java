package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.dto.QuestionResponse;
import com.stackoverflow.beta.model.request.QuestionCreateRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing questions.
 */
public interface IQuestion {

    /**
     * Saves a new question.
     *
     * @param input the details of the question to save
     * @return the saved {@link Question} entity
     */
    Question saveQuestion(QuestionCreateRequest input);

    /**
     * Retrieves all questions associated with a specific tag.
     *
     * @param tag the tag used to filter questions
     * @return a list of {@link Question} entities associated with the specified tag
     */
    List<Question> findQuestionsByTag(String tag);

    /**
     * Retrieves a question by its unique ID.
     *
     * @param questionId the unique ID of the question to retrieve
     * @return an {@link Optional} containing the {@link Question} entity, if found
     */
    Optional<Question> findQuestionById(int questionId);

    /**
     * Retrieves the top questions based on the specified criteria.
     *
     * @param limit    the maximum number of top questions to retrieve
     * @return a list of {@link QuestionResponse} containing details of the top questions
     */
    QuestionResponse findTopQuestions(int limit);
}
