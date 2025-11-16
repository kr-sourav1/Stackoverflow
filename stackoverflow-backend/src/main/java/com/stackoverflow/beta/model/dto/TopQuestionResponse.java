package com.stackoverflow.beta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopQuestionResponse {
    private String title;
    private int votes;
    private int user;
}
