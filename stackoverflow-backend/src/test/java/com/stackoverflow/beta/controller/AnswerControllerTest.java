package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.request.PostAnswerRequest;
import com.stackoverflow.beta.service.IAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AnswerControllerTest {

    @Mock
    private IAnswer answerService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AnswerController answerController = new AnswerController(answerService);
        mockMvc = MockMvcBuilders.standaloneSetup(answerController).build();
    }

//    @Test
//    void testPostAnswerWithoutMedia() throws Exception {
//        PostAnswerRequest postAnswerRequest = new PostAnswerRequest(1, "Test answer without media.");
//        Answer answer = new Answer(1, "Test answer without media.", 0, null, 1, null, null);
//        when(answerService.save(postAnswerRequest)).thenReturn(answer);
//
//        mockMvc.perform(post("/answer/post")
//                        .param("questionId", "1")
//                        .param("answer", "Test answer without media.")
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.content").value("Test answer without media."))
//                .andExpect(jsonPath("$.mediaUrl").doesNotExist());
//    }
//
//    @Test
//    void testPostAnswerWithMedia() throws Exception {
//        PostAnswerRequest postAnswerRequest = new PostAnswerRequest(1, "Test answer with media.");
//        MultipartFile file = mock(MultipartFile.class);
//        String mediaUrl = "http://example.com/media";
//        Answer answer = new Answer(1, "Test answer with media.", 0, null, 1, mediaUrl, null);
//        when(answerService.saveWithMedia(eq(file), eq(postAnswerRequest))).thenReturn(answer);
//
//        mockMvc.perform(post("/answer/post")
//                        .param("questionId", "1")
//                        .param("answer", "Test answer with media.")
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(status().isCreated());
//    }
}
