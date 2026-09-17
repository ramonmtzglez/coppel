package com.tvmaze.middleware.dto;

import java.util.List;

public record ShowResponse(
        Long id,
        String name,
        String channel,
        String summary,
        List<String> genres
) {
}
