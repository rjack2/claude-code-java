package com.vinsguru.netflux.movie.controller;

import com.vinsguru.netflux.movie.dto.MovieDetails;
import com.vinsguru.netflux.movie.dto.MovieSummary;
import com.vinsguru.netflux.movie.dto.SearchRequest;
import com.vinsguru.netflux.movie.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{id}")
    public MovieDetails getById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/batch")
    public List<MovieSummary> getBatch(@RequestParam @Size(max = 100) List<Long> ids) {
        return movieService.getMoviesByIds(ids);
    }

    @GetMapping("/popular")
    public List<MovieSummary> getPopular(@RequestParam(required = false) Integer limit) {
        return movieService.getPopularMovies(limit);
    }

    @GetMapping("/latest")
    public List<MovieSummary> getLatest(@RequestParam(required = false) Integer limit) {
        return movieService.getLatestMovies(limit);
    }

    @GetMapping("/search")
    public List<MovieSummary> search(@Valid SearchRequest request) {
        return movieService.searchMovies(request);
    }

}
