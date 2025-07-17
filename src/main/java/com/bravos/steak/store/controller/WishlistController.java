package com.bravos.steak.store.controller;

import com.bravos.steak.store.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/store/private/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlistItems());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToWishlist(@RequestParam Long gameId) {
        wishlistService.addToWishlist(gameId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromWishlist(@RequestParam Long gameId) {
        wishlistService.removeFromWishlist(gameId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearWishlist() {
        wishlistService.clearWishlist();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/move-to-cart")
    public ResponseEntity<?> moveToCart(@RequestParam Long gameId) {
        wishlistService.moveToCart(gameId);
        return ResponseEntity.ok().build();
    }

}
