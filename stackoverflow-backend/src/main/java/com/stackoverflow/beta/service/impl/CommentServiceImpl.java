package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.request.PostCommentRequest;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.CommentRepository;
import com.stackoverflow.beta.service.IComment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.stackoverflow.beta.utils.SecurityUtils.getUserIdFromSecurityContext;


@Service
@Slf4j
public class CommentServiceImpl implements IComment {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;


    public CommentServiceImpl(CommentRepository commentRepository,
                              AnswerRepository answerRepository) {
        this.commentRepository = commentRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public Comment save(PostCommentRequest postCommentRequest) {
        Optional<Integer> userIdFromSecurityContext = getUserIdFromSecurityContext();
        if (userIdFromSecurityContext.isEmpty()) {
            throw new ValidationException("UserId" + userIdFromSecurityContext.get() +
                    " doesn't exist ", HttpStatus.BAD_REQUEST);
        }

        Optional<Answer> optionalAnswer = answerRepository.findById(postCommentRequest.getAnswerId());
        if (optionalAnswer.isEmpty()) {
            log.error("Answer with ID: {} not found", postCommentRequest.getAnswerId());
            throw new ValidationException("Answer with given id doesn't exist " + postCommentRequest.getAnswerId(), HttpStatus.BAD_REQUEST);
        }

        Comment comment = Comment.builder()
                .text(postCommentRequest.getComment())
                .userId(userIdFromSecurityContext.get())
                .answer(optionalAnswer.get())
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment successfully saved with commentId={}", savedComment.getId());

        // Publishing the saved answer to Elasticsearch
        return comment;
    }

}
