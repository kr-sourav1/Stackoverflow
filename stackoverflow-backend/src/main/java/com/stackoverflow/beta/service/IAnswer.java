package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.request.PostAnswerRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IAnswer {

    /**
     * Saves an answer based on the provided input details.
     *
     * @param answerInput The input object containing answer details.
     * @return The saved Answer.
     */
    Answer save(PostAnswerRequest answerInput);

    /**
     * Saves an answer along with an associated media file.
     *
     * @param file          The media file associated with the answer.
     * @param answerContent The textual content of the answer.
     * @return The saved Answer with media.
     */
    Answer saveWithMedia(MultipartFile file, PostAnswerRequest answerContent);

    /**
     * Fetches an answer by its ID.
     *
     * @param answerId The ID of the answer to retrieve.
     * @return The Answer associated with the given ID.
     */
    Answer getAnswerById(int answerId);
}

