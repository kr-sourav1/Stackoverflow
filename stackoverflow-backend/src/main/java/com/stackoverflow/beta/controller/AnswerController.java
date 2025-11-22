package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.request.PostAnswerRequest;
import com.stackoverflow.beta.service.IAnswer;
import com.stackoverflow.beta.utils.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for managing answers.
 */
@RestController
@RequestMapping("/answer")
@CrossOrigin(origins = "http://localhost:5173/")
public class AnswerController {
    private final IAnswer answerService;

    @Autowired
    public AnswerController(IAnswer answerService) {
        this.answerService = answerService;
    }

    /**
     * Endpoint to post an answer with optional media.
     *
     * @param file               Optional multipart file representing the media content.
     * @return ResponseEntity containing the created Answer object or error message.
     */
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postAnswer(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "questionId") int questionId,
            @RequestParam(value = "answer") String answerString) {
        try {
            PostAnswerRequest postAnswerRequest = new PostAnswerRequest(questionId, answerString);
            Answer answer;
            if (file != null) {
                // Save answer with media
                answer = answerService.saveWithMedia(file, postAnswerRequest);
            } else {
                // Save answer without media
                answer = answerService.save(postAnswerRequest);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(answer);
        } catch (Exception e) {
            return ExceptionHandler.handleException(e);
        }
    }
}
