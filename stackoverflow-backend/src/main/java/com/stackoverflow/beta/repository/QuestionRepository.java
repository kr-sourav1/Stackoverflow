package com.stackoverflow.beta.repository;

import com.stackoverflow.beta.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query(value = "SELECT COUNT(*) FROM question WHERE title = :title AND content = :content AND user_id = :userId", nativeQuery = true)
    int countByTitleContentAndUserId(String title, String content, int userId);

    @Query(value = "SELECT * FROM question WHERE id IN (:questionIds)", nativeQuery = true)
    List<Question> fetchQuestionList(@Param("questionIds") List<Integer> questionIds);

    @Query(value = "SELECT DISTINCT q.id FROM question AS q " +
            "LEFT JOIN answer AS a ON q.id = a.question_id " +
            "LEFT JOIN comment AS c ON a.id = c.answer_id " +
            "LEFT JOIN question_tags AS qt ON qt.question_id = q.id " +
            "LEFT JOIN tag AS t ON t.id = qt.tag_id " +
            "WHERE q.content LIKE %:query% OR q.title LIKE %:query% OR " +
            "a.content LIKE %:query% OR c.text LIKE %:query% OR t.name LIKE %:query%", nativeQuery = true)
    List<Integer> searchQuestion(@Param("query") String query);
}
