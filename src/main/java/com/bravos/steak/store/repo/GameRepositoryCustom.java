package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Game;
import org.springframework.data.jpa.domain.Specification;

public interface GameRepositoryCustom {

    Long getMaxCursorBySpecification(Specification<Game> specification);

}
