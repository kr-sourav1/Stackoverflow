package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.service.CastVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
    private final VoteFactory voteFactory;

    @Autowired
    public VoteService(VoteFactory voteFactory) {
        this.voteFactory = voteFactory;
    }

    public int upVote(PostType postType, int postId, int userId) {
        CastVote castVote = voteFactory.getVoteService(postType);
        return castVote.upVote(postId, userId);
    }

    public int downVote(PostType postType, int postId, int userId) {
        CastVote castVote = voteFactory.getVoteService(postType);
        return castVote.downVote(postId, userId);
    }
}
