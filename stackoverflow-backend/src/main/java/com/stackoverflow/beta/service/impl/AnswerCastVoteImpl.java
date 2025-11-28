package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Vote;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.VoteRepository;
import com.stackoverflow.beta.service.CastVote;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AnswerCastVoteImpl implements CastVote {

    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;

    public AnswerCastVoteImpl(AnswerRepository repository,
                              VoteRepository voteRepository) {
        this.answerRepository = repository;
        this.voteRepository = voteRepository;
    }

    @Override
    @Transactional
    public int upVote(int id, int userId) {
        log.info("Attempting to upvote answer with ID: {} by user: {}", id, userId);
        Answer answer = getAnswer(id);
        PostType postType = PostType.ANSWER;

        Optional<Vote> existingOpt =
                voteRepository.findByUserIdAndPostTypeAndPostId(userId, postType, id);

        int delta = 0;

        if (existingOpt.isEmpty()) {
            // first time upvote
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
                // already upvoted → remove vote
                voteRepository.delete(existing);
                delta = -1;
            } else if (existing.getValue() == -1) {
                // was downvoted → now upvote
                existing.setValue(1);
                delta = +2;
            }
        }

        if (delta != 0) {
            answer.setVotes(answer.getVotes() + delta);
            answerRepository.save(answer);
        }

        log.info("Final vote count for answer {}: {}", id, answer.getVotes());
        return answer.getVotes();
    }

    @Override
    @Transactional
    public int downVote(int id, int userId) {
        log.info("Attempting to downvote answer with ID: {} by user: {}", id, userId);
        Answer answer = getAnswer(id);
        PostType postType = PostType.ANSWER;

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
                // already downvoted → remove
                voteRepository.delete(existing);
                delta = +1;
            } else if (existing.getValue() == 1) {
                // was upvoted → now downvote
                existing.setValue(-1);
                delta = -2;
            }
        }

        if (delta != 0) {
            answer.setVotes(answer.getVotes() + delta);
            answerRepository.save(answer);
        }

        log.info("Final vote count for answer {}: {}", id, answer.getVotes());
        return answer.getVotes();
    }

    private Answer getAnswer(int id) {
        log.debug("Fetching answer with ID: {}", id);
        return answerRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Answer not posted so far!!", HttpStatus.BAD_REQUEST));
    }
}
