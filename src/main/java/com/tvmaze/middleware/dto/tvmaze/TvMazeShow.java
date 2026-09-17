package com.tvmaze.middleware.dto.tvmaze;

import java.util.List;

public record TvMazeShow(
        Long id,
        String name,
        List<String> genres,
        String summary,
        TvMazeNetwork network,
        TvMazeWebChannel webChannel
) {
}
