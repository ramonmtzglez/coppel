package com.tvmaze.middleware.dto;

import java.util.List;

public record ShowDetailResponse(
        Long id,
        String name,
        String channel,
        String summary,
        List<String> genres,
        String status,
        Integer runtime,
        String premiered,
        String ended,
        Double rating,
        String image,
        String language,
        String officialSite,
        String url
) {
}
