# Similar Movies

## Context
Users currently have no way to discover movies similar to ones they are interested in. A `Similar Movies` recommendation improves content discovery and engagement.

## Feature Request
On the movie details page, show a `Similar Movies` list for a given movie, based on shared genres.

## Requirements
- Maximum 20 movies returned.
- Exclude the given movie itself from the results.
- Find movies that share at least one genre with the given movie.
- Rank by the number of matching genres. More matches rank higher.
  - Example: if the given movie has Action and Comedy genres, a movie with both Action and Comedy ranks higher than a movie with only Action or only Comedy.
- Break ties using `voteCount` in descending order.
