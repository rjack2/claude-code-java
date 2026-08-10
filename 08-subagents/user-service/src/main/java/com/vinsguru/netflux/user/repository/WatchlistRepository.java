package com.vinsguru.netflux.user.repository;

import com.vinsguru.netflux.user.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserId(Long userId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    void deleteByUserIdAndMovieId(Long userId, Long movieId);

}
