package com.stackoverflow.beta.model;

import com.stackoverflow.beta.PostType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "post_type", "post_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which user voted
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // QUESTION or ANSWER
    @Enumerated(EnumType.STRING)
    @Column(name = "post_type", nullable = false, length = 20)
    private PostType postType;

    // ID of the question or answer
    @Column(name = "post_id", nullable = false)
    private Integer postId;

    // +1 = upvote, -1 = downvote
    @Column(name = "value", nullable = false)
    private int value;
}
