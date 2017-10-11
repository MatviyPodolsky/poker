package com.kubatatami.poker.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kuba on 28/09/14.
 */
public class PokerCommand implements Serializable {

    public CommandType type;

    //hello msg
    public User user;

    //client list msg
    public Collection<User> users;
    public GameType gameType;
    public Boolean gameActive;

    //deal card
    public Card card;

    public HoldemGame game;
    public Integer playerId;

    public Integer riseValue;
    public Integer deckValue;
    public Integer smallBlind;
    public Integer bigBlind;
    public Integer entryFee;
    public String action;
    public Integer dealerPosition;
    public Integer cash;
    public Integer pot;
    public Integer callValue;
    public Integer lastBet;


    public PokerCommand() {
    }

    public PokerCommand(CommandType type) {
        this.type = type;
    }

    public PokerCommand(String id, String name, String image, int cash) {
        type = CommandType.HELLO;
        user = new User(id, name, image, cash);
    }

    public PokerCommand(Collection<User> users, int entryFee, GameType gameType,boolean gameActive) {
        type = CommandType.USER_LIST;
        this.users = users;
        this.entryFee = entryFee;
        this.gameType = gameType;
        this.gameActive = gameActive;
    }

    public PokerCommand(HoldemGame game, int playerId, int deckValue) {
        type = CommandType.GAME_SETUP;
        this.game = game;
        this.playerId = playerId;
        this.deckValue = deckValue;
    }

    public PokerCommand(HoldemGame game) {
        type = CommandType.GAME_DATA;
        this.game = game;
    }

    public PokerCommand(List<User> users, CommandType commandType) {
        type = commandType;
        this.users = users;
    }

    public PokerCommand(Integer smallBlind, Integer bigBlind, Integer entryFee) {
        type = CommandType.GAME_SETUP;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.entryFee = entryFee;
    }

    public PokerCommand(Card card) {
        type = CommandType.DEAL_CARD;
        this.card = card;
    }

    public PokerCommand(Card card, CommandType commandType) {
        type = commandType;
        this.card = card;
    }

    public PokerCommand(CommandType type, Integer riseValue, Integer lastBet) {
        this.type = type;
        this.riseValue = riseValue;
        this.lastBet = lastBet;
    }

    public PokerCommand(Integer lastBet) {
        this.type = CommandType.CALL;
        this.lastBet = lastBet;
    }


    public PokerCommand(Integer deckValue, CommandType type) {
        this.type = type;
        this.deckValue = deckValue;
    }

    public PokerCommand(User user, String action, Integer cash, Integer callValue, Integer pot) {
        this.type = CommandType.PLAYER_ACTION;
        this.user = user;
        this.action = action;
        this.cash = cash;
        this.pot = pot;
        this.callValue = callValue;
    }
}
