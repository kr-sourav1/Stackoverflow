package com.stackoverflow.beta.service;

public interface CastVote {

    /**
     * Upvotes and returns the updated vote count.
     *
     * @param postId the ID of the answer/question to be upvoted
     * @param userId the ID of the user who is voting
     */
    int upVote(int postId, int userId);

    /**
     * Downvotes and returns the updated vote count.
     *
     * @param postId the ID of the answer/question to be downvoted
     * @param userId the ID of the user who is voting
     */
    int downVote(int postId, int userId);
}

