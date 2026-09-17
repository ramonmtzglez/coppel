package com.tvmaze.middleware.service;

import com.tvmaze.middleware.dto.ShowResponse;

import java.util.List;

public interface ShowService {

    List<ShowResponse> search(String query);
}
