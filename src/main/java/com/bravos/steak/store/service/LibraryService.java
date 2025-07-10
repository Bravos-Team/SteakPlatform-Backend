package com.bravos.steak.store.service;

import com.bravos.steak.store.model.response.GameLibraryItem;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface LibraryService {

    List<GameLibraryItem> getMyLibrary(Sort sort);

}
