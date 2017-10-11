package com.kubatatami.poker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jstasinski on 26/11/14.
 */
public class HoldemGame implements Serializable{
    public List<User> players = new ArrayList<User>();
    public List<Card> tableCards = new ArrayList<Card>();
    public Integer smallBlind;
    public HoldemGameState gameState;
    public Integer pot = 0;
    public Integer callValue;
    public Integer dealerPosition;
    public Integer decisionPosition = -1;
    public List<Integer> winnersId = new ArrayList<Integer>();
    public Boolean foldGame = false;
    public Integer gameStateIncrement = 0;
    public Integer entryFee = 0;


}
