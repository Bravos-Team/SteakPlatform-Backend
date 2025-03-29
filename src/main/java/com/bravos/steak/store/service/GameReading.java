package com.bravos.steak.store.service;



import com.bravos.steak.store.entity.Game;

import java.util.List;

public interface GameReading {
    List<Game> findAll();
    List<Game> findByName(String name);
}
