package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Tag;
import com.stackoverflow.beta.repository.TagRepository;
import com.stackoverflow.beta.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class TagServiceTest {
    @Mock
    private TagRepository tagRepository;
    @InjectMocks
    private TagServiceImpl tagsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTagByName_Success() {
        String tagName = "Java";
        Tag expectedTag = new Tag();
        expectedTag.setId(1);
        expectedTag.setName(tagName);

        when(tagRepository.findByName(tagName)).thenReturn(expectedTag);

        Tag result = tagsService.getByName(tagName);

        assertNotNull(result);
        assertEquals(tagName, result.getName());
    }

    @Test
    void testCreateTag_Success() {
        Tag newTag = new Tag();
        newTag.setName("Java");

        Tag savedTag = new Tag();
        savedTag.setId(1);
        savedTag.setName("Java");

        when(tagRepository.save(newTag)).thenReturn(savedTag);

        Tag result = tagsService.create(newTag);

        assertNotNull(result);
        assertEquals("Java", result.getName());
    }
}
