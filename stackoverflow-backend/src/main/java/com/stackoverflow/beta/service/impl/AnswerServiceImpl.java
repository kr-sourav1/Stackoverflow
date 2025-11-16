package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.request.PostAnswerRequest;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.service.IAnswer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.stackoverflow.beta.utils.SecurityUtils.getUserIdFromSecurityContext;

@Slf4j
@Service
public class AnswerServiceImpl implements IAnswer {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final StorageService s3Service;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AnswerServiceImpl(AnswerRepository answerRepository,
                             QuestionRepository questionRepository,
                             StorageService s3Service,
                             KafkaTemplate<String, Object> kafkaTemplate) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.s3Service = s3Service;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Answer save(PostAnswerRequest postAnswerRequest) {

        Optional<Question> optionalQuestion = questionRepository.findById(postAnswerRequest.getQuestionId());
        if (optionalQuestion.isEmpty()) {
            throw new ValidationException("Question with given id doesn't exist " +
                    postAnswerRequest.getQuestionId(), HttpStatus.BAD_REQUEST);
        }

        Optional<Integer> userIdFromSecurityContext = getUserIdFromSecurityContext();
        if (userIdFromSecurityContext.isEmpty()) {
            throw new ValidationException("UserId" + userIdFromSecurityContext.get() +
                    " doesn't exist ", HttpStatus.BAD_REQUEST);
        }

        Question question = optionalQuestion.get();
        Integer userId = userIdFromSecurityContext.get();

        Answer answer = Answer.builder()
                .userId(userId)
                .question(question)
                .content(postAnswerRequest.getAnswer())
                .build();

        // Saving the Answer object to the repository
        Answer savedAnswer = answerRepository.save(answer);
        log.info("Answer successfully saved with answerId = {}", savedAnswer.getId());

        // Publishing the saved answer to Elasticsearch
        return savedAnswer;
    }

    @Override
    public Answer saveWithMedia(MultipartFile file, PostAnswerRequest postAnswerRequest) {
        String mediaUrl = s3Service.uploadFile(file);
        Optional<Question> optionalQuestion = questionRepository.findById(postAnswerRequest.getQuestionId());
        if (optionalQuestion.isEmpty()) {
            throw new ValidationException("Question with given id doesn't exist " + postAnswerRequest.getQuestionId(), HttpStatus.BAD_REQUEST);
        }

        Optional<Integer> userIdFromSecurityContext = getUserIdFromSecurityContext();
        if (userIdFromSecurityContext.isEmpty()) {
            throw new ValidationException("UserId" + userIdFromSecurityContext.get() +
                    " doesn't exist ", HttpStatus.BAD_REQUEST);
        }

        Question question = optionalQuestion.get();
        Answer answer = Answer.builder()
                .userId(userIdFromSecurityContext.get())
                .question(question)
                .content(postAnswerRequest.getAnswer())
                .mediaUrl(mediaUrl)
                .build();

        // Saving the Answer object to the repository
        Answer savedAnswer = answerRepository.save(answer);
        log.info("Answer successfully saved with answerId={}", savedAnswer.getId());

        // Publishing the saved answer to Elasticsearch
        return savedAnswer;
    }

    @Override
    public Answer getAnswerById(int id) {
        return answerRepository.findById(id).orElseThrow(() ->
                new ValidationException("Answer not found", HttpStatus.BAD_REQUEST));
    }

}
