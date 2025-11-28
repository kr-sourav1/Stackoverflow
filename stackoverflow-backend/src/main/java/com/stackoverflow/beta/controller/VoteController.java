package com.stackoverflow.beta.controller;


import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.service.impl.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/vote")
@CrossOrigin(origins = "http://localhost:5173/")
public class VoteController {
    private final VoteService votingService;

    @Autowired
    public VoteController(VoteService votingService) {
        this.votingService = votingService;
    }

    /**
     * Endpoint to upvote a post (either a question or an answer).
     *
     * @param postType - the type of post to vote for (question or answer)
     * @param postId   - the ID of the post to be upvoted
     * @return the updated vote count after the upvote
     */
    @PostMapping("/upVote")
    public ResponseEntity<?> upVote(@RequestParam PostType postType,
                                    @RequestParam int postId,
                                    @RequestParam int userId) {
        try {
            int updatedVotes = votingService.upVote(postType, postId, userId);
            return new ResponseEntity<>(updatedVotes, HttpStatus.OK);
        } catch (ValidationException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/downVote")
    public ResponseEntity<?> downVote(@RequestParam PostType postType,
                                      @RequestParam int postId,
                                      @RequestParam int userId) {
        try {
            int updatedVotes = votingService.downVote(postType, postId, userId);
            return new ResponseEntity<>(updatedVotes, HttpStatus.OK);
        } catch (ValidationException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
