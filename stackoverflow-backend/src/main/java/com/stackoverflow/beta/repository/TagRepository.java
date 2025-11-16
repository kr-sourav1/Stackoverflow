package com.stackoverflow.beta.repository;

import com.stackoverflow.beta.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM tag where name = :tagName")
    Tag findByName(String tagName);

    @Query(nativeQuery = true, value = "select t.name from tag t,question_tags qt where t.id=qt.tag_id and qt.question_id = :questionId")
    List<String> getTagNamesByQuestionId(int questionId);
}
