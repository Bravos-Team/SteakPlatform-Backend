package com.bravos.steak.common.seed;

import com.bravos.steak.common.service.helper.DateTimeHelper;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.store.entity.*;
import com.bravos.steak.store.model.enums.OrderStatus;
import com.bravos.steak.store.repo.GameRepository;
import com.bravos.steak.store.repo.OrderRepository;
import com.bravos.steak.store.repo.UserGameRepository;
import com.bravos.steak.useraccount.entity.UserAccount;
import com.bravos.steak.useraccount.entity.UserProfile;
import com.bravos.steak.useraccount.model.enums.AccountStatus;
import com.bravos.steak.useraccount.repo.UserAccountRepository;
import com.bravos.steak.useraccount.repo.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class Generator {
    private final OrderRepository orderRepository;

    private final SnowflakeGenerator snowflakeGenerator;

    private static final String HASHED_PASSWORD = "$2a$10$bUvVFe4RYE1ea7nKjXOrjuoFIGii2GIMFpurxlq71uwVVNYSvYfIG";

    private static final String[] VN_FIRST_NAMES = {
            "Nguyen", "Tran", "Le", "Pham", "Hoang", "Huynh", "Phan", "Vu", "Vo", "Dang",
            "Bui", "Do", "Ho", "Ngo", "Duong", "Ly"
    };

    private static final String[] VN_MIDDLE_NAMES = {
            "Thi", "Van", "Duc", "Quang", "Minh", "Thanh", "Huu", "Ngoc", "Hong", "Mai",
            "Anh", "Bao", "Chau", "Diep", "Giang", "Ha"
    };

    private static final String[] VN_LAST_NAMES = {
            "An", "Binh", "Chau", "Duc", "Giang", "Hanh", "Hoa", "Hung", "Khanh", "Lam",
            "Linh", "Long", "Minh", "Nam", "Phuc", "Quang", "Son", "Tuan", "Vinh", "Bao"
    };

    private static final Random RANDOM = new Random();

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final GameRepository gameRepository;
    private final UserGameRepository userGameRepository;

    public Generator(SnowflakeGenerator snowflakeGenerator, UserAccountRepository userAccountRepository,
                     UserProfileRepository userProfileRepository, GameRepository gameRepository,
                     OrderRepository orderRepository, UserGameRepository userGameRepository) {
        this.snowflakeGenerator = snowflakeGenerator;
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.gameRepository = gameRepository;
        this.orderRepository = orderRepository;
        this.userGameRepository = userGameRepository;
    }

    private String generateRandomUsername() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder username = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int index = RANDOM.nextInt(0, alphabet.length());
            username.append(alphabet.charAt(index));
        }
        return username.toString();
    }

    private String generateRandomEmail() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder email = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = RANDOM.nextInt(0, alphabet.length());
            email.append(alphabet.charAt(index));
        }
        email.append("@example.com");
        return email.toString();
    }

    private String generateRandomName() {
        String firstName = VN_FIRST_NAMES[RANDOM.nextInt(0, VN_FIRST_NAMES.length)];
        String middleName = VN_MIDDLE_NAMES[RANDOM.nextInt(0, VN_MIDDLE_NAMES.length)];
        String lastName = VN_LAST_NAMES[RANDOM.nextInt(0, VN_LAST_NAMES.length)];
        return firstName + " " + middleName + " " + lastName;
    }

    @Transactional
    public List<Long> generateUserAccount(int count) {
        List<UserAccount> userAccounts = new ArrayList<>(count);
        List<UserProfile> userProfiles = new ArrayList<>(count);
        final int batchSize = 200;
        for(int i = 0; i < count; i++) {
            UserAccount userAccount = UserAccount.builder()
                    .id(snowflakeGenerator.generateId())
                    .username(generateRandomUsername())
                    .email(generateRandomEmail())
                    .password(HASHED_PASSWORD)
                    .status(AccountStatus.ACTIVE)
                    .build();

            userAccounts.add(userAccount);

            UserProfile userProfile = new UserProfile();
            userProfile.setId(userAccount.getId());
            userProfile.setDisplayName(generateRandomName());
            userProfile.setSex(i % 2 == 0);

            userProfiles.add(userProfile);
        }

        List<Long> ids = userAccounts.stream().map(UserAccount::getId).toList();

        while (!userAccounts.isEmpty()) {
            int endIndex = Math.min(batchSize, userAccounts.size());
            List<UserAccount> subList = userAccounts.subList(0, endIndex);
            List<UserProfile> subProfileList = userProfiles.subList(0, endIndex);
            userAccountRepository.saveAll(subList);
            userProfileRepository.saveAll(subProfileList);
            userAccounts.removeAll(subList);
            userProfiles.removeAll(subProfileList);
            log.info("Inserted {} user accounts", endIndex);
        }

        return ids;
    }

    @Transactional
    @Scheduled(cron = "0 0 */2 * * *")
    public void generateRevenueData() {
        List<Game> games = gameRepository.findAll();

        LocalDateTime year2020 = LocalDateTime.of(2020, 1, 1, 0, 0);
        List<Long> userIds = generateUserAccount(RANDOM.nextInt(500, 1000) + 200);
        log.info("Generated {} user accounts", userIds.size());
        log.info("Starting to generate revenue data for year 2020");
        for (Long userId : userIds) {
            generateOrderData(userId, games, year2020, null);
        }
        log.info("Generated revenue data for year 2020");

        LocalDateTime year2021 = year2020.plusYears(1);
        userIds = generateUserAccount(RANDOM.nextInt(500, 1000) + 200);
        log.info("Generated {} user accounts", userIds.size());
        log.info("Starting to generate revenue data for year 2021");
        for (Long userId : userIds) {
            generateOrderData(userId, games, year2021, null);
        }
        log.info("Generated revenue data for year 2021");

        LocalDateTime year2022 = year2021.plusYears(1);
        userIds = generateUserAccount(RANDOM.nextInt(500, 1000) + 200);
        log.info("Generated {} user accounts", userIds.size());
        log.info("Starting to generate revenue data for year 2022");

        for (Long userId : userIds) {
            generateOrderData(userId, games, year2022, null);
        }
        log.info("Generated revenue data for year 2022");

        LocalDateTime year2023 = year2022.plusYears(1);
        userIds = generateUserAccount(RANDOM.nextInt(500, 1000) + 200);
        log.info("Generated {} user accounts", userIds.size());
        log.info("Starting to generate revenue data for year 2023");

        for (Long userId : userIds) {
            generateOrderData(userId, games, year2023, null);
        }
        log.info("Generated revenue data for year 2023");

        LocalDateTime year2024 = year2023.plusYears(1);
        userIds = generateUserAccount(RANDOM.nextInt(500, 1000) + 200);

        log.info("Generated {} user accounts", userIds.size());
        log.info("Starting to generate revenue data for year 2024");

        for (Long userId : userIds) {
            generateOrderData(userId, games, year2024, null);
        }
        log.info("Generated revenue data for year 2024");

        LocalDateTime year2025 = year2024.plusYears(1);
        userIds = generateUserAccount(RANDOM.nextInt(500, 1000) + 200);
        log.info("Generated {} user accounts", userIds.size());
        log.info("Starting to generate revenue data for year 2025");
        for (Long userId : userIds) {
            generateOrderData(userId, games, year2025, LocalDateTime.now());
        }
        log.info("Generated revenue data for year 2025");

        log.info("Finished generating revenue data");
    }

    public void generateOrderData(Long userId, List<Game> games, LocalDateTime startTime, LocalDateTime endTime) {
        Set<Long> randomGameIds = new HashSet<>(25);
        List<Game> availableGames = new ArrayList<>(games);
        UserAccount userAccount = UserAccount.builder().id(userId).build();
        List<Order> orders = new ArrayList<>();
        List<UserGame> userGames = new ArrayList<>();
        Map<Long, Game> gameMap = new HashMap<>();

        for (Game game : games) {
            gameMap.put(game.getId(), game);
        }

        while (randomGameIds.size() < 25) {
            int randomIndex = RANDOM.nextInt(0, availableGames.size() - 1);
            randomGameIds.add(availableGames.remove(randomIndex).getId());
        }

        log.info("Generating order data for user {} with {} games", userId, randomGameIds.size());

        int startMonth = startTime.getMonthValue();
        int endMonth = endTime != null ? endTime.getMonth().getValue() - 1 : 12;
        while (!randomGameIds.isEmpty() && startMonth <= endMonth) {
            int randomQuantity = Math.min(RANDOM.nextInt(1, 3), randomGameIds.size());
            Set<Long> randomGameIdsForOrder = new HashSet<>(randomQuantity);
            while (randomGameIdsForOrder.size() < randomQuantity && !randomGameIds.isEmpty()) {
                int randomIndex = RANDOM.nextInt(0, randomGameIds.size());
                Long[] gameIdArray = randomGameIds.toArray(new Long[0]);
                randomGameIdsForOrder.add(gameIdArray[randomIndex]);
                randomGameIds.remove(gameIdArray[randomIndex]);
            }

            log.info("Creating order for user {} in month {} with {} games", userId, startMonth, randomGameIdsForOrder.size());

            LocalDateTime randomDateInMonth = startTime.withMonth(startMonth)
                    .withDayOfMonth(RANDOM.nextInt(1, startTime.withMonth(startMonth).toLocalDate().lengthOfMonth() + 1))
                    .withHour(RANDOM.nextInt(0, 23))
                    .withMinute(RANDOM.nextInt(0, 59))
                    .withSecond(RANDOM.nextInt(0, 59));
            Order order = Order.builder()
                    .id(snowflakeGenerator.generateId())
                    .userAccount(userAccount)
                    .status(OrderStatus.SUCCESS)
                    .createdAt(DateTimeHelper.from(randomDateInMonth))
                    .updatedAt(DateTimeHelper.from(randomDateInMonth.plusMinutes(1)))
                    .build();
            List<OrderDetails> orderDetails = new ArrayList<>(randomQuantity);
            for (Long gameId : randomGameIdsForOrder) {
                Game game = Game.builder().id(gameId).build();
                OrderDetails orderDetail = OrderDetails.builder()
                        .id(snowflakeGenerator.generateId())
                        .order(order)
                        .game(game)
                        .price(gameMap.get(gameId).getPrice())
                        .build();
                orderDetails.add(orderDetail);
            }
            for (OrderDetails orderDetail : orderDetails) {
                UserGame userGame = UserGame.builder()
                        .id(UserGameId.builder().gameId(orderDetail.getGame().getId()).build())
                        .user(userAccount)
                        .game(orderDetail.getGame())
                        .ownedDate(orderDetail.getOrder().getCreatedAt())
                        .build();
                userGames.add(userGame);
            }
            order.setOrderDetails(orderDetails);
            orders.add(order);
            ++startMonth;
        }
        log.info("Generated {} orders for user {}", orders.size(), userId);

        log.info("Saving orders and user games for user {}", userId);

        orderRepository.saveAllAndFlush(orders);
        userGameRepository.saveAllAndFlush(userGames);

        log.info("Saved orders and user games for user {}", userId);
    }

}
