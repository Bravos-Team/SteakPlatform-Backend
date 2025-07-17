package com.bravos.steak.store.controller;

import com.bravos.steak.store.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/store/public/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long gameId) {
        cartService.addToCart(gameId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long gameId) {
        cartService.removeFromCart(gameId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/move-to-wishlist")
    public ResponseEntity<?> moveToWishlist(@RequestParam Long gameId) {
        cartService.moveToWishlist(gameId);
        return ResponseEntity.ok().build();
    }

}
