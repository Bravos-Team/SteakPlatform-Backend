package com.bravos.steak.store.service.impl;

import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.UnauthorizeException;
import com.bravos.steak.store.entity.Cart;
import com.bravos.steak.store.entity.CartItem;
import com.bravos.steak.store.entity.Game;
import com.bravos.steak.store.event.MoveToCartEvent;
import com.bravos.steak.store.event.MoveToWishlistEvent;
import com.bravos.steak.store.event.PaymentSuccessEvent;
import com.bravos.steak.store.model.enums.GameStatus;
import com.bravos.steak.store.model.response.CartListItem;
import com.bravos.steak.store.model.response.CartResponse;
import com.bravos.steak.store.repo.*;
import com.bravos.steak.store.repo.injection.CartGameInfo;
import com.bravos.steak.store.repo.injection.GamePrice;
import com.bravos.steak.store.service.CartService;
import com.bravos.steak.useraccount.entity.UserAccount;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final SessionService sessionService;
    private final SnowflakeGenerator snowflakeGenerator;
    private final CartRepository cartRepository;
    private final GameRepository gameRepository;
    private final CartItemRepository cartItemRepository;
    private final GameDetailsRepository gameDetailsRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CartServiceImpl(SessionService sessionService, SnowflakeGenerator snowflakeGenerator,
                           CartRepository cartRepository, GameRepository gameRepository,
                           CartItemRepository cartItemRepository, GameDetailsRepository gameDetailsRepository,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.sessionService = sessionService;
        this.snowflakeGenerator = snowflakeGenerator;
        this.cartRepository = cartRepository;
        this.gameRepository = gameRepository;
        this.cartItemRepository = cartItemRepository;
        this.gameDetailsRepository = gameDetailsRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public void addToCart(Long gameId) {

        if (gameRepository.countGameByIdAndStatus(gameId, GameStatus.OPENING) <= 0) {
            throw new BadRequestException("Game not found or not available for purchase.");
        }

        Long userId = getUserId();
        Cart cart;
        if (userId == null) {
            Cookie cartCookie = sessionService.getCookie("cart-id");
            if (cartCookie == null || cartCookie.getValue() == null) {
                cart = createGuestCart();
            } else {
                try {
                    long cartId = Long.parseLong(cartCookie.getValue());
                    cart = cartRepository.findById(cartId).orElseGet(this::createGuestCart);
                } catch (NumberFormatException e) {
                    cart = createGuestCart();
                }
            }

        } else {
            try {
                cart = cartRepository.findByUserAccountId(userId).orElseGet(() ->
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

        if(cartItemRepository.existsByCartIdAndGameId(cart.getId(), gameId)) {
            throw new BadRequestException("Game is already in the cart.");
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

    @Transactional
    @Override
    public void removeFromCart(Long gameId) {
        Long userId = getUserId();
        if (userId != null) {
            Cart cart = cartRepository.findByUserAccountId(userId).orElse(null);
            if (cart != null) {
                try {
                    cartItemRepository.deleteCartItemByGameIdAndCartId(gameId, cart.getId());
                } catch (Exception e) {
                    log.error("Failed to remove item from cart: {}", e.getMessage(), e);
                    throw new RuntimeException("Failed to remove item from cart");
                }
                cart.setUpdatedAt(DateTimeHelper.currentTimeMillis());
                try {
                    cartRepository.save(cart);
                } catch (Exception e) {
                    log.error("Failed to update cart after removing item: {}", e.getMessage(), e);
                    throw new RuntimeException("Failed to update cart after removing item");
                }
            }
        } else {
            Cookie cartCookie = sessionService.getCookie("cart-id");
            if (cartCookie != null) {
                long cartId;
                try {
                    cartId = Long.parseLong(cartCookie.getValue());
                } catch (NumberFormatException e) {
                    return;
                }
                try {
                    cartItemRepository.deleteCartItemByGameIdAndCartId(gameId, cartId);
                    cartRepository.updateUpdatedAtById(DateTimeHelper.currentTimeMillis(), cartId);
                } catch (Exception e) {
                    log.error("Failed to update cart after removing item: {}", e.getMessage(), e);
                    throw new RuntimeException("Failed to update cart after removing item");
                }
            }
        }
    }

    @Transactional
    @Override
    public void removeFromCart(List<Long> gameIds) {
        Long userId = getUserId();
        if (userId != null) {
            Cart cart = cartRepository.findByUserAccountId(userId).orElse(null);
            if (cart != null) {
                try {
                    cartItemRepository.deleteCartItemByCartIdAndGameIdIn(cart.getId(), gameIds);
                } catch (Exception e) {
                    log.error("Failed to remove items from cart: {}", e.getMessage(), e);
                    throw new RuntimeException("Failed to remove items from cart");
                }
                cart.setUpdatedAt(DateTimeHelper.currentTimeMillis());
            }
        } else {
            Cookie cartCookie = sessionService.getCookie("cart-id");
            if (cartCookie != null) {
                long cartId;
                try {
                    cartId = Long.parseLong(cartCookie.getValue());
                } catch (NumberFormatException e) {
                    return;
                }
                try {
                    cartItemRepository.deleteCartItemByCartIdAndGameIdIn(cartId, gameIds);
                    cartRepository.updateUpdatedAtById(DateTimeHelper.currentTimeMillis(), cartId);
                } catch (Exception e) {
                    log.error("Failed to remove items from cart: {}", e.getMessage(), e);
                    throw new RuntimeException("Failed to remove items from cart");
                }
            }
        }
    }

    @Override
    @Transactional
    public void clearCart() {
        Long userId = getUserId();
        if (userId == null) {
            Cookie cartCookie = sessionService.getCookie("cart-id");
            if (cartCookie != null) {
                long cartId;
                try {
                    cartId = Long.parseLong(cartCookie.getValue());
                } catch (NumberFormatException e) {
                    return;
                }
                try {
                    cartRepository.deleteById(cartId);
                    removeGuestCookie();
                } catch (Exception e) {
                    log.error("Failed to clear cart for guest user: {}", e.getMessage(), e);
                    throw new RuntimeException("Failed to clear cart for guest user");
                }
            }
        } else {
            try {
                cartRepository.deleteByUserAccountId(userId);
            } catch (Exception e) {
                log.error("Failed to clear cart for user {}: {}", userId, e.getMessage(), e);
                throw new RuntimeException("Failed to clear cart for user: " + userId);
            }
        }
    }

    @Override
    public CartResponse getMyCart() {
        Long userId = getUserId();
        List<GamePrice> gamePrices;
        if (userId == null) {
            Cookie cartCookie = sessionService.getCookie("cart-id");
            long cartId;
            if (cartCookie == null || cartCookie.getValue() == null) return new CartResponse(List.of());
            try {
                cartId = Long.parseLong(cartCookie.getValue());
            } catch (NumberFormatException e) {
                return new CartResponse(List.of());
            }
            gamePrices = cartItemRepository.findGamePricesInCartByCartId(cartId, GameStatus.OPENING);
        } else {
            gamePrices = cartItemRepository.findGamePricesInCartByUserAccountId(userId, GameStatus.OPENING);
        }
        if (gamePrices.isEmpty()) return new CartResponse(List.of());
        List<CartGameInfo> cartGameInfos = gameDetailsRepository.findByIdIn(
                gamePrices.stream().map(GamePrice::getGameId).toList());
        Map<Long, CartListItem> cartListItemMap = cartGameInfos.stream()
                .collect(Collectors.toMap(CartGameInfo::getId, CartListItem::new));
        for (GamePrice gamePrice : gamePrices) {
            CartListItem cartListItem = cartListItemMap.get(gamePrice.getGameId());
            cartListItem.setPrice(gamePrice.getPrice().doubleValue());
        }
        return new CartResponse(cartListItemMap.values().stream().toList());
    }

    @Override
    @Transactional
    public void mergeCart() {
        Cookie guestCartCookie = sessionService.getCookie("cart-id");
        if (guestCartCookie == null || guestCartCookie.getValue() == null) {
            return;
        }
        Long userId = getUserId();
        if (userId == null) return;
        Long guestCartId;
        try {
            guestCartId = Long.valueOf(guestCartCookie.getValue());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid guest cart ID format");
        }
        Set<CartItem> afterMergedCartItems = new HashSet<>(cartItemRepository.findAllByCartId(guestCartId));
        if (afterMergedCartItems.isEmpty()) return;

        Cart userCart = cartRepository.findByUserAccountId(userId).orElse(null);
        if (userCart == null) {
            userCart = cartRepository.findById(guestCartId).orElseThrow(() ->
                    new BadRequestException("Guest cart not found"));
            userCart.setUserAccount(UserAccount.builder().id(userId).build());
            try {
                cartRepository.save(userCart);
            } catch (Exception e) {
                log.error("Failed to save user cart: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to save user cart: " + e.getMessage(), e);
            }
        } else {
            if (Objects.equals(userCart.getId(), guestCartId)) {
                return;
            }
            List<CartItem> userCartItems = cartItemRepository.findAllByCartId(userCart.getId());
            Cart finalUserCart = userCart;
            afterMergedCartItems.forEach(cartItem -> cartItem.setCart(finalUserCart));
            afterMergedCartItems.addAll(userCartItems);
            try {
                cartItemRepository.saveAll(afterMergedCartItems);
                removeGuestCookie();
            } catch (Exception e) {
                log.error("Failed to merge cart items: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to merge cart items: " + e.getMessage(), e);
            }
            Thread.startVirtualThread(() -> cartRepository.deleteById(guestCartId));
        }
    }

    @Override
    @Transactional
    public void moveToWishlist(Long gameId) {
        Long userId = getUserId();
        if(userId == null) {
            throw new UnauthorizeException("You must be logged in to move items to wishlist.");
        }
        removeFromCart(gameId);
        applicationEventPublisher.publishEvent(new MoveToWishlistEvent(this, gameId));
    }

    @EventListener
    @Transactional
    @Order(2)
    public void removeCartWhenPaymentSuccessfully(PaymentSuccessEvent event) {
        clearCart();
    }

    @EventListener
    @Transactional
    public void handleMoveToCartEvent(MoveToCartEvent event) {
        addToCart(event.getGameId());
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication instanceof JwtAuthentication) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

    private Cart createGuestCart() {
        Cart cart = Cart.builder()
                .id(snowflakeGenerator.generateId())
                .userAccount(null)
                .updatedAt(DateTimeHelper.currentTimeMillis())
                .build();

        ResponseCookie cookie = ResponseCookie.from("cart-id", String.valueOf(cart.getId()))
                .path("/api/v1/store/public/cart")
                .maxAge(7 * 60 * 60 * 24)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
//                .domain(System.getProperty("COOKIE_DOMAIN"))
                .build();

        sessionService.addCookie(cookie);
        return cartRepository.save(cart);
    }

    private void removeGuestCookie() {
        ResponseCookie cookie = ResponseCookie.from("cart-id", "")
                .path("/api/v1/store/public/cart")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
//                .domain(System.getProperty("COOKIE_DOMAIN"))
                .build();
        sessionService.addCookie(cookie);
    }


}
