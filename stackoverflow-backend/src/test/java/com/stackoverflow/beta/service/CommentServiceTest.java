package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.request.PostCommentRequest;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.CommentRepository;
import com.stackoverflow.beta.service.impl.CommentServiceImpl;
import com.stackoverflow.beta.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveComment_Success() {
        PostCommentRequest request = PostCommentRequest.builder()
                .answerId(1)
                .comment("This is a comment")
                .build();

        // Mocking SecurityUtils to return a user ID
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getUserIdFromSecurityContext)
                    .thenReturn(Optional.of(123));

            // Mocking answer repository to return an Answer
            Answer mockAnswer = Answer.builder().id(1).content("Sample Answer").build();
            when(answerRepository.findById(request.getAnswerId())).thenReturn(Optional.of(mockAnswer));

            // Mocking comment repository to return a saved comment
            Comment mockComment = Comment.builder()
                    .id(1)
                    .text(request.getComment())
                    .userId(123)
                    .answer(mockAnswer)
                    .build();
            when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

            // Mock Kafka publishing (no need to mock publishToKafka directly)
            CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mock(SendResult.class));
            when(kafkaTemplate.send(anyString(), any())).thenReturn(future);

            // Call the method under test
            Comment savedComment = commentService.save(request);

            // Assertions
            assertNotNull(savedComment);
            assertEquals("This is a comment", savedComment.getText());
            assertEquals(123, savedComment.getUserId());
            assertEquals(mockAnswer, savedComment.getAnswer());

            // Verify interactions
            verify(answerRepository, times(1)).findById(request.getAnswerId());
            verify(commentRepository, times(1)).save(any(Comment.class));
            verify(kafkaTemplate, times(1)).send(anyString(), any());
        }
    }

}
