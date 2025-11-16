package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.request.PostCommentRequest;
import com.stackoverflow.beta.service.IComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CommentControllerTest {
    @Mock
    private IComment commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostComment_Success() {
        PostCommentRequest postCommentInput = mockCommentRequest();
        Comment mockCommentResponse = mockCommentResponse();

        when(commentService.save(any(PostCommentRequest.class))).thenReturn(mockCommentResponse);

        ResponseEntity<?> response = commentController.postComment(postCommentInput);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockCommentResponse, response.getBody());
    }

    @Test
    void testPostComment_Failure() {
        PostCommentRequest postCommentInput = mockCommentRequest();

        when(commentService.save(any(PostCommentRequest.class))).thenThrow(new ValidationException("User not registered.", HttpStatus.BAD_REQUEST));

        ResponseEntity<?> response = commentController.postComment(postCommentInput);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    private Comment mockCommentResponse() {
        Comment mockCommentResponse = new Comment();
        mockCommentResponse.setId(1);
        mockCommentResponse.setUserId(1);
        mockCommentResponse.setText("This is a test comment.");
        return mockCommentResponse;
    }

    private PostCommentRequest mockCommentRequest() {
        PostCommentRequest postCommentInput = new PostCommentRequest();
        postCommentInput.setAnswerId(1);
        postCommentInput.setComment("This is a test comment.");
        return postCommentInput;
    }

}
