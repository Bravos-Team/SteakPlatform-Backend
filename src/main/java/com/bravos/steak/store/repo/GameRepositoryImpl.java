package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class GameRepositoryImpl implements GameRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long getMaxCursorBySpecification(Specification<Game> specification) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        var criteriaQuery = criteriaBuilder.createQuery(Long.class);
        var root = criteriaQuery.from(Game.class);
        Predicate predicate = specification.toPredicate(root, criteriaQuery, criteriaBuilder);
        criteriaQuery.select(criteriaBuilder.max(root.get("releaseDate"))).where(predicate);
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

}
