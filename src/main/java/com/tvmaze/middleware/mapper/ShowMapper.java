package com.tvmaze.middleware.mapper;

import com.tvmaze.middleware.dto.ShowDetailResponse;
import com.tvmaze.middleware.dto.ShowResponse;
import com.tvmaze.middleware.dto.tvmaze.TvMazeShow;

public final class ShowMapper {

    private ShowMapper() {
    }

    public static ShowResponse toResponse(TvMazeShow show) {
        return new ShowResponse(
                show.id(),
                show.name(),
                resolveChannel(show),
                show.summary(),
                show.genres()
        );
    }

    public static ShowDetailResponse toDetailResponse(TvMazeShow show) {
        return new ShowDetailResponse(
                show.id(),
                show.name(),
                resolveChannel(show),
                show.summary(),
                show.genres(),
                show.status(),
                show.runtime(),
                show.premiered(),
                show.ended(),
                show.rating() != null ? show.rating().average() : null,
                show.image() != null ? show.image().original() : null,
                show.language(),
                show.officialSite(),
                show.url()
        );
    }

    private static String resolveChannel(TvMazeShow show) {
        if (show.network() != null && show.network().name() != null) {
            return show.network().name();
        }
        if (show.webChannel() != null && show.webChannel().name() != null) {
            return show.webChannel().name();
        }
        return null;
    }
}
