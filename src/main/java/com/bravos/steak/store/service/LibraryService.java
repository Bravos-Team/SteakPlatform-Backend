package com.bravos.steak.store.service;

import com.bravos.steak.store.model.response.GameLibraryItem;

import java.util.List;

public interface LibraryService {

    List<GameLibraryItem> getMyLibrary();

}
