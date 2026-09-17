package com.tvmaze.middleware.controller;

import com.tvmaze.middleware.dto.ShowDetailResponse;
import com.tvmaze.middleware.dto.ShowResponse;
import com.tvmaze.middleware.service.ShowService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
@Validated
public class ShowController {

    private final ShowService showService;

    @GetMapping("/search")
    public ResponseEntity<List<ShowResponse>> search(
            @RequestParam("q") @NotBlank(message = "search query must not be blank") String q) {

        return ResponseEntity.ok(showService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowDetailResponse> getShow(
            @PathVariable @Positive(message = "show id must be positive") Long id) {

        return ResponseEntity.ok(showService.getShow(id));
    }
}
