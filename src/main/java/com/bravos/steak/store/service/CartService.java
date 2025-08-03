package com.bravos.steak.store.service;

import com.bravos.steak.store.model.response.CartResponse;

import java.util.List;

public interface CartService {

    void addToCart(Long gameId);

    void removeFromCart(Long gameId);

    void removeFromCart(List<Long> gameIds);

    void clearCart();

    CartResponse getMyCart();

    void mergeCart();

    void moveToWishlist(Long gameId);

}
