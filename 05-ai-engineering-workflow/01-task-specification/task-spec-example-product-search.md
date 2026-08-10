# Search Products by Category

## Objective & Scope
- Add the ability for users to search products by category, and sort the results.

## Out of Scope
- Free text search across product names is out of scope for this task.

## API
- `GET /api/products/search`
- Query params:
  - `category`
    - Required
    - e.g. `electronics`
    - Returns `400 Bad Request` if missing
  - `sort`
    - Optional
    - Allowed values:
      - `bestSeller`
      - `price`
      - `rating`
    - Default: `bestSeller`
    - Invalid values fall back to `bestSeller`
- On success, return `200 OK`.
- Response model:
  - A list of matching products.
  - Empty list if nothing matches, not an error.

```json
[
  {
    "id": "p123",
    "name": "Wireless Mouse",
    "price": 25.99,
    "rating": 4.5
  }
]
```

## Core Business Logic & Rules
- Products that are out of stock should not show up in the search results, even if they match the category.

## Acceptance Criteria
- Category
  - Search by category returns matching products.
  - Missing category returns HTTP 400.
  - Out-of-stock products are excluded.
- Sort
  - If sort is omitted, use `bestSeller`.
  - If sort is invalid, use `bestSeller`.
