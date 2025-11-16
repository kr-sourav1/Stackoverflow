package com.stackoverflow.beta.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCreateRequest {

    @NonNull
    private String title;
    private String content;
    private List<String> tags;
}
