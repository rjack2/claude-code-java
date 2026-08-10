package com.vinsguru.netflux.movie.dto;

import jakarta.validation.constraints.AssertTrue;

import java.util.Objects;

public record SearchRequest(String title,
                             String genre,
                             Integer limit) {

    @AssertTrue(message = "At least one of title or genre is required")
    public boolean isAtLeastOnePresent() {
        return Objects.nonNull(title) || Objects.nonNull(genre);
    }

}
