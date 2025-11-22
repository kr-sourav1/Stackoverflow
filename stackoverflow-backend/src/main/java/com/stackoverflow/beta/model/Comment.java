package com.stackoverflow.beta.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    // Comment.java
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    @JsonIgnore
    private User user;

//    @Transient
//    private String userName;
//
//    @JsonProperty("user")
//    public String getUserJsonName() {
//        return this.userName;
//    }


    @Column(length = 2000)
    private String text;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answerId")
    private Answer answer;

}
