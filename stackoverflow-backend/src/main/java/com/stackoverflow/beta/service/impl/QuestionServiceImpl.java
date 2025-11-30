package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.Tag;
import com.stackoverflow.beta.model.dto.QuestionResponse;
import com.stackoverflow.beta.model.request.QuestionCreateRequest;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.service.IQuestion;
import com.stackoverflow.beta.utils.CustomPriorityQueue;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import java.awt.print.Pageable;
import java.util.*;

import static com.stackoverflow.beta.utils.SecurityUtils.getUserIdFromSecurityContext;

@Service
@Slf4j
public class QuestionServiceImpl implements IQuestion {

    private final QuestionRepository questionRepository;
//    private final QuestionTagRepository questionTagRepository;
    private final TagServiceImpl tagService;
    private final CustomPriorityQueue voteCache = CustomPriorityQueue.getInstance();
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StorageService s3Service;

    @PostConstruct
    private void initializePriorityQueue() {
        List<Question> allQuestions = questionRepository.findAll();
        voteCache.addAll(allQuestions);
    }

    public QuestionServiceImpl(QuestionRepository questionRepository,
//                               QuestionTagRepository questionTagRepository,
                               TagServiceImpl tagService,
                               KafkaTemplate<String, Object> kafkaTemplate,
                               StorageService s3Service) {
        this.questionRepository = questionRepository;
//        this.questionTagRepository = questionTagRepository;
        this.tagService = tagService;
        this.kafkaTemplate = kafkaTemplate;
        this.s3Service = s3Service;
    }

    @Override
    public Question saveQuestion(QuestionCreateRequest questionCreateRequest) {
        int userId = getUserIdFromSecurityContext()
                .orElseThrow(() -> new SecurityException("User not authenticated"));

        Question question = createQuestion(questionCreateRequest);
        question.setUserId(userId);

        validateQuestionDoesNotExist(questionCreateRequest, userId);
        question.setTags(buildTags(questionCreateRequest.getTags()));

        Question savedQuestion = questionRepository.save(question);
        log.info("Question successfully saved with ID: {}", savedQuestion.getId());

        voteCache.add(savedQuestion);
        return savedQuestion;
    }

    // 👇 NEW: same as saveQuestion, but with a file parameter
    @Override
    public Question saveQuestionWithMedia(MultipartFile file, QuestionCreateRequest questionCreateRequest) {
        int userId = getUserIdFromSecurityContext()
                .orElseThrow(() -> new SecurityException("User not authenticated"));

        Question question = createQuestion(questionCreateRequest);
        question.setUserId(userId);

        validateQuestionDoesNotExist(questionCreateRequest, userId);
        question.setTags(buildTags(questionCreateRequest.getTags()));

        if (file != null && !file.isEmpty()) {
            String mediaUrl = s3Service.uploadFile(file);   // 👈 exactly like AnswerServiceImpl
            question.setMediaUrl(mediaUrl);
        }

        Question savedQuestion = questionRepository.save(question);
        log.info("Question with media successfully saved with ID: {}", savedQuestion.getId());

        voteCache.add(savedQuestion);
        return savedQuestion;
    }


    private Set<Tag> buildTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = fetchOrCreateTag(tagName);
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public List<Question> findQuestionsByTag(String tagName) {
//        log.info("Fetching questions for tag: {}", tagName);
//        Tag tag = fetchTagByName(tagName);
//
//        if(Objects.isNull(tag)){
//            log.info("Tag {} does not exist.", tagName);
//            return new ArrayList<>();
//        }
//
//        return questionTagRepository.findByTagId(tag.getId()).stream()
//                .map(questionTags -> {
//                    Question question = fetchQuestionById(questionTags.getQuestionId());
//                    List<String> tags = tagService.findAllTagsByQuestionId(question.getId());
//                    linkTagsToQuestion(question, tags);
//                    return question;
//                })
//                .toList();
       return null;

    }
    @Override
    public Optional<Question> findQuestionById(int id) {
        log.info("Fetching question with ID: {}", id);
        return Optional.ofNullable(questionRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Question not found", HttpStatus.NOT_FOUND)));
    }

    //    public List<TopQuestionResponse> findTopQuestions(int count) {
//        log.info("Fetching top {} questions based on criteria votes", count);
//        List<Question> topQuestions = new ArrayList<>(voteCache);
//
//        return topQuestions.stream()
//                .sorted((question1, question2) -> question2.getVotes() - question1.getVotes())
//                .limit(count)
//                .map(question -> new TopQuestionResponse(
//                        question.getTitle(),
//                        question.getVotes(),
//                        question.getUserId()))
//                .toList();
//    }

    @Override
    public QuestionResponse findTopQuestions(int limit) {
        // Get top N questions sorted by votes (or any field)
        List<Question> questions = questionRepository.findTopQuestionsByVotes();
        questions = questions.stream().limit(limit).toList();

        return QuestionResponse.builder()
                .questions(questions)
                .build();
    }

    private void validateQuestionDoesNotExist(QuestionCreateRequest questionCreateRequest, int userId) {
        boolean exists = questionRepository.countByTitleContentAndUserId(
                questionCreateRequest.getTitle(), questionCreateRequest.getContent(), userId) > 0;

        if (exists) {
            log.error("Question with title '{}' already exists.", questionCreateRequest.getTitle());
            throw new ValidationException("Question already exists!", HttpStatus.BAD_REQUEST);
        }
    }

    private Question createQuestion(QuestionCreateRequest input) {
        return Question.builder()
                .title(input.getTitle())
                .content(input.getContent())
                .build();
    }

    private Tag fetchOrCreateTag(String tagName) {
        return Optional.ofNullable(tagService.getByName(tagName))
                .orElseGet(() -> tagService.create(Tag.builder().name(tagName).build()));
    }
}
