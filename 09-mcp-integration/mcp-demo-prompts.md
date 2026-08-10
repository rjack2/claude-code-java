# PostgreSQL MCP Demo Prompts

Use the following prompts to explore the capabilities of the PostgreSQL MCP server with Claude Code.

## 1. Inspect the Database

```text
List all the tables and explain their structure.
```

---

## 2. Count Records

```text
How many movies are there in the movie table?
```

---

## 3. Filter Data

```text
How many Action movies are there?
```

---

## 4. Aggregation

```text
Group movies by genre and show the top 5 genres by movie count.
```

---

## 5. Complex Query

```text
Consider only the top 1,000 movies based on vote_count (descending). Group them by genre and show the top 5 genres by movie count.
```

---

## 6. Database Optimization

```text
The application frequently queries movies by genre.

Can you suggest any database optimizations?
```

---

## 7. Query Design

```
I need to implement a "Similar Movies" feature.

Requirements:

- Return the top 10 similar movies for a given movie id.
- Rank movies by the number of matching genres and matching themes.
- Movies with more matching genres and themes should have a higher rank.
- Break ties using vote_count in descending order.

Suggest an efficient PostgreSQL solution.
```
