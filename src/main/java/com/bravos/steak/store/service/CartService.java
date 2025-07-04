package com.bravos.steak.store.service;

public interface CartService {

    void addToCart(Long gameId);

    void removeFromCart(Long gameId);

    void clearCart();

}
