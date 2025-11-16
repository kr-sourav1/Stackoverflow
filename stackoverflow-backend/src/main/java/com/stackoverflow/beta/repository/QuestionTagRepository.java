//package com.stackoverflow.beta.repository;
//
//import com.stackoverflow.beta.model.Question;
//import com.stackoverflow.beta.model.QuestionTags;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface QuestionTagRepository extends JpaRepository<QuestionTags, Integer> {
//    @Query(value = "SELECT * FROM question_tags WHERE tag_id = :tagId", nativeQuery = true)
//    List<QuestionTags> findByTagId(int tagId);
//
//    @Query(value = "SELECT COUNT(*) FROM question_tags WHERE question_id = :questionId AND tag_id = :tagId", nativeQuery = true)
//    int countByQuestionIdAndTagId(@Param("questionId") int questionId, @Param("tagId") int tagId);
//
//}
