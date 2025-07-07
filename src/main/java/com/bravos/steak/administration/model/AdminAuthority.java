package com.bravos.steak.administration.model;

public class AdminAuthority {

    public static final String MASTER = "ADMIN_MASTER";

    public static final String REVIEW_GAME = "ADMIN_REVIEW_GAME";

    public static final String READ_USERS = "ADMIN_READ_USERS";

    public static final String MANAGE_USERS = "ADMIN_MANAGE_USERS";

    public static final String READ_GAMES = "ADMIN_READ_GAMES";

    public static final String MANAGE_GAMES = "ADMIN_MANAGE_GAMES";

    public static final String READ_STATISTICS = "ADMIN_READ_STATISTICS";

    public static final String READ_PUBLISHERS = "ADMIN_READ_PUBLISHERS";

    public static final String MANAGE_PUBLISHERS = "ADMIN_MANAGE_PUBLISHERS";

    public static final String READ_SENSTIVE_PUBLISHERS = "ADMIN_READ_SENSITIVE_PUBLISHERS";

    public static final String READ_LOGS = "ADMIN_READ_LOGS";

    public static final String READ_MY_LOGS = "ADMIN_READ_MY_LOGS";

    public static final String MANAGE_ADMIN_ACCOUNTS = "ADMIN_MANAGE_ADMIN_ACCOUNTS";

    public static boolean isValidAuthority(String value) {
        return switch (value) {
            case MASTER, REVIEW_GAME, READ_USERS, MANAGE_USERS, READ_GAMES,
                 MANAGE_GAMES, READ_STATISTICS, READ_PUBLISHERS, MANAGE_PUBLISHERS,
                 READ_SENSTIVE_PUBLISHERS, READ_LOGS, READ_MY_LOGS, MANAGE_ADMIN_ACCOUNTS -> true;
            default -> false;
        };
    }

}
