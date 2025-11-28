package com.stackoverflow.beta.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 3000)
    private String content;
    @Column(nullable = false)
    private int votes = 0;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId")
    private Question question;

    @Column(name = "user_id")
    private int userId;

    // Answer.java
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;


    private String mediaUrl;

    //todo try to make variable private
    @JsonManagedReference
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    /**
     * check for lock
     */
    public void upvote() {
        this.votes = this.votes + 1;
    }

    public void downVote() {
        this.votes = this.votes - 1;
    }
}
