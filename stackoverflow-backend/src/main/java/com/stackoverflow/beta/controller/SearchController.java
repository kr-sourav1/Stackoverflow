package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.constant.Constants;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.service.ISearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller for handling search operations.
 */
@RestController
@Slf4j
@RequestMapping("/search")
@CrossOrigin(origins = "http://localhost:5173/")
public class SearchController {
    private final ISearch searchService;

    @Autowired
    public SearchController(ISearch searchService) {
        this.searchService = searchService;
    }

    /**
     * Endpoint for searching based on a query.
     *
     * @param query The search query.
     * @return A ResponseEntity containing the search result or error message.
     */

//    @GetMapping(Constants.SEARCH_QUERY + "/{" + Constants.QUERY + "}")
//    public ResponseEntity<?> searchQuery(@PathVariable String query) {
//        try {
//            return new ResponseEntity<>(searchService.searchFromQuery(query.trim().toLowerCase()), HttpStatus.OK);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(e.getMessage());
//        }
//
//    }

    // GLOBAL SEARCH
    // exception handling - other exception handling you think
    // text based query support - raise exception if query param is number
    // text based search on comment/answer/tag also.
    // proper error message should appear on client
    // in case of blank string it shouldn't return anything
    @GetMapping(Constants.SEARCH_QUERY)
    public ResponseEntity<?> searchQuery(@RequestParam("query") String query) {
        try {
            List<Question> response = searchService.searchFromQuery(query.trim());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while searching ", e);
            return null;
        }

    }

}
