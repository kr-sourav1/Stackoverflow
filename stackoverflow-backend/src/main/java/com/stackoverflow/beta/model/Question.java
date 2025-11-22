package com.stackoverflow.beta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 500)
    @NotNull
    @Size(min = 1, message = "The title must be at least 1 characters long")
    private String title;

    @Column(length = 3000)
    @NotNull
    @Size(min = 5, message = "The content must be at least 5 characters long")
    private String content;
    private int votes;
    @Column(name = "user_id")
    private int userId;

    // Question.java (add these imports where necessary)
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @CreationTimestamp(source = SourceType.DB)
    private Date createdAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    /**
     * Marking tags as @Transient to exclude it from persistence
     */
    @ManyToMany
    @JoinTable(
            name = "question_tags",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    /**
     * check for lock
     */
    public void upvote() {
        this.votes = this.votes + 1;
    }

    public void downVote() {
        this.votes = this.votes - 1;
    }


    public Question(String title, String content, User askedBy) {
        this.title = title;
        this.content = content;
        this.userId = askedBy.getId();
    }

    public Question(String title, String content, int askedBy) {
        this.title = title;
        this.content = content;
        this.userId = askedBy;
    }
}
