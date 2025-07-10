package com.bravos.steak.store.controller;

import com.bravos.steak.store.service.LibraryService;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/my-games")
    public ResponseEntity<?> getLibrary(@SortDefault.SortDefaults({
            @SortDefault(sort = "ownedAt", direction = Sort.Direction.DESC),
            @SortDefault(sort = "playRecentDate", direction = Sort.Direction.DESC)})Sort sort) {
        return ResponseEntity.ok(libraryService.getMyLibrary(sort));
    }

}
