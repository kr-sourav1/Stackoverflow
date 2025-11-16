package com.stackoverflow.beta.service;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.request.PostAnswerRequest;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.service.impl.AnswerServiceImpl;
import com.stackoverflow.beta.service.impl.StorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private StorageService s3Service;

    @Mock
    private AnswerServiceImpl answerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        answerService = new AnswerServiceImpl(answerRepository, questionRepository, s3Service, kafkaTemplate);

        // Mock SecurityContext globally
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testSaveAnswerWithInvalidQuestionId() {
        PostAnswerRequest postAnswerRequest = new PostAnswerRequest(999, "Invalid question id answer.");
        when(questionRepository.findById(eq(999))).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            answerService.save(postAnswerRequest);
        });

        assertEquals("Question with given id doesn't exist 999", exception.getMessage());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // Clear context after each test to avoid interference
    }
}
