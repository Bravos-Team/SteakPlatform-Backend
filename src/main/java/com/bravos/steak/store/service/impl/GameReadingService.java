package com.bravos.steak.store.service.impl;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.service.GameReading;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameReadingService implements GameReading {
    @Override
    public List<Game> findAll() {
        return List.of();
    }

    @Override
    public List<Game> findByName(String name) {
        return List.of();
    }
}
