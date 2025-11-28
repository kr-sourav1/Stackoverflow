package com.stackoverflow.beta.repository;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndPostTypeAndPostId(Integer userId,
                                                    PostType postType,
                                                    Integer postId);
}
