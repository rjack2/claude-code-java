package com.vinsguru.netflux.user.mapper;

import com.vinsguru.netflux.user.entity.Watchlist;

public class WatchlistMapper {

    private WatchlistMapper() {
    }

    public static Watchlist toEntity(Long userId, Long movieId) {
        var watchlist = new Watchlist();
        watchlist.setUserId(userId);
        watchlist.setMovieId(movieId);
        return watchlist;
    }

}
