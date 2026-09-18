package com.tvmaze.middleware.mapper;

import com.tvmaze.middleware.document.ShowDocument;
import com.tvmaze.middleware.dto.CommentDto;
import com.tvmaze.middleware.dto.ShowDetailResponse;
import com.tvmaze.middleware.dto.ShowResponse;
import com.tvmaze.middleware.dto.tvmaze.TvMazeShow;

import java.time.Instant;
import java.util.List;

public final class ShowMapper {

    private ShowMapper() {
    }

    public static ShowResponse toResponse(TvMazeShow show, List<CommentDto> comments) {
        return new ShowResponse(
                show.id(),
                show.name(),
                resolveChannel(show),
                show.summary(),
                show.genres(),
                comments
        );
    }

    public static ShowDetailResponse toDetailResponse(TvMazeShow show, List<CommentDto> comments) {
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
                show.url(),
                comments
        );
    }

    public static ShowDocument toDocument(TvMazeShow show) {
        return ShowDocument.builder()
                .id(show.id())
                .name(show.name())
                .channel(resolveChannel(show))
                .summary(show.summary())
                .genres(show.genres())
                .status(show.status())
                .runtime(show.runtime())
                .premiered(show.premiered())
                .ended(show.ended())
                .rating(show.rating() != null ? show.rating().average() : null)
                .image(show.image() != null ? show.image().original() : null)
                .language(show.language())
                .officialSite(show.officialSite())
                .url(show.url())
                .cachedAt(Instant.now())
                .build();
    }

    public static ShowDetailResponse toDetailResponse(ShowDocument doc, List<CommentDto> comments) {
        return new ShowDetailResponse(
                doc.getId(),
                doc.getName(),
                doc.getChannel(),
                doc.getSummary(),
                doc.getGenres(),
                doc.getStatus(),
                doc.getRuntime(),
                doc.getPremiered(),
                doc.getEnded(),
                doc.getRating(),
                doc.getImage(),
                doc.getLanguage(),
                doc.getOfficialSite(),
                doc.getUrl(),
                comments
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
