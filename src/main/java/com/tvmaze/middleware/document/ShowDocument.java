package com.tvmaze.middleware.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "shows")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowDocument {

    @Id
    private Long id;

    private String name;
    private String channel;
    private String summary;
    private List<String> genres;
    private String status;
    private Integer runtime;
    private String premiered;
    private String ended;
    private Double rating;
    private String image;
    private String language;
    private String officialSite;
    private String url;

    private Instant cachedAt;
}
