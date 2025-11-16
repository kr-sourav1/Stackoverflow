package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.QuestionTags;
import com.stackoverflow.beta.model.Tag;
import com.stackoverflow.beta.model.elastic.QuestionESModel;
import com.stackoverflow.beta.model.request.QuestionCreateRequest;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.repository.QuestionTagRepository;
import com.stackoverflow.beta.service.impl.ElasticSynchronizer;
import com.stackoverflow.beta.service.impl.QuestionServiceImpl;
import com.stackoverflow.beta.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private QuestionTagRepository questionTagRepository;

    @Mock
    private ElasticSynchronizer elasticSynchronizer;

    @Mock
    private IUserService userService;
    @Mock
    private ITag tagsService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveQuestion_Success() {
        QuestionCreateRequest input = getQuestionCreateRequest();
        Question question = savedQuestion();

        // Mock user existence check
        when(userService.isUserExist(123)).thenReturn(true);

        // Mock duplicate question validation
        when(questionRepository.countByTitleContentAndUserId(input.getTitle(), input.getContent(), 123)).thenReturn(0);

        // Mock question saving
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Mock tag retrieval and creation
        Tag tag1 = new Tag(1, "Tag1");
        when(tagsService.getByName("Tag1")).thenReturn(tag1);
        when(tagsService.create(any(Tag.class))).thenReturn(tag1);

        // Mock question-tags association saving
        QuestionTags questionTags = new QuestionTags();
        questionTags.setQuestionId(1);
        questionTags.setTagId(1);
        questionTags.setId(1);
        when(questionTagRepository.save(any(QuestionTags.class))).thenReturn(questionTags);

        // Mock KafkaTemplate send method
        QuestionESModel esModel = new QuestionESModel();
        when(elasticSynchronizer.toQuestionESModel(any(Question.class), anyList())).thenReturn(esModel);

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq("stackoverflow-question"), eq(esModel))).thenReturn(future);

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            // Mock the static method to return a user ID
            mockedStatic.when(SecurityUtils::getUserIdFromSecurityContext)
                    .thenReturn(Optional.of(123));

            // Call the method under test
            Question result = questionService.saveQuestion(input);

            // Assertions
            assertNotNull(result);
            assertEquals("Title1", result.getTitle());
            assertEquals("Content1", result.getContent());

            // Verify interactions
            verify(questionRepository, times(1)).save(any(Question.class));
            verify(kafkaTemplate, times(1)).send(eq("stackoverflow-question"), eq(esModel));
        }
    }


    @Test
    void testGetQuestionsByTag_Success() {
        String tagInput = "Tag1";
        Tag tag1 = new Tag(1, tagInput);
        Question question = new Question();
        question.setId(1);
        question.setTitle("Test Question");

        when(tagsService.getByName(tagInput)).thenReturn(tag1);
        when(questionTagRepository.findByTagId(tag1.getId())).thenReturn(List.of(new QuestionTags(1, 1, 1)));
        when(questionRepository.findById(1)).thenReturn(Optional.of(question));

        List<Question> result = questionService.findQuestionsByTag(tagInput);

        assertEquals(1, result.size());
        assertEquals(question.getTitle(), result.get(0).getTitle());
    }

    @Test
    void testGetQuestionById_Success() {
        int questionId = 1;
        Question question = new Question();
        question.setId(questionId);
        question.setTitle("Test Question");

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        Optional<Question> result = questionService.findQuestionById(questionId);

        assertNotNull(result);
        assertEquals("Test Question", result.get().getTitle());
    }

    private Question savedQuestion() {
        Question question = new Question();
        question.setId(1);
        question.setContent("Content1");
        question.setTitle("Title1");
        question.setVotes(0);
        return question;
    }

    private QuestionCreateRequest getQuestionCreateRequest() {
        return new QuestionCreateRequest("Title1", "Content1", Arrays.asList("tag1", "tag2"));
    }
}
