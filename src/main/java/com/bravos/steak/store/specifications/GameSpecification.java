package com.bravos.steak.store.specifications;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.request.FilterQuery;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GameSpecification {

    private static final List<String> allowedSortFields = List.of(
            "name", "price"
    );

    public static Specification<Game> withoutFilters(Long cursor) {
        return (root, query, cb) -> {
            if (query == null) {
                query = cb.createQuery();
            }
            List<Predicate> predicates = new ArrayList<>(2);
            long currentTime = DateTimeHelper.currentTimeMillis();
            if (cursor != null) {
                predicates.add(cb.lessThan(root.get("releaseDate"), cursor > currentTime ? currentTime : cursor));
            } else {
                predicates.add(cb.lessThan(root.get("releaseDate"), currentTime));
            }
            query.orderBy(cb.desc(root.get("releaseDate")));
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

            if(filterQuery.getKeyword() != null && !filterQuery.getKeyword().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filterQuery.getKeyword().toLowerCase() + "%"));
            }
            if (filterQuery.getGenreIds() != null && filterQuery.getGenreIds().length > 0) {
                predicates.add(root.join("genres").get("id").in((Object[]) filterQuery.getGenreIds()));
            }
            if (filterQuery.getTagIds() != null && filterQuery.getTagIds().length > 0) {
                predicates.add(root.join("tags").get("id").in((Object[]) filterQuery.getTagIds()));
            }
            if (filterQuery.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filterQuery.getMinPrice()));
            }
            if (filterQuery.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filterQuery.getMaxPrice()));
            }
            if (filterQuery.getSortBy() != null && !filterQuery.getSortBy().isEmpty()) {
                String[] sortParts = filterQuery.getSortBy().split(",");
                String sortField = sortParts[0].trim();

                if (allowedSortFields.contains(sortField)) {
                    String sortDirection = sortParts.length > 1 ? sortParts[1].trim() : "desc";

                    if ("asc".equalsIgnoreCase(sortDirection)) {
                        query.orderBy(cb.asc(root.get(sortField)));
                    } else {
                        query.orderBy(cb.desc(root.get(sortField)));
                    }
                }
            }

            if (filterQuery.getCursorDirection()) {
                query.orderBy(cb.desc(root.get("releaseDate")));
            } else {
                query.orderBy(cb.asc(root.get("releaseDate")));
            }

            predicates.add(cb.equal(root.get("status"), GameStatus.OPENING));

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

}
