package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.dto.TopQuestionResponse;
import com.stackoverflow.beta.model.request.QuestionCreateRequest;
import com.stackoverflow.beta.service.IQuestion;
import com.stackoverflow.beta.utils.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing questions.
 */
@RestController
@RequestMapping("/question")
@CrossOrigin(origins = "http://localhost:5173/")
public class QuestionController {
    private final IQuestion questionService;

    @Autowired
    public QuestionController(IQuestion questionService) {
        this.questionService = questionService;
    }

    /**
     * Endpoint for posting a new question.
     *
     * @param questionCreateRequest the question request to post
     * @return the saved Question entity
     */
    @PostMapping("/post")
    public ResponseEntity<?> postQuestion(@RequestBody QuestionCreateRequest questionCreateRequest) {
        try {
            return new ResponseEntity<>(questionService.saveQuestion(questionCreateRequest), HttpStatus.CREATED);
        }
        catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    /**
     * Endpoint for finding a question by its ID.
     *
     * @param id the ID of the question to find
     * @return the QuestionResponse containing the question details
     */
    @GetMapping("/findById/{id}")
    ResponseEntity<?> findQuestionById(@PathVariable int id) {
        try {
            Optional<Question> question = questionService.findQuestionById(id);
            return new ResponseEntity<>(question, HttpStatus.OK);  // Change to OK, since you found the question
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }  catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    /**
     * Endpoint to get the top questions based on the given criteria.
     *
     * @param count    the criteria for selecting number of top questions (e.g., 10 or 20)
     * @return a ResponseEntity containing the list of top questions
     */
    @GetMapping("/top")
    public ResponseEntity<?> getTopQuestions(@RequestParam int count) {
        try {
            TopQuestionResponse topQuestions = questionService.findTopQuestions(count);
            return new ResponseEntity<>(topQuestions, HttpStatus.OK);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

    /**
     * Endpoint for retrieving questions by tag.
     *
     * @param tag the tag to filter questions by
     * @return a ResponseEntity containing a list of questions associated with the specified tag
     */
//    @GetMapping("/tag/{tag}")
//    public ResponseEntity<?> findQuestionsByTag(@PathVariable String tag) {
//        try {
//            return ResponseEntity.ok(questionService.findQuestionsByTag(tag));
//        } catch (Exception e) {
//            return ExceptionHandler.handleException(e);
//        }
//    }
}
