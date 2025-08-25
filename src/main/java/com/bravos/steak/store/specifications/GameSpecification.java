package com.bravos.steak.store.specifications;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.UserGame;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.request.FilterQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GameSpecification {

    public static final Set<String> SORT_AVAILABLE = Set.of(
            "releaseDate", "name", "buyerCount"
    );

    public static Specification<Game> withoutFilters(Long cursor) {
        return (root, query, cb) -> {
            if (query == null) {
                query = cb.createQuery();
            }
            List<Predicate> predicates = new ArrayList<>(2);
            long currentTime = DateTimeHelper.currentTimeMillis();

            predicates.add(cb.lessThanOrEqualTo(root.get("releaseDate"), currentTime));

            if (cursor != null) {
                predicates.add(cb.lessThan(root.get("id"), cursor));
            }

            query.orderBy(cb.desc(root.get("id")));
            predicates.add(cb.equal(root.get("status"), GameStatus.OPENING));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Game> withFilters(FilterQuery filterQuery) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>(7);

            if (query == null) {
                query = cb.createQuery();
            }

            long currentTime = DateTimeHelper.currentTimeMillis();

            predicates.add(cb.lessThanOrEqualTo(root.get("releaseDate"), currentTime));
            predicates.add(cb.equal(root.get("status"), GameStatus.OPENING));

            if(filterQuery.getKeyword() != null && !filterQuery.getKeyword().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filterQuery.getKeyword().toLowerCase() + "%"));
            }
            if (filterQuery.getGenreIds() != null && filterQuery.getGenreIds().length > 0) {
                predicates.add(root.join("genres").get("id").in((Object[]) filterQuery.getGenreIds()));
            }
            if (filterQuery.getTagIds() != null && filterQuery.getTagIds().length > 0) {
                predicates.add(root.join("tags").get("id").in((Object[]) filterQuery.getTagIds()));
            }

            if (!Objects.equals(filterQuery.getMinPrice(), filterQuery.getMaxPrice())) {
                if (filterQuery.getMinPrice() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filterQuery.getMinPrice()));
                }
                if (filterQuery.getMaxPrice() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), filterQuery.getMaxPrice()));
                }
            } else if (filterQuery.getMinPrice() != null) {
                predicates.add(cb.equal(root.get("price"), filterQuery.getMinPrice()));
            }

            if (filterQuery.getSortBy() != null && !filterQuery.getSortBy().isEmpty()) {
                String[] sortParts = filterQuery.getSortBy().split(",");
                String sortType = sortParts[0].trim();
                String sortDirection = sortParts.length > 1 ? sortParts[1].trim() : "desc";
                if (SORT_AVAILABLE.contains(sortType)) {
                    Expression<Long> sortExpression;
                    if(sortType.equalsIgnoreCase("buyerCount")) {
                        Subquery<Long> subquery = query.subquery(Long.class);
                        Root<UserGame> userGameRoot = subquery.from(UserGame.class);
                        subquery.select(cb.count(userGameRoot.get("id")))
                                .where(cb.equal(userGameRoot.get("game").get("id"), root.get("id")));
                        sortExpression = subquery.getSelection();
                    } else {
                        sortExpression = root.get(sortType);
                    }
                    if ("desc".equalsIgnoreCase(sortDirection)) {
                        query.orderBy(cb.desc(sortExpression));
                    } else {
                        query.orderBy(cb.asc(sortExpression));
                    }
                } else {
                    throw new BadRequestException("Invalid sort field: " + sortType);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Game> newestGames() {
        return (root, query, cb) -> {
            if (query == null) {
                query = cb.createQuery();
            }
            List<Predicate> predicates = new ArrayList<>(2);
            long currentTime = DateTimeHelper.currentTimeMillis();
            predicates.add(cb.lessThanOrEqualTo(root.get("releaseDate"), currentTime));
            predicates.add(cb.equal(root.get("status"), GameStatus.OPENING));
            query.orderBy(cb.desc(root.get("releaseDate")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Game> comingSoonGames() {
        return (root, query, cb) -> {
            if (query == null) {
                query = cb.createQuery();
            }
            List<Predicate> predicates = new ArrayList<>(2);
            long currentTime = DateTimeHelper.currentTimeMillis();
            predicates.add(cb.greaterThan(root.get("releaseDate"), currentTime));
            predicates.add(cb.equal(root.get("status"), GameStatus.OPENING));
            query.orderBy(cb.asc(root.get("releaseDate")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Game> topMostPlayedGames(Set<Long> gameIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>(2);
            predicates.add(cb.equal(root.get("status"), GameStatus.OPENING));
            if (gameIds != null && !gameIds.isEmpty()) {
                predicates.add(root.get("id").in(gameIds));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Game> publisherManager(Long publisherId, String keyword, String status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>(3);

            if (publisherId != null) {
                predicates.add(cb.equal(root.get("publisher").get("id"), publisherId));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }
            if (status != null) {
                try {
                    predicates.add(cb.equal(root.get("status"), GameStatus.valueOf(status)));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid game status: " + status);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Game> withGameIds(List<Long> gameIds) {
        return (root, query, cb) -> {
            if (gameIds == null || gameIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("id").in(gameIds);
        };
    }
}
