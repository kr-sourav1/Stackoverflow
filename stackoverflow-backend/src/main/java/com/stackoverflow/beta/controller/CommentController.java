package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.request.PostCommentRequest;
import com.stackoverflow.beta.service.IComment;
import com.stackoverflow.beta.utils.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing comment.
 */
@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "http://localhost:5173/")
public class CommentController {
    private final IComment commentService;

    @Autowired
    public CommentController(IComment commentService) {
        this.commentService = commentService;
    }

    /**
     * Endpoint to post a comment.
     *
     * @param postCommentRequest - the request object containing comment details
     * @return ResponseEntity containing the created Comment object or error message
     */
    @PostMapping(path = "/post")
    public ResponseEntity<?> postComment(@RequestBody PostCommentRequest postCommentRequest) {
        try {
            Comment comment = commentService.save(postCommentRequest);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }

}
