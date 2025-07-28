package com.bravos.steak.store.repo;

import com.bravos.steak.store.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
}