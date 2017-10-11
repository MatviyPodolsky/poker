package com.kubatatami.poker.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.kubatatami.judonetworking.observers.ObserverAdapter;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.model.Card;
import com.kubatatami.poker.model.CommandType;
import com.kubatatami.poker.model.HoldemGame;
import com.kubatatami.poker.model.HoldemGameState;
import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.User;
import com.kubatatami.poker.model.UserRoundState;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class GameHelper {

    @RootContext
    protected Context context;
    @Pref
    protected Settings_ settings;

    public List<Card> cards;
    public int handCardsSize = 0;
    public int tableCardsSize = 0;

    public int deckPosition = 0;

    public int lastGameStateIncrement = -1;
    public int playerId;
    public int playersGameReady = 0;
    public int playersRoundReady = 0;

    public ObserverAdapter<Card> cardAdapter;

    public HoldemGameState gameState = HoldemGameState.NULL;
    public HoldemGame holdemGame;

    public int giveOutIterator = 0;
    public int lastBet = 0;

    public int decisionIterator = 0;
    public boolean swiped = false;
    public boolean tutorial = false;


    private static int[][] permutations = {
            {0, 1, 2, 3, 4},
            {0, 1, 2, 3, 5},
            {0, 1, 2, 3, 6},
            {0, 1, 2, 4, 5},
            {0, 1, 2, 4, 6},
            {0, 1, 2, 5, 6},
            {0, 1, 3, 4, 5},
            {0, 1, 3, 4, 6},
            {0, 1, 3, 5, 6},
            {0, 1, 4, 5, 6},
            {0, 2, 3, 4, 5},
            {0, 2, 3, 4, 6},
            {0, 2, 3, 5, 6},
            {0, 2, 4, 5, 6},
            {0, 3, 4, 5, 6},
            {1, 2, 3, 4, 5},
            {1, 2, 3, 4, 6},
            {1, 2, 3, 5, 6},
            {1, 2, 4, 5, 6},
            {1, 3, 4, 5, 6},
            {2, 3, 4, 5, 6},
    };


    public void clearGameHelper() {
        cards = null;
        handCardsSize = 0;
        tableCardsSize = 0;

        deckPosition = 0;

        lastGameStateIncrement = -1;
        playersGameReady = 0;
        playersRoundReady = 0;

        cardAdapter = null;

        gameState = HoldemGameState.NULL;
        holdemGame = null;

        giveOutIterator = 0;
        lastBet = 0;

        decisionIterator = 0;
        swiped = false;
        tutorial = false;
    }

    public int getNextPlayingUserId(User currentUser) {
        int userIndex = holdemGame.players.indexOf(currentUser);
        userIndex++;
        for (int i = userIndex % holdemGame.players.size(); ; i = (i + 1) % holdemGame.players.size()) {
            if (holdemGame.players.get(i).roundState.equals(UserRoundState.PLAY)) {
                return i;
            }
        }

    }

    public boolean isGameOver() {
        int iterator = 0;

        if (holdemGame.players.get(playerId).cash <= 0) {
            return true;
        }
        for (int i = 0; i < holdemGame.players.size(); i++) {
            if (holdemGame.players.get(i).cash > 0) {
                iterator++;
            }
        }
        if (iterator > 1)
            return false;
        else
            return true;

    }

    public List<Integer> gameOverPlayersIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < holdemGame.players.size(); i++) {
            if (holdemGame.players.get(i).cash <= 0) {
                ids.add(i);
            }
        }
        return ids;

    }


    public int getNextUserId(int userId) {
        userId++;
        for (int i = userId % holdemGame.players.size(); ; i = (i + 1) % holdemGame.players.size()) {
            return i;
        }

    }

    public List<Integer> getWinnersId() {
        List<Integer> winnersId = new ArrayList<Integer>();
        int bestPlayerResult = -1;
        for (int i = 0; i < holdemGame.players.size(); i++) {
            if (holdemGame.players.get(i).roundState.equals(UserRoundState.PLAY)) {
                int result = calculateCardsValue(i);
                if (result > bestPlayerResult) {
                    winnersId = new ArrayList<Integer>();
                    winnersId.add(i);
                    bestPlayerResult = result;
                } else if (result == bestPlayerResult) {
                    winnersId.add(i);
                }
            }
        }
        return winnersId;
    }


    public int getWinnerIdOnFolds() {
        for (int i = 0; i < holdemGame.players.size(); i++) {
            if (holdemGame.players.get(i).roundState.equals(UserRoundState.PLAY)) {
                return i;
            }
        }
        return -1;
    }

    public void clear(ViewGroup cardTableView) {
        for (int i = 0; i < cardTableView.getChildCount(); i++) {
            ImageView cardView = (ImageView) cardTableView.getChildAt(i);
            cardView.setImageDrawable(null);
        }
        holdemGame.tableCards.clear();
    }

    public int playingPlayerNumber() {
        int number = 0;
        for (int i = 0; i < holdemGame.players.size(); i++) {
            if (!holdemGame.players.get(i).roundState.equals(UserRoundState.FOLD)) {
                number++;
            }
        }
        return number;
    }

    public int calculateCardsValue(int playerId) {
        int biggestValue = -1;
        List<Card> cards = new ArrayList<Card>();
        cards.addAll(holdemGame.tableCards);
        cards.addAll(holdemGame.players.get(playerId).cards);
        for (int i = 0; i < permutations.length; i++) {
            Card[] cardsArray = new Card[5];
            for (int k = 0; k < permutations[i].length; k++) {
                cardsArray[k] = cards.get(permutations[i][k]);
            }
            HandEvaluator he = new HandEvaluator(cardsArray);
            int result = he.getValue();
            if (result > biggestValue) {
                biggestValue = result;
                holdemGame.players.get(playerId).combination = he.getType().getDescription();
            }
        }

        return biggestValue;
    }


    @Background
    public void loadDrawableWithReverse(ImageView cardView, Card card, ImageView reverseView) {
        Drawable drawable = card.getDrawable(context);
        showDrawable(cardView, drawable, reverseView);
    }

    @Background
    public void loadDrawable(ImageView cardView, Card card) {
        Drawable drawable = card.getDrawable(context);
        showDrawable(cardView, drawable);
    }

    @Background
    public void loadCardDrawableWithoutAnim(ImageView cardView, Card card) {
        Drawable drawable = card.getDrawable(context);
        showDrawableWithoutAnim(cardView, drawable);
    }

    @UiThread
    public void showDrawableWithoutAnim(ImageView cardView, Drawable drawable) {
        cardView.setImageDrawable(drawable);


    }

    @UiThread
    public void showDrawable(ImageView cardView, Drawable drawable) {
        cardView.setImageDrawable(drawable);
        ObjectAnimator.ofFloat(cardView, "y", -cardView.getHeight(), cardView.getY()).setDuration(300).start();
    }

    @UiThread
    public void showDrawable(ImageView cardView, Drawable drawable, ImageView reverseView) {
        cardView.setImageDrawable(drawable);
        final AnimatorSet set = new AnimatorSet();
        set.playSequentially(ObjectAnimator.ofFloat(cardView, "y", -cardView.getHeight(), cardView.getY()).setDuration(300),
                ObjectAnimator.ofFloat(reverseView, View.ALPHA, 0, 1));
        set.start();
    }

    public void addCardGameToHandWithAnim(final Card card, final ImageView cardView, final ImageView cardViewRevers) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                loadDrawableWithReverse(cardView, card, cardViewRevers);
            }
        });
    }

    public void addCardGameToHand(final Card card) {
        holdemGame.players.get(playerId).cards.add(card);
    }

    public void addCardGameOnTableWithAnim(final Card card, final ImageView cardView) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                loadDrawable(cardView, card);
            }
        });
    }

    public void addCardGameOnTable(final Card card) {
        holdemGame.tableCards.add(card);
    }


}
