package com.kubatatami.poker.model;

/**
 * Created by Kuba on 28/09/14.
 */
public enum GameType {
    TEXAS_HOLDEM("Texas Hold\'em"),
    BLACK_JACK("Black Jack"),
    BACCARAT("Baccarat");


    private String name;
    GameType(String str){
        name = str;
    }

    @Override
    public String toString() {
        return name;
    }
}
