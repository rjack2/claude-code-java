package com.vinsguru.netflux.movie.service;

import com.vinsguru.netflux.movie.dto.MovieDetails;
import com.vinsguru.netflux.movie.dto.MovieSummary;
import com.vinsguru.netflux.movie.dto.SearchRequest;
import com.vinsguru.netflux.movie.entity.Movie;
import com.vinsguru.netflux.movie.exceptions.MovieNotFoundException;
import com.vinsguru.netflux.movie.mapper.MovieMapper;
import com.vinsguru.netflux.movie.repository.MovieRepository;
import com.vinsguru.netflux.movie.repository.MovieSpecifications;
import com.vinsguru.netflux.movie.util.LimitUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private static final String FIELD_VOTE_COUNT = "voteCount";
    private static final String FIELD_RELEASE_DATE = "releaseDate";

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public MovieDetails getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(MovieMapper::toDetails)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public List<MovieSummary> getMoviesByIds(List<Long> ids) {
        return movieRepository.findAllById(ids).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> getPopularMovies(Integer limit) {
        var pageable = PageRequest.of(0, LimitUtil.clamp(limit), Sort.by(Sort.Direction.DESC, FIELD_VOTE_COUNT));
        return movieRepository.findAll(pageable).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> getLatestMovies(Integer limit) {
        var pageable = PageRequest.of(0, LimitUtil.clamp(limit), Sort.by(Sort.Direction.DESC, FIELD_RELEASE_DATE));
        return movieRepository.findAll(pageable).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> searchMovies(SearchRequest request) {
        var spec = Specification.<Movie>where(MovieSpecifications.titleContains(request.title()))
                .and(MovieSpecifications.hasGenre(request.genre()));
        var pageable = PageRequest.of(0, LimitUtil.clamp(request.limit()));
        return movieRepository.findAll(spec, pageable).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

}
