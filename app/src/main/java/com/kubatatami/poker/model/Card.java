package com.kubatatami.poker.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.kubatatami.poker.PokerApp_;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.utils.WebPUtils;

import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by Kuba on 06/10/14.
 */
public class Card implements Serializable {

    protected int rank;
    protected Color color;


    public static final int ACE = 12;
    public static final int KING = 11;
    public static final int QUEEN = 10;
    public static final int JACK = 9;
    public static final int TEN = 8;
    public static final int NINE = 7;
    public static final int EIGHT = 6;
    public static final int SEVEN = 5;
    public static final int SIX = 4;
    public static final int FIVE = 3;
    public static final int FOUR = 2;
    public static final int THREE = 1;
    public static final int DEUCE = 0;

    public static final int NO_OF_RANKS = 13;

    /**
     * The number of suits in a deckView.
     */
    public static final int NO_OF_COLORS = 4;

    public Card() {
    }

    public Card(int rank, Color color) {
        this.rank = rank;
        this.color = color;
    }

    public int getRank() {
        return rank;
    }

    public Color getColor() {
        return color;
    }

    public Drawable getDrawable(Context context) {
        String rankName;
        if (rank == JACK) {
            rankName = "jack";
        } else if (rank == QUEEN) {
            rankName = "queen";
        } else if (rank == KING) {
            rankName = "king";
        } else if (rank == ACE) {
            rankName = "ace";
        } else {
            rankName = (rank + 2) + "";
        }

        String fileName = "cards_assets/" + "deck_" + PokerApp_.getInstance().getDeck() + PokerApp_.getInstance().getDimensionFolder() + color.name().toLowerCase() + "_" + rankName + ".png.webp";
        try {
            System.gc();
            InputStream is = context.getResources().getAssets().open(fileName);

            return new BitmapDrawable(Resources.getSystem(),WebPUtils.webpToBitmap(is));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
