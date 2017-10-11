package com.kubatatami.poker.model;

/**
 * Created by Kuba on 28/09/14.
 */
public enum CommandType {
    //internal
    HELLO,
    USER_LIST,
    CLOSE,

    //join logic
    JOIN,
    JOIN_CANCEL,
    ACCEPT,
    DISACCEPT,
    START,

    //game
    GAME_READY,
    ROUND_READY,
    ROUND_SETUP,
    GAME_SETUP,
    DECK_SET,
    DEAL_CARD,
    DEAL_CARD_PROXY,
    DEAL,
    DECISION,
    RISE,
    CALL,
    BET,
    CHECK,
    FOLD,
    ALL_IN,
    USERS_DATA,
    GAME_DATA,
    PLAYER_ACTION
}
