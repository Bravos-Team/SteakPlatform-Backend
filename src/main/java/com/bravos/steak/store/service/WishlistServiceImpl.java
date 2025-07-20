package com.bravos.steak.store.service;

import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.entity.Wishlist;
import com.bravos.steak.store.event.MoveToCartEvent;
import com.bravos.steak.store.event.MoveToWishlistEvent;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.response.CartListItem;
import com.bravos.steak.store.repo.GameDetailsRepository;
import com.bravos.steak.store.repo.WishlistRepository;
import com.bravos.steak.store.repo.injection.CartGameInfo;
import com.bravos.steak.store.repo.injection.GamePrice;
import com.bravos.steak.useraccount.entity.UserAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final SnowflakeGenerator snowflakeGenerator;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final GameDetailsRepository gameDetailsRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, SnowflakeGenerator snowflakeGenerator,
                               ApplicationEventPublisher applicationEventPublisher,
                               GameDetailsRepository gameDetailsRepository) {
        this.wishlistRepository = wishlistRepository;
        this.snowflakeGenerator = snowflakeGenerator;
        this.applicationEventPublisher = applicationEventPublisher;
        this.gameDetailsRepository = gameDetailsRepository;
    }

    @Override
    public void addToWishlist(Long gameId) {
        Long userId = getUserId();

        if(wishlistRepository.existsByGameIdAndUserAccountId(gameId,userId)) {
            throw new BadRequestException("Game with ID " + gameId + " is already in the wishlist for user ID " + userId);
        }

        Wishlist wishlist = Wishlist.builder()
                .id(snowflakeGenerator.generateId())
                .userAccount(UserAccount.builder().id(userId).build())
                .game(Game.builder().id(gameId).build())
                .build();

        try {
            wishlistRepository.save(wishlist);
        } catch (Exception e) {
            log.error("Failed to add game with ID {} to wishlist for user ID {}: {}", gameId, userId, e.getMessage());
            throw new RuntimeException("Failed to add game to wishlist: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeFromWishlist(Long gameId) {
        Long userId = getUserId();
        try {
            wishlistRepository.deleteByGameIdAndUserAccountId(gameId, userId);
        } catch (Exception e) {
            log.error("Failed to remove game with ID {} from wishlist for user ID {}: {}", gameId, userId, e.getMessage());
            throw new RuntimeException("Failed to remove game from wishlist: " + e.getMessage(), e);
        }
    }

    @Override
    public void clearWishlist() {
        Long userId = getUserId();
        try {
            wishlistRepository.deleteAllByUserAccountId(userId);
        } catch (Exception e) {
            log.error("Failed to clear wishlist for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to clear wishlist: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CartListItem> getWishlistItems() {
        Long userId = getUserId();
        List<GamePrice> gamePrices = wishlistRepository.findGamePricesInWishlistByUserAccountId(userId,GameStatus.OPENING);
        if(gamePrices.isEmpty()) return List.of();
        List<CartGameInfo> cartGameInfos = gameDetailsRepository.findByIdIn(
                gamePrices.stream().map(GamePrice::getGameId).toList());
        Map<Long,CartListItem> cartListItemMap = cartGameInfos.stream()
                .collect(Collectors.toMap(CartGameInfo::getId, CartListItem::new));
        for (GamePrice gamePrice : gamePrices) {
            CartListItem item = cartListItemMap.get(gamePrice.getGameId());
            item.setPrice(gamePrice.getPrice().doubleValue());
        }
        return cartListItemMap.values().stream().toList();
    }

    @Override
    public void moveToCart(Long gameId) {
        removeFromWishlist(gameId);
        applicationEventPublisher.publishEvent(new MoveToCartEvent(this, gameId));
    }

    @EventListener
    public void handleMoveToWishlistEvent(MoveToWishlistEvent event) {
        addToWishlist(event.getGameId());
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication instanceof JwtAuthentication) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

}
