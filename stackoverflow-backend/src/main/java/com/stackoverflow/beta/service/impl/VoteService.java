package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.model.Vote;
import com.stackoverflow.beta.model.dto.VoteResponse;
import com.stackoverflow.beta.repository.VoteRepository;
import com.stackoverflow.beta.service.CastVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
    private final VoteFactory voteFactory;
    private final VoteRepository voteRepository;

    @Autowired
    public VoteService(VoteFactory voteFactory, VoteRepository voteRepository) {
        this.voteFactory = voteFactory;
        this.voteRepository = voteRepository;
    }

    public VoteResponse upVote(PostType postType, int postId, int userId) {
        CastVote castVote = voteFactory.getVoteService(postType);
        int updatedVotes = castVote.upVote(postId, userId);

        int myVote = voteRepository
                .findByUserIdAndPostTypeAndPostId(userId, postType, postId)
                .map(Vote::getValue)
                .orElse(0);

        return new VoteResponse(updatedVotes, myVote);
    }

    public VoteResponse downVote(PostType postType, int postId, int userId) {
        CastVote castVote = voteFactory.getVoteService(postType);
        int updatedVotes = castVote.downVote(postId, userId);

        int myVote = voteRepository
                .findByUserIdAndPostTypeAndPostId(userId, postType, postId)
                .map(Vote::getValue)
                .orElse(0);

        return new VoteResponse(updatedVotes, myVote);
    }
}
