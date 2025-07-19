package com.bravos.steak.store.specifications;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.enums.GameStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GameSpecification {

    public static Specification<Game> withoutFilters(Long cursor) {
        return (root, query, cb) -> {
            if (query == null) {
                query = cb.createQuery();
            }
            List<Predicate> predicates = new ArrayList<>(2);
            if (cursor != null) {
                predicates.add(cb.lessThan(root.get("releaseDate"), cursor));
            }
            query.orderBy(cb.desc(root.get("releaseDate")));
            predicates.add(cb.equal(root.get("status"), GameStatus.OPENING));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Game> withFilters(
            Long minPrice,
            Long maxPrice,
            Long cursor
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>(5);

            if(query == null) {
                query = cb.createQuery();
            }

            if (minPrice != null && minPrice > 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null && maxPrice > 0) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (cursor != null) {
                predicates.add(cb.lessThan(root.get("releaseDate"), cursor));
            }

            predicates.add(cb.equal(root.get("status"), GameStatus.OPENING));

            query.orderBy(cb.desc(root.get("releaseDate")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }



}
