package com.bravos.steak.common.model;

public final class PublisherAuthority {

    public static final String MASTER = "PUBLISHER_MASTER";
    public static final String READ_REVENUE_STATISTIC = "PUBLISHER_READ_REVENUE_STATISTIC";
    public static final String READ_MEMBERS = "PUBLISHER_READ_MEMBERS";
    public static final String WRITE_MEMBERS = "PUBLISHER_WRITE_MEMBERS";
    public static final String DELETE_MEMBERS = "PUBLISHER_DELETE_MEMBERS";
    public static final String READ_GAMES = "PUBLISHER_READ_GAMES";
    public static final String READ_GAME_STATISTIC = "PUBLISHER_READ_GAME_STATISTIC";
    public static final String WRITE_GAME_INFO = "PUBLISHER_WRITE_GAME_INFO";
    public static final String WRITE_GAME_PRICE = "PUBLISHER_WRITE_GAME_PRICE";
    public static final String CREATE_GAME = "PUBLISHER_CREATE_GAME";
    public static final String MANAGE_GAMES = "PUBLISHER_MANAGE_GAMES";
    public static final String READ_HUB = "PUBLISHER_READ_HUB";
    public static final String WRITE_HUB = "PUBLISHER_WRITE_HUB";
    public static final String DELETE_HUB = "PUBLISHER_DELETE_HUB";
    public static final String READ_MY_LOGS = "PUBLISHER_READ_MY_LOGS";
    public static final String READ_ALL_LOGS = "PUBLISHER_READ_ALL_LOGS";

    public static final String WRITE_INFO = "PUBLISHER_WRITE_INFO";
    public static final String READ_SENSITIVE_INFO = "PUBLISHER_READ_SENSITIVE_INFO";
    public static final String WRITE_SENSITIVE_INFO = "PUBLISHER_WRITE_SENSITIVE_INFO";

    public static boolean isValidAuthority(String value) {
        return switch (value) {
            case MASTER, READ_REVENUE_STATISTIC, READ_MEMBERS, WRITE_MEMBERS, DELETE_MEMBERS, READ_GAMES,
                 READ_GAME_STATISTIC, WRITE_GAME_INFO, WRITE_GAME_PRICE, CREATE_GAME, MANAGE_GAMES, READ_HUB, WRITE_HUB,
                 DELETE_HUB, READ_MY_LOGS, READ_ALL_LOGS, WRITE_INFO, WRITE_SENSITIVE_INFO, READ_SENSITIVE_INFO -> true;
            default -> false;
        };
    }

}
