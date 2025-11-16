package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.model.Tag;
import com.stackoverflow.beta.repository.TagRepository;
import com.stackoverflow.beta.service.ITag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TagServiceImpl implements ITag {
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag getByName(String name) {
        log.info("Fetching tag by name: {}", name);
        return tagRepository.findByName(name);
    }

    @Override
    public Tag create(Tag tag) {
        log.info("Creating new tag with name: {}", tag.getName());
        return tagRepository.save(tag);
    }

    public List<String> findAllTagsByQuestionId(int questionId){
        return tagRepository.getTagNamesByQuestionId(questionId);
    }
}
