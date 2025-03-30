package com.bravos.steak.store.model.mapper;

import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.Game_Genre;
import com.bravos.steak.store.model.response.GameResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(target = "publisherName", source = "publisher.name")
    GameResponse toGameResponse(Game game);

    default List<String> mapGenres(List<Game_Genre> gameGenres) {
        if (gameGenres == null) {
            return Collections.emptyList();
        }
        return gameGenres.stream().map(gg -> gg.getGenre().getName()).toList();
    }

    Game toEntity(GameResponse gameResponse);
}
