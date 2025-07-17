package com.bravos.steak.store.service;

import com.bravos.steak.store.model.response.CartListItem;

import java.util.List;

public interface WishlistService {

    void addToWishlist(Long gameId);

    void removeFromWishlist(Long gameId);

    void clearWishlist();

    List<CartListItem> getWishlistItems();

    void moveToCart(Long gameId);

}
