package com.tvmaze.middleware.dto.tvmaze;

import java.util.List;

public record TvMazeShow(
        Long id,
        String url,
        String name,
        String type,
        String language,
        List<String> genres,
        String status,
        Integer runtime,
        String premiered,
        String ended,
        String officialSite,
        TvMazeRating rating,
        TvMazeNetwork network,
        TvMazeWebChannel webChannel,
        TvMazeImage image,
        String summary
) {
}
