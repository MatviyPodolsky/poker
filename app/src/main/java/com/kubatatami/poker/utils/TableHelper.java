package com.kubatatami.poker.utils;

import com.kubatatami.poker.model.Card;
import com.kubatatami.poker.model.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Kuba on 08/10/14.
 */
public class TableHelper {

    public static List<Card> randomTable(){
        Random random=new Random();
        List<Card> cardList = new ArrayList<Card>();
        for(int i=0;i<Card.NO_OF_RANKS;i++){
            for(int z =0;z<Card.NO_OF_COLORS;z++){
                cardList.add(new Card(i, Color.values()[z]));
            }
        }
        List<Card> randomCardList = new ArrayList<Card>(cardList.size());
        for(int i=cardList.size()-1;i>0;i--){
            Card card = cardList.remove(random.nextInt(i));
            randomCardList.add(card);
        }
        randomCardList.add(cardList.remove(0));
        return randomCardList;
    }

}
