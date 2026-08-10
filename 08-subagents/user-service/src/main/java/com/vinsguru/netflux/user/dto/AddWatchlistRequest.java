package com.vinsguru.netflux.user.dto;

import jakarta.validation.constraints.NotNull;

public record AddWatchlistRequest(@NotNull Long movieId) {
}
