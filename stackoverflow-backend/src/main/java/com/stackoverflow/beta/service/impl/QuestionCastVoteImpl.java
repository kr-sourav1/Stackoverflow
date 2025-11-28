package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.Vote;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.repository.VoteRepository;
import com.stackoverflow.beta.service.CastVote;
import com.stackoverflow.beta.utils.CustomPriorityQueue;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class QuestionCastVoteImpl implements CastVote {

    private final QuestionRepository questionRepository;
    private final VoteRepository voteRepository;
    private final CustomPriorityQueue customPriorityQueue = CustomPriorityQueue.getInstance();

    @Autowired
    public QuestionCastVoteImpl(QuestionRepository questionRepository,
                                VoteRepository voteRepository) {
        this.questionRepository = questionRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    @Transactional
    public int upVote(int id, int userId) {
        log.info("Attempting to upvote question with ID: {} by user: {}", id, userId);
        Question question = getQuestion(id);
        PostType postType = PostType.QUESTION;

        Optional<Vote> existingOpt =
                voteRepository.findByUserIdAndPostTypeAndPostId(userId, postType, id);

        int delta = 0;

        if (existingOpt.isEmpty()) {
            // user has never voted → create new upvote
            Vote v = new Vote();
            v.setUserId(userId);
            v.setPostType(postType);
            v.setPostId(id);
            v.setValue(1);
            voteRepository.save(v);
            delta = +1;
        } else {
            Vote existing = existingOpt.get();
            if (existing.getValue() == 1) {
                // already upvoted → clicking again removes vote
                voteRepository.delete(existing);
                delta = -1;
            } else if (existing.getValue() == -1) {
                // was downvoted → now switching to upvote
                existing.setValue(1);
                delta = +2; // -1 → +1
            }
        }

        if (delta != 0) {
            question.setVotes(question.getVotes() + delta);
            question = questionRepository.save(question);

            if (customPriorityQueue.containsQuestion(question)) {
                customPriorityQueue.updateQuestion(question);
            } else {
                customPriorityQueue.add(question);
            }
        }

        log.info("Final vote count for question {}: {}", id, question.getVotes());
        return question.getVotes();
    }

    @Override
    @Transactional
    public int downVote(int id, int userId) {
        log.info("Attempting to downvote question with ID: {} by user: {}", id, userId);
        Question question = getQuestion(id);
        PostType postType = PostType.QUESTION;

        Optional<Vote> existingOpt =
                voteRepository.findByUserIdAndPostTypeAndPostId(userId, postType, id);

        int delta = 0;

        if (existingOpt.isEmpty()) {
            // first time downvote
            Vote v = new Vote();
            v.setUserId(userId);
            v.setPostType(postType);
            v.setPostId(id);
            v.setValue(-1);
            voteRepository.save(v);
            delta = -1;
        } else {
            Vote existing = existingOpt.get();
            if (existing.getValue() == -1) {
                // already downvoted → clicking again removes vote
                voteRepository.delete(existing);
                delta = +1;
            } else if (existing.getValue() == 1) {
                // was upvoted → now switching to downvote
                existing.setValue(-1);
                delta = -2; // +1 → -1
            }
        }

        if (delta != 0) {
            question.setVotes(question.getVotes() + delta);
            question = questionRepository.save(question);

            if (customPriorityQueue.containsQuestion(question)) {
                customPriorityQueue.updateQuestion(question);
            } else {
                customPriorityQueue.add(question);
            }
        }

        log.info("Final vote count for question {}: {}", id, question.getVotes());
        return question.getVotes();
    }

    private Question getQuestion(int id) {
        log.debug("Fetching question with ID: {}", id);
        return questionRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Question not posted so far!!", HttpStatus.BAD_REQUEST));
    }
}
