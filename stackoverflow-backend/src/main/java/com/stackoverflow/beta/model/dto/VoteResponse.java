package com.stackoverflow.beta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VoteResponse {
    private int votes;
    private int myVote;
//    public VoteResponse(int votes, int myVote){
//        this.votes = votes;
//        this.myVote = myVote;
//    }
//    public int getVotes(){
//        return votes;
//    }
//    public int getMyVote(){
//        return myVote;
//    }
}
