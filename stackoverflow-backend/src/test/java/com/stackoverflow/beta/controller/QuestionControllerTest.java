package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.request.QuestionCreateRequest;
import com.stackoverflow.beta.service.IQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class QuestionControllerTest {
    @Mock
    private IQuestion questionService;

    @InjectMocks
    private QuestionController questionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostQuestion_Success() {
        QuestionCreateRequest postQuestionInput = getPostQuestionInput();
        Question mockQuestionResponse = getQuestionResponse();

        when(questionService.saveQuestion(any(QuestionCreateRequest.class))).thenReturn(mockQuestionResponse);

        ResponseEntity<?> response = questionController.postQuestion(postQuestionInput);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockQuestionResponse, response.getBody());
    }

    @Test
    void testFindQuestionById_Success() {
        Question questionResponse = getQuestionResponse();
        when(questionService.findQuestionById(anyInt())).thenReturn(Optional.of(questionResponse));

        ResponseEntity<?> response = questionController.findQuestionById(1);
        assertEquals(Optional.of(questionResponse), response.getBody());
    }

    @Test
    void testFindQuestionById_Failure() {
        ValidationException validationException = new ValidationException("Invalid question ID", HttpStatus.BAD_REQUEST);
        when(questionService.findQuestionById(anyInt())).thenThrow(validationException);

        ResponseEntity<?> response = questionController.findQuestionById(1);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid question ID", response.getBody());
    }

//    @Test
//    void testGetTopQuestions_Success() {
//        List<TopQuestionResponse> topQuestions = new ArrayList<>();
//        mockTopListQuestionsResponse(topQuestions);
//
//        when(questionService.findTopQuestions(anyInt())).thenReturn(topQuestions);
//
//        ResponseEntity<?> response = questionController.getTopQuestions(5);
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(topQuestions, response.getBody());
//    }

    @Test
    void testGetTopQuestions_Failure() {
        when(questionService.findTopQuestions(anyInt())).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<?> response = questionController.getTopQuestions(5);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

//    @Test
//    void testGetQuestionsByTag_Success() {
//        List<Question> questionsByTag = new ArrayList<>();
//        mockListQuestionsResponse(questionsByTag);
//
//        when(questionService.findQuestionsByTag(anyString())).thenReturn(questionsByTag);
//
//        ResponseEntity<?> response = questionController.findQuestionsByTag("java");
//
//        // Verify the response
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(questionsByTag, response.getBody());
//    }
//
//    private void mockTopListQuestionsResponse(List<TopQuestionResponse> topQuestions) {
//        TopQuestionResponse question1 = new TopQuestionResponse();
//        question1.setTitle("Title 1");
//        question1.setUser(1);
//        question1.setVotes(1);
//
//        TopQuestionResponse question2 = new TopQuestionResponse();
//        question2.setTitle("Title 2");
//        question2.setUser(1);
//        question2.setVotes(0);
//
//        topQuestions.add(question1);
//        topQuestions.add(question2);
//    }

    private void mockListQuestionsResponse(List<Question> questions) {
        Question question1 = new Question();
        question1.setTitle("Title 1");
        question1.setContent("Content 1");
        question1.setId(1);

        Question question2 = new Question();
        question2.setTitle("Title 2");
        question2.setContent("Content 2");
        question2.setId(2);

        questions.add(question1);
        questions.add(question2);
    }

    private Question getQuestionResponse() {
        Question mockQuestionResponse = new Question();
        mockQuestionResponse.setId(1);
        mockQuestionResponse.setTitle("Mock Title");
        mockQuestionResponse.setContent("Mock Content");
        return mockQuestionResponse;
    }

    private QuestionCreateRequest getPostQuestionInput() {
        QuestionCreateRequest postQuestionInput = new QuestionCreateRequest();
        postQuestionInput.setTitle("Mock Title");
        postQuestionInput.setContent("Mock Content");
        return postQuestionInput;
    }
}
