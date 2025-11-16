//package com.stackoverflow.beta.consumer;
//
//import com.stackoverflow.beta.model.elastic.AnswerESModel;
//import com.stackoverflow.beta.model.elastic.CommentESModel;
//import com.stackoverflow.beta.model.elastic.QuestionESModel;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.DltHandler;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.annotation.RetryableTopic;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class KafkaMessageListener {
//
//    @Autowired
//    ElasticSynchronizer elasticSynchronizer;
//
//    @RetryableTopic(kafkaTemplate = "kafkaTemplate",
//            attempts = "2",
//            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
//    )
//    @KafkaListener(topics = "stackoverflow-question", groupId = "sof-group-1")
//    public void consume(QuestionESModel questionESModel) {
//        try {
//            elasticSynchronizer.syncQuestionDetailsToES(questionESModel);
//            log.info("msg consumed {}", questionESModel);
//        } catch (Exception e) {
//            //create an alert
//            log.error("Exception while publishing to elastic-search for questionId={}", questionESModel.getId(), e);
//        }
//    }
//    @RetryableTopic(kafkaTemplate = "kafkaTemplate",
//            attempts = "2",
//            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
//    )
//    @KafkaListener(topics = "stackoverflow-answer", groupId = "sof-group-2")
//    public void answerConsumer(AnswerESModel answerESModel) {
//        try {
//            elasticSynchronizer.syncAnswerToES(answerESModel);
//            log.info("Answer consumed {}", answerESModel.getId());
//        } catch (Exception e) {
//            //create an alert
//            log.error("Exception while publishing answer to elastic for answerId={}, exception={}"
//                   , answerESModel.getId(), e.getMessage());
//        }
//    }
//
//    @RetryableTopic(kafkaTemplate = "kafkaTemplate",
//            attempts = "2",
//            backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
//    )
//    @KafkaListener(topics = "stackoverflow-comment", groupId = "sof-group-3")
//    public void commentConsumer(CommentESModel commentESModel) {
//        log.info("Publishing comment with ID: {} to Elasticsearch", commentESModel.getId());
//        try {
//            elasticSynchronizer.syncCommentToES(commentESModel);
//        } catch (Exception e) {
//            //create an alert
//            log.error("Exception while publishing comments to elastic for commentId=+" + commentESModel.getId() + " ,exception=" + e.getMessage());
//        }
//    }
//
//    // Dead letter topic handlers for both QuestionESModel and Answer
//    @DltHandler
//    public void listenDLT(QuestionESModel questionESModel) {
//        log.info("DLT received: {}", questionESModel.getId());
//    }
//
//    @DltHandler
//    public void listenDLT(AnswerESModel answerESModel) {
//        log.info("DLT received: {}", answerESModel.getId());
//    }
//
//    @DltHandler
//    public void listenDLT(CommentESModel commentESModel) {
//        log.info("DLT received: {}", commentESModel.getId());
//    }
//}
