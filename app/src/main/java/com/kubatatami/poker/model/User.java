package com.kubatatami.poker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 28/09/14.
 */
public class User implements Serializable {
    public String id;
    public String name;
    public String image;
    public Integer cash;
    public UserRoundState roundState;
    public Integer betInRound = 0;
    public CommandType lastCommand;
    public Integer riseValue;
    public String combination;
    public List<Card> cards = new ArrayList<>();
    public boolean isHost = false;

    public User() {
    }

    public User(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
        roundState = UserRoundState.PLAY;
    }

    public User(String id, String name, String image, int cash) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.cash = cash;
        roundState = UserRoundState.PLAY;
    }

    public void clearPlayerState(boolean clearFold) {
        if (clearFold) {
            lastCommand = null;
            roundState = UserRoundState.PLAY;
            riseValue = null;
            betInRound = 0;
            cards = new ArrayList<>();
        } else {
            if (lastCommand != null && !lastCommand.equals(CommandType.FOLD) && !lastCommand.equals(CommandType.ALL_IN)) {
                lastCommand = null;
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
