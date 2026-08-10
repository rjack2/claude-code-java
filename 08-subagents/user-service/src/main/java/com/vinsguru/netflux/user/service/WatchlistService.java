package com.vinsguru.netflux.user.service;

import com.vinsguru.netflux.user.entity.Watchlist;
import com.vinsguru.netflux.user.mapper.WatchlistMapper;
import com.vinsguru.netflux.user.repository.WatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    public WatchlistService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public List<Long> getWatchlist(Long userId) {
        return watchlistRepository.findByUserId(userId).stream()
                .map(Watchlist::getMovieId)
                .toList();
    }

    @Transactional
    public void addToWatchlist(Long userId, Long movieId) {
        if (watchlistRepository.existsByUserIdAndMovieId(userId, movieId)) {
            return;
        }
        watchlistRepository.save(WatchlistMapper.toEntity(userId, movieId));
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long movieId) {
        watchlistRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

}
