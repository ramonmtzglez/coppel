package com.tvmaze.middleware.service;

import com.tvmaze.middleware.dto.ShowDetailResponse;
import com.tvmaze.middleware.dto.ShowResponse;

import java.util.List;

public interface ShowService {

    List<ShowResponse> search(String query);

    ShowDetailResponse getShow(Long id);
}
