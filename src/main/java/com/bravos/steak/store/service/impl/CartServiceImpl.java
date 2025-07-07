package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.store.entity.Cart;
import com.bravos.steak.store.entity.CartItem;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.repo.CartItemRepository;
import com.bravos.steak.store.repo.CartRepository;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.service.CartService;
import com.bravos.steak.useraccount.entity.UserAccount;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final SessionService sessionService;
    private final SnowflakeGenerator snowflakeGenerator;
    private final CartRepository cartRepository;
    private final GameRepository gameRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(SessionService sessionService, SnowflakeGenerator snowflakeGenerator,
                           CartRepository cartRepository, GameRepository gameRepository,
                           CartItemRepository cartItemRepository) {
        this.sessionService = sessionService;
        this.snowflakeGenerator = snowflakeGenerator;
        this.cartRepository = cartRepository;
        this.gameRepository = gameRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public void addToCart(Long gameId) {

        if(gameRepository.countGameByIdAndStatus(gameId, GameStatus.OPENING) <= 0) {
            throw new BadRequestException("Game not found or not available for purchase.");
        }

        Long userId = getUserId();
        Cart cart;
        if(userId == null) {
            Cookie cartCookie = sessionService.getCookie("cart-id");
            long cartId;
            if (cartCookie != null) {

                try {
                    cartId = Long.parseLong(cartCookie.getValue());
                } catch (NumberFormatException e) {
                    cartId = snowflakeGenerator.generateId();
                }

            } else {
                cartId = snowflakeGenerator.generateId();
            }

            cart = Cart.builder()
                    .id(cartId)
                    .userAccount(null)
                    .updatedAt(DateTimeHelper.currentTimeMillis())
                    .build();

            try {
                cart = cartRepository.save(cart);
            } catch (Exception e) {
                log.error("Failed to save cart: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to save cart");
            }

        } else {
            try {
                cart = cartRepository.findByUserAccountId((userId)).orElse(
                    cartRepository.save(Cart.builder()
                            .id(snowflakeGenerator.generateId())
                            .userAccount(UserAccount.builder()
                                    .id(userId)
                                    .build())
                            .updatedAt(DateTimeHelper.currentTimeMillis())
                            .build())
                );
            } catch (Exception e) {
                log.error("Failed to retrieve or create cart for user {}: {}", userId, e.getMessage(), e);
                throw new RuntimeException("Failed to retrieve or create cart for user: " + userId);
            }
        }

        CartItem cartItem = CartItem.builder()
                .id(snowflakeGenerator.generateId())
                .game(Game.builder()
                        .id(gameId)
                        .build())
                .cart(cart)
                .addedAt(DateTimeHelper.currentTimeMillis())
                .build();

        try {
            cartItemRepository.save(cartItem);
        } catch (Exception e) {
            log.error("Failed to add game to cart: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add game to cart");
        }

    }

    @Override
    public void removeFromCart(Long gameId) {
        Long userId = getUserId();
        if (userId != null) {
            cartRepository.findByUserAccountId(userId).ifPresent(cart ->
                    cartItemRepository.removeCartItemByGameIdAndCartId(gameId, cart.getId()));
        } else {
            Cookie cartCookie = sessionService.getCookie("cart-id");
            if (cartCookie != null) {
                long cartId;
                try {
                    cartId = Long.parseLong(cartCookie.getValue());
                } catch (NumberFormatException e) {
                    return;
                }
                cartItemRepository.removeCartItemByGameIdAndCartId(gameId,cartId);
            }
        }
    }

    @Override
    public void clearCart() {
        Long userId = getUserId();
        if(userId == null) {
            Cookie cartCookie = sessionService.getCookie("cart-id");
            if(cartCookie != null) {
                long cartId;
                try {
                    cartId = Long.parseLong(cartCookie.getValue());
                } catch (NumberFormatException e) {
                    return;
                }
                cartRepository.removeById(cartId);
            }
        } else {
            cartRepository.removeByUserAccountId(userId);
        }
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication instanceof JwtAuthentication) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

}
