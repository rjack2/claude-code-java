---
description: Persistence standards for entities, repositories, dynamic queries, and PostgreSQL arrays
paths:
  - "**/*.java"
---

# Persistence Standards

## Entity

- Entities are persistence-only models, never exposed via API responses.

```java
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private boolean active;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    // getters and setters
}
```

## Repository

- Repository layer must never be exposed outside the service layer.
- Extend `JpaSpecificationExecutor` as well when the repository needs dynamic queries.

```java
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
}
```

## Dynamic Queries

- Use `JpaSpecificationExecutor` for dynamic filtering.
- Never concatenate raw input into a query string. Always use named or positional parameters.
- Each filter condition is a static method in a dedicated `Specifications` class.
- A `null` parameter returns `cb.conjunction()` (no-op predicate). Never skip the predicate entirely.

### Specification

```java
public class ItemSpecifications {
    public static Specification<Item> hasName(String name) {
        return (root, query, cb) ->
                Objects.isNull(name) ? cb.conjunction() : cb.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Item> hasCategory(String category) {
        return (root, query, cb) ->
                Objects.isNull(category) ? cb.conjunction() : cb.equal(root.get("category"), category);
    }
}
```

### Combining Specifications

```java
var spec = Specification.<Item>where(ItemSpecifications.hasName(name))
                        .and(ItemSpecifications.hasCategory(category));
var results = itemRepository.findAll(spec);
```

## PostgreSQL Array Columns

- Map `text[]` columns to `List<String>` using `@JdbcTypeCode(SqlTypes.ARRAY)` and `@Column(columnDefinition = "text[]")`.
- To filter by array membership, use Hibernate's built-in `array_position` function in a `Specification`. Never write a new SQL or PL/pgSQL function in the database for this.

### Mapping

```java
@JdbcTypeCode(SqlTypes.ARRAY)
@Column(columnDefinition = "text[]")
private List<String> tags;
```

### Filtering by Array Membership

```java
public static Specification<Item> hasTag(String tag) {
    return (root, query, cb) ->
            Objects.isNull(tag) ? cb.conjunction() :
            cb.greaterThan(cb.function("array_position", Integer.class, root.get("tags"), cb.literal(tag)), 0);
}
```

## Custom Queries

- If an operation (bulk updates, ranking, aggregation, complex joins) cannot be expressed through `Specification`, use a parameterized `@Query` instead, JPQL or native SQL, whichever fits.
- Never build the query by string concatenation. Use named or positional parameters.
- Add a comment above the query explaining why a `Specification` could not express it.

### Bulk Update

- Example: a bulk update, which `Specification` cannot express at all.

```java
@Modifying
@Query("""
        UPDATE Item i
        SET i.active = false
        WHERE i.createdAt < :cutoff
        """)
int deactivateOlderThan(@Param("cutoff") LocalDateTime cutoff);
```

### Grouped Aggregate

- Example: a grouped aggregate, computed in the database because it is more efficient than aggregating in the application.

```java
public interface CategoryAveragePrice {
    String getCategory();
    Double getAveragePrice();
}
```

```java
@Query(value = """
        SELECT category, AVG(price) AS averagePrice
        FROM item
        GROUP BY category
        """, nativeQuery = true)
List<CategoryAveragePrice> averagePricePerCategory();
```
