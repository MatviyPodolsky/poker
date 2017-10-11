package com.kubatatami.poker.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kubatatami.poker.Listener.ServerListener;
import com.kubatatami.poker.PokerApp_;
import com.kubatatami.poker.R;
import com.kubatatami.poker.adapters.ClientDiscoveryAdapter;
import com.kubatatami.poker.connection.ClientSession;
import com.kubatatami.poker.connection.ServerSession;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.dialogs.HoldemGameResultFragment;
import com.kubatatami.poker.dialogs.HoldemGameResultFragment_;
import com.kubatatami.poker.dialogs.QuitDialogFragment;
import com.kubatatami.poker.dialogs.QuitDialogFragment_;
import com.kubatatami.poker.model.Card;
import com.kubatatami.poker.model.CommandType;
import com.kubatatami.poker.model.HoldemGame;
import com.kubatatami.poker.model.HoldemGameState;
import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.ServerUser;
import com.kubatatami.poker.model.User;
import com.kubatatami.poker.model.UserRoundState;
import com.kubatatami.poker.ui.ClipLinearLayout;
import com.kubatatami.poker.utils.AvatarUtils;
import com.kubatatami.poker.utils.GameHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 * original by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_game)
public class GameActivityBlackJack extends BaseActivity implements View.OnDragListener, View.OnTouchListener,
        ServerListener, QuitDialogFragment.OnQuitListener, HoldemGameResultFragment.ResultCloseListener {

    public static final int QUIT_REQUEST = 1001, HIDE_BLEND_REQUEST = 1002;

    @Bean
    protected ClientSession clientSession;
    @Bean
    protected ServerSession serverSession;
    @Bean
    protected GameHelper gameHelper;
    @Pref
    protected Settings_ settings;
    @Extra
    protected Boolean host = false;
    @ViewById(R.id.game_card_table)
    protected ViewGroup cardTableView;
    @ViewById(R.id.game_hidden_cards)
    protected View hiddenCards;

    @ViewById(R.id.game_settings_container)
    protected ClipLinearLayout settingsContainer;

    @ViewById(R.id.game_sound_settings)
    protected ImageView soundSettings;

    @ViewById(R.id.game_accept_bet)
    protected View acceptBetButton;

    @ViewById(R.id.game_ready_container)
    protected View readyContainer;

    @ViewById(R.id.waiting_for_player_container)
    protected View waitingContainer;

    @ViewById(R.id.waiting_for_player_text)
    protected TextView waitingText;

    @ViewById(R.id.disconnected_container)
    protected View disconnectContainer;

    @ViewById(R.id.game_coin_1)
    protected ImageView gameCoin1;
    @ViewById(R.id.game_coin_2)
    protected ImageView gameCoin2;
    @ViewById(R.id.game_coin_5)
    protected ImageView gameCoin5;
    @ViewById(R.id.game_coin_10)
    protected ImageView gameCoin10;


    @ViewById(R.id.holdem_blend)
    protected View holdemBlendView;

    @ViewById(R.id.game_hand_first_card)
    protected ImageView firstHandCard;
    @ViewById(R.id.game_hand_first_card_revers)
    protected ImageView firstHandCardRevers;

    @ViewById(R.id.game_hand_second_card)
    protected ImageView secondHandCard;
    @ViewById(R.id.game_hand_second_card_revers)
    protected ImageView secondHandCardRevers;

    @ViewById(R.id.game_hand_second_card)
    protected ImageView thirdHandCard;
    @ViewById(R.id.game_hand_second_card_revers)
    protected ImageView thirdHandCardRevers;

    @ViewById(R.id.game_hand_second_card)
    protected ImageView fourthHandCard;
    @ViewById(R.id.game_hand_second_card_revers)
    protected ImageView fourthHandCardRevers;


    @ViewById(R.id.game_bet_container)
    protected ViewGroup betContainer;

    @ViewById(R.id.game_decision_container)
    protected ViewGroup decisionContainer;

    @ViewById(R.id.game_fold_button)
    protected TextView foldView;

    @ViewById(R.id.game_call_button)
    protected TextView callView;

    @ViewById(R.id.game_rise_button)
    protected TextView riseView;

    @ViewById(R.id.game_bet_value)
    protected TextView betValue;

    @ViewById(R.id.game_pot)
    protected TextView potValue;

    //players
    @ViewById(R.id.game_player_1)
    protected View p1View;
    @ViewById(R.id.game_player_2)
    protected View p2View;
    @ViewById(R.id.game_player_3)
    protected View p3View;
    @ViewById(R.id.game_player_4)
    protected View p4View;


    @ViewById(R.id.game_player_1_avatar)
    protected ImageView p1Avatar;
    @ViewById(R.id.game_player_1_cash)
    protected TextView p1Cash;
    @ViewById(R.id.game_player_1_action)
    protected TextView p1Action;
    @ViewById(R.id.game_player_1_blind)
    protected TextView p1Blind;
    @ViewById(R.id.game_player_1_dealer)
    protected TextView p1Dealer;

    @ViewById(R.id.game_player_2_avatar)
    protected ImageView p2Avatar;
    @ViewById(R.id.game_player_2_cash)
    protected TextView p2Cash;
    @ViewById(R.id.game_player_2_action)
    protected TextView p2Action;
    @ViewById(R.id.game_player_2_blind)
    protected TextView p2Blind;
    @ViewById(R.id.game_player_2_dealer)
    protected TextView p2Dealer;

    @ViewById(R.id.game_player_3_avatar)
    protected ImageView p3Avatar;
    @ViewById(R.id.game_player_3_cash)
    protected TextView p3Cash;
    @ViewById(R.id.game_player_3_action)
    protected TextView p3Action;
    @ViewById(R.id.game_player_3_blind)
    protected TextView p3Blind;
    @ViewById(R.id.game_player_3_dealer)
    protected TextView p3Dealer;

    @ViewById(R.id.game_player_4_avatar)
    protected ImageView p4Avatar;
    @ViewById(R.id.game_player_4_cash)
    protected TextView p4Cash;
    @ViewById(R.id.game_player_4_action)
    protected TextView p4Action;
    @ViewById(R.id.game_player_4_blind)
    protected TextView p4Blind;
    @ViewById(R.id.game_player_4_dealer)
    protected TextView p4Dealer;


    private int bet = 0;

    @AfterViews
    protected void afterViews() {
        settingsContainer.setProgress(0f);
        soundSettings.setImageLevel(settings.sound().get() ? 1 : 0);
        firstHandCardRevers.setImageLevel(PokerApp_.getInstance().getDeck());
        secondHandCardRevers.setImageLevel(PokerApp_.getInstance().getDeck());
        thirdHandCardRevers.setImageLevel(PokerApp_.getInstance().getDeck());
        fourthHandCardRevers.setImageLevel(PokerApp_.getInstance().getDeck());

        if (settings.showBlend().get() && !gameHelper.tutorial) {
            holdemBlendView.setVisibility(View.VISIBLE);
        } else {
            gameHelper.tutorial = true;
        }

        if (host) {
            if (gameHelper.holdemGame == null) {
                gameHelper.holdemGame = new HoldemGame();
                gameHelper.holdemGame.gameState = HoldemGameState.NULL;
            }

            serverSession.setServerListener(this);
            if (gameHelper.gameState.equals(HoldemGameState.NULL)) {
                gameHelper.holdemGame.smallBlind = settings.smallBlind().get();
                gameHelper.holdemGame.players = new ArrayList<>(serverSession.getAcceptedUsers());
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                gameHelper.holdemGame.players.add(0, new User(androidId, settings.name().get(), settings.image().get()));
                gameHelper.holdemGame.players.get(0).isHost = true;
                gameHelper.holdemGame.dealerPosition = 0;

                int entryFee = settings.entryFee().get();
                gameHelper.holdemGame.entryFee = entryFee;
                for (User user : gameHelper.holdemGame.players) {
                    user.cash = entryFee;
                }
                gameHelper.playerId = 0;
            }
            statusBarUpdatePlayersNumber();
            updateStatusBarCash();
        } else {
            clientSession.setClientDiscoveryListener(clientDiscoveryAdapter);
            if (gameHelper.gameState.equals(HoldemGameState.NULL)) {
                clientSession.sendToServer(new PokerCommand(CommandType.GAME_READY));
            }
        }


        hiddenCards.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gameHelper.holdemGame.players.get(gameHelper.playerId).cards.size() < 2)
                    return false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    firstHandCardRevers.setVisibility(View.INVISIBLE);
                    secondHandCardRevers.setVisibility(View.INVISIBLE);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    firstHandCardRevers.setVisibility(View.VISIBLE);
                    secondHandCardRevers.setVisibility(View.VISIBLE);
                    return true;
                }

                return false;
            }
        });

        betContainer.setOnDragListener(this);
        gameCoin1.setOnTouchListener(this);
        gameCoin2.setOnTouchListener(this);
        gameCoin5.setOnTouchListener(this);
        gameCoin10.setOnTouchListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreGameView();
        if (host) {
            serverSession.setServerListener(this);
            if (gameHelper.holdemGame.players.get(0).cards.size() == 2) {
                gameHelper.decisionIterator = gameHelper.playingPlayerNumber();
                User player = gameHelper.holdemGame.players.get(0);
                if (gameHelper.giveOutIterator == gameHelper.holdemGame.players.size() * 2) {
                    player = gameHelper.holdemGame.players.get(gameHelper.getNextUserId(gameHelper.getNextUserId(0)));
                }
                sendOrNotDecision(player);

            }
        } else {
            clientSession.setClientDiscoveryListener(clientDiscoveryAdapter);
        }
    }

    @Click(R.id.game_settings)
    public void settingsClick() {
        if (settingsContainer.getProgress() == 0f) {
            ObjectAnimator.ofFloat(settingsContainer, "progress", 0f, 1f).setDuration(200).start();
        } else {
            ObjectAnimator.ofFloat(settingsContainer, "progress", 1f, 0f).setDuration(200).start();
        }
    }

    @Click(R.id.game_sound_settings)
    protected void soundClick() {
        if (settings.sound().get()) {
            settings.sound().put(false);
        } else {
            settings.sound().put(true);
        }
        soundSettings.setImageLevel(settings.sound().get() ? 1 : 0);
    }

    @Click(R.id.game_exit_settings)
    protected void exitSettingsClick() {
        QuitDialogFragment_.builder().requestCode(QUIT_REQUEST).build().show(getFragmentManager(), "quit");
    }

    @Click(R.id.holdem_blend)
    public void holdemBlendClick() {
        QuitDialogFragment_.builder().requestCode(HIDE_BLEND_REQUEST).message(getString(R.string.dismiss_tutorial)).tick(true).yesText(getString(R.string.yes)).build().show(getFragmentManager(), "quit");
    }

    protected void restoreGameView() {
        clearStatusBarAction();
        if (gameHelper.holdemGame == null || gameHelper.holdemGame.gameState.equals(HoldemGameState.NULL))
            return;

        for (int i = 0; i < gameHelper.holdemGame.tableCards.size(); i++) {
            final ImageView cardView = (ImageView) cardTableView.getChildAt(i);
            gameHelper.loadCardDrawableWithoutAnim(cardView, gameHelper.holdemGame.tableCards.get(i));
        }
        gameHelper.tableCardsSize = gameHelper.holdemGame.tableCards.size();
        for (int i = 0; i < gameHelper.holdemGame.players.get(gameHelper.playerId).cards.size(); i++) {
            ImageView cardView = i == 0 ? firstHandCard : secondHandCard;
            ImageView cardViewRevers = i == 0 ? firstHandCardRevers : secondHandCardRevers;
            gameHelper.loadCardDrawableWithoutAnim(cardView, gameHelper.holdemGame.players.get(gameHelper.playerId).cards.get(i));
            cardViewRevers.setAlpha(1f);

        }
        if (!checkBlinds() || !gameHelper.holdemGame.gameState.equals(HoldemGameState.HOLE_CARDS)) {
            gameHelper.lastBet = 0;
        }
        gameHelper.handCardsSize = gameHelper.holdemGame.players.get(gameHelper.playerId).cards.size();
        if (!host && gameHelper.holdemGame.players != null) {
            updateGame();
        }
    }


    @Override
    public boolean onDrag(View layoutview, DragEvent dragevent) {

        int action = dragevent.getAction();
        View view = (View) dragevent.getLocalState();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                if (view.equals(gameCoin1)) {
                    bet += 1;
                } else if (view.equals(gameCoin2)) {
                    bet += 2;
                } else if (view.equals(gameCoin5)) {
                    bet += 5;
                } else if (view.equals(gameCoin10)) {
                    bet += 10;
                }
                if (bet > (gameHelper.holdemGame.players.get(gameHelper.playerId).cash + gameHelper.lastBet)) {
                    bet = gameHelper.holdemGame.players.get(gameHelper.playerId).cash + gameHelper.lastBet;
                }
                soundManager.playRandomSound(R.raw.coin_01, R.raw.coin_02);
                updateBet();
                break;
            case DragEvent.ACTION_DRAG_ENDED:

                break;
            default:
                break;
        }
        return true;
    }

    private boolean dropEventNotHandled(DragEvent dragEvent) {
        return !dragEvent.getResult();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(null, shadowBuilder, view, 0);
            soundManager.playRandomSound(R.raw.coin_01, R.raw.coin_02);
            return true;
        } else {
            return false;
        }
    }

    @Click(R.id.game_ready_ready_button)
    protected void readyClick() {
        readyContainer.setVisibility(View.GONE);
        if (host) {
            serverGetRoundReady();
        } else {
            clientSession.sendToServer(new PokerCommand(CommandType.ROUND_READY));
        }

    }

    @Click(R.id.game_ready_quit_button)
    protected void quitClick() {
        readyContainer.setVisibility(View.GONE);
        QuitDialogFragment_.builder().requestCode(QUIT_REQUEST).build().show(getFragmentManager(), "quit");
    }

    @Click(R.id.game_reject_bet)
    protected void rejectBetClick() {
        bet = gameHelper.holdemGame.callValue;
        updateBet();
        betContainer.setVisibility(View.GONE);
        decisionContainer.setVisibility(View.VISIBLE);
    }

    @Click(R.id.game_accept_bet)
    protected void acceptBetClick() {
        CommandType type = gameHelper.holdemGame.callValue == 0 ? CommandType.BET : CommandType.RISE;
        PokerCommand command = new PokerCommand(type, bet, gameHelper.lastBet);
        if (host) {
            gameHelper.holdemGame.players.get(0).cash -= (bet - gameHelper.lastBet);
            gameHelper.holdemGame.players.get(0).betInRound += (bet - gameHelper.lastBet);
            gameHelper.holdemGame.callValue = bet;
            gameHelper.holdemGame.pot += (bet - gameHelper.lastBet);
            gameHelper.holdemGame.players.get(0).lastCommand = type;
            if (gameHelper.holdemGame.players.get(0).cash == 0) {
                gameHelper.holdemGame.players.get(0).lastCommand = CommandType.ALL_IN;
            }
            gameHelper.holdemGame.players.get(0).riseValue = bet;
            gameHelper.decisionIterator = gameHelper.playingPlayerNumber() - 1;
            sendOrNotDecision(gameHelper.holdemGame.players.get(0));
        } else {
            clientSession.sendToServer(command);
        }
        gameHelper.lastBet = bet;
        updateGame();
        betContainer.setVisibility(View.GONE);
    }

    @Click(R.id.game_call_button)
    protected void callClick() {
        PokerCommand command = new PokerCommand(gameHelper.lastBet);
        if (host) {
            gameHelper.holdemGame.players.get(0).lastCommand = gameHelper.holdemGame.callValue == 0 ? CommandType.CHECK : CommandType.CALL;
            gameHelper.holdemGame.players.get(0).riseValue = 0;
            gameHelper.holdemGame.players.get(0).cash -= (gameHelper.holdemGame.callValue - gameHelper.lastBet);
            gameHelper.holdemGame.players.get(0).betInRound += (gameHelper.holdemGame.callValue - gameHelper.lastBet);
            gameHelper.holdemGame.pot += (gameHelper.holdemGame.callValue - gameHelper.lastBet);
            gameHelper.lastBet = gameHelper.holdemGame.callValue;
            if (gameHelper.holdemGame.players.get(0).cash == 0) {
                gameHelper.holdemGame.players.get(0).lastCommand = CommandType.ALL_IN;
            }
            gameHelper.decisionIterator--;
            sendOrNotDecision(gameHelper.holdemGame.players.get(0));
        } else {
            clientSession.sendToServer(command);
        }
        decisionContainer.setVisibility(View.GONE);
        updatePot();
        updateStatusBarCash();
    }

    @Click(R.id.game_rise_button)
    protected void riseClick() {
        //todo: many options of start bet
//        if (gameHelper.callValue == 0) {
//            bet = gameHelper.bigBlind;
//        } else {
        bet = gameHelper.holdemGame.callValue;
//        }
        updateBet();
        betContainer.setVisibility(View.VISIBLE);
        decisionContainer.setVisibility(View.GONE);
    }

    @Click(R.id.game_fold_button)
    protected void foldClick() {
        PokerCommand command = new PokerCommand(CommandType.FOLD);
        if (host) {
            gameHelper.holdemGame.players.get(0).roundState = UserRoundState.FOLD;
            gameHelper.holdemGame.players.get(0).lastCommand = CommandType.FOLD;
            gameHelper.holdemGame.players.get(0).riseValue = 0;
            gameHelper.decisionIterator--;
            sendOrNotDecision(gameHelper.holdemGame.players.get(0));
        } else {
            clientSession.sendToServer(command);
        }
        decisionContainer.setVisibility(View.GONE);
    }

    private void updateStatusBarCash() {
        for (int i = 0; i < gameHelper.holdemGame.players.size(); i++) {
            switch (i) {
                case 0:
                    p1Cash.setText(getString(R.string.cash_, gameHelper.holdemGame.players.get(i).cash));
                    break;
                case 1:
                    p2Cash.setText(getString(R.string.cash_, gameHelper.holdemGame.players.get(i).cash));
                    break;
                case 2:
                    p3Cash.setText(getString(R.string.cash_, gameHelper.holdemGame.players.get(i).cash));
                    break;
                case 3:
                    p4Cash.setText(getString(R.string.cash_, gameHelper.holdemGame.players.get(i).cash));
                    break;
            }
        }
    }

    private int getCurrentPlayerId() {
        if (gameHelper.holdemGame.decisionPosition >= 0) {
            return gameHelper.holdemGame.decisionPosition;
        } else {
            return gameHelper.holdemGame.dealerPosition;
        }
    }

    private void setCurrentUserIndicator() {
        switch (getCurrentPlayerId()) {
            case 0:
                p1View.setBackgroundResource(R.drawable.rounded_yellow_bgr);
                break;
            case 1:
                p2View.setBackgroundResource(R.drawable.rounded_yellow_bgr);
                break;
            case 2:
                p3View.setBackgroundResource(R.drawable.rounded_yellow_bgr);
                break;
            case 3:
                p4View.setBackgroundResource(R.drawable.rounded_yellow_bgr);
                break;
        }
    }

    private void clearPlayersBackground() {
        p1View.setBackgroundResource(R.drawable.rounded_black_bgr);
        p2View.setBackgroundResource(R.drawable.rounded_black_bgr);
        p3View.setBackgroundResource(R.drawable.rounded_black_bgr);
        p4View.setBackgroundResource(R.drawable.rounded_black_bgr);
    }

    private void updateStatusBarBlindAndDeal() {
        p1Dealer.setVisibility(View.INVISIBLE);
        p2Dealer.setVisibility(View.INVISIBLE);
        p3Dealer.setVisibility(View.INVISIBLE);
        p4Dealer.setVisibility(View.INVISIBLE);
        switch (gameHelper.holdemGame.dealerPosition) {
            case 0:
                p1Dealer.setVisibility(View.VISIBLE);
                break;
            case 1:
                p2Dealer.setVisibility(View.VISIBLE);
                break;
            case 2:
                p3Dealer.setVisibility(View.VISIBLE);
                break;
            case 3:
                p4Dealer.setVisibility(View.VISIBLE);
                break;
        }
        clearBlinds();
        updateSmallBlind(gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition));
        updateBigBlind(gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition + 1));
    }


    private void updateSmallBlind(int smallBlindPosition) {
        switch (smallBlindPosition) {
            case 0:
                p1Blind.setText(getString(R.string.s));
                break;
            case 1:
                p2Blind.setText(getString(R.string.s));
                break;
            case 2:
                p3Blind.setText(getString(R.string.s));
                break;
            case 3:
                p4Blind.setText(getString(R.string.s));
                break;
        }
    }

    private void clearBlinds() {
        p1Blind.setText("");
        p2Blind.setText("");
        p3Blind.setText("");
        p4Blind.setText("");
    }

    private void updateBigBlind(int bigBlindPosition) {
        switch (bigBlindPosition) {
            case 0:
                p1Blind.setText(getString(R.string.b));
                break;
            case 1:
                p2Blind.setText(getString(R.string.b));
                break;
            case 2:
                p3Blind.setText(getString(R.string.b));
                break;
            case 3:
                p4Blind.setText(getString(R.string.b));
                break;
        }
    }


    private void clearStatusBarAction() {
        p1Action.setText("");
        p2Action.setText("");
        p3Action.setText("");
        p4Action.setText("");

    }

    private void statusBarUpdatePlayersNumber() {
        p1View.setVisibility(View.VISIBLE);
        p2View.setVisibility(View.VISIBLE);
        p3View.setVisibility(View.VISIBLE);
        p4View.setVisibility(View.VISIBLE);
        AvatarUtils.showThumb(p1Avatar, gameHelper.holdemGame.players.get(0).image);
        AvatarUtils.showThumb(p2Avatar, gameHelper.holdemGame.players.get(1).image);
        if (gameHelper.holdemGame.players.size() < 4) {
            p4View.setVisibility(View.GONE);
        } else {
            AvatarUtils.showThumb(p4Avatar, gameHelper.holdemGame.players.get(3).image);
        }
        if (gameHelper.holdemGame.players.size() < 3) {
            p3View.setVisibility(View.GONE);
        } else {
            AvatarUtils.showThumb(p3Avatar, gameHelper.holdemGame.players.get(2).image);
        }
    }

    private boolean checkBlinds() {
        if (gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition) == gameHelper.playerId) {
            gameHelper.lastBet = gameHelper.holdemGame.smallBlind;
            return true;
        }
        if (gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition + 1) == gameHelper.playerId) {
            gameHelper.lastBet = gameHelper.holdemGame.smallBlind * 2;
            return true;
        }
        return false;
    }

    private void updateGame() {
        if (gameHelper.holdemGame.gameStateIncrement <= gameHelper.lastGameStateIncrement) {
            return;
        } else {
            gameHelper.lastGameStateIncrement = gameHelper.holdemGame.gameStateIncrement;
        }
        clearPlayersBackground();
        waitingContainer.setVisibility(View.GONE);
        if (gameHelper.holdemGame.gameState.equals(HoldemGameState.RESULT)) {
            HoldemGameResultFragment_.builder().build().show(getFragmentManager(), "quit");
            return;
        }
        if (gameHelper.holdemGame.gameState.equals(HoldemGameState.NULL)) {
            if (gameHelper.tutorial) {
                showContainer(readyContainer);
            }

            statusBarUpdatePlayersNumber();
            updateStatusBarBlindAndDeal();
            updatePlayerState();
            updateStatusBarCash();
            return;
        }
        if (gameHelper.holdemGame.gameState.equals(HoldemGameState.CLEAR)) {
            clearGameViewForRoundStart();
        }
        if (gameHelper.holdemGame.dealerPosition == gameHelper.playerId && gameHelper.holdemGame.decisionPosition == -2) {
            SwipeActivity_.intent(this).host(host).startForResult(0);
            return;
        }
        if (gameHelper.handCardsSize == 0 && gameHelper.holdemGame.players.get(gameHelper.playerId).cards.size() == 1) {
            gameHelper.addCardGameToHandWithAnim(gameHelper.holdemGame.players.get(gameHelper.playerId).cards.get(0), firstHandCard, firstHandCardRevers);
        }
        if (gameHelper.handCardsSize == 1 && gameHelper.holdemGame.players.get(gameHelper.playerId).cards.size() == 2) {
            gameHelper.addCardGameToHandWithAnim(gameHelper.holdemGame.players.get(gameHelper.playerId).cards.get(1), secondHandCard, secondHandCardRevers);
            checkBlinds();
        }
        gameHelper.handCardsSize = gameHelper.holdemGame.players.get(gameHelper.playerId).cards.size();
        if (gameHelper.tableCardsSize == gameHelper.holdemGame.tableCards.size() - 1) {
            final ImageView cardView = (ImageView) cardTableView.getChildAt(gameHelper.tableCardsSize);
            gameHelper.addCardGameOnTableWithAnim(gameHelper.holdemGame.tableCards.get(gameHelper.tableCardsSize), cardView);
            gameHelper.lastBet = 0;
            clearPlayerState(false);

        }
        gameHelper.tableCardsSize = gameHelper.holdemGame.tableCards.size();
        updateStatusBarBlindAndDeal();
        updatePlayerState();
        updateStatusBarCash();
        updatePot();
        setCurrentUserIndicator();
        if (gameHelper.holdemGame.decisionPosition == gameHelper.playerId) {
            prepareDecisionContainer();
            if (gameHelper.holdemGame.players.get(gameHelper.playerId).lastCommand != null && gameHelper.holdemGame.players.get(gameHelper.playerId).lastCommand.equals(CommandType.ALL_IN)) {
                if (host) {
                    gameHelper.decisionIterator--;
                    sendOrNotDecision(gameHelper.holdemGame.players.get(0));
                } else {
                    clientSession.sendToServer(new PokerCommand(CommandType.ALL_IN));
                }
            } else {
                showContainer(decisionContainer);
            }
        } else {
            waitingText.setText(getString(R.string.waiting_for_, gameHelper.holdemGame.players.get(getCurrentPlayerId()).name));
            waitingContainer.setVisibility(View.VISIBLE);
            decisionContainer.setVisibility(View.GONE);
        }
    }


    private void prepareDecisionContainer() {
        if (gameHelper.holdemGame.callValue == 0) {
            foldView.setVisibility(View.GONE);
            callView.setText(getString(R.string.check));
            riseView.setText(getString(R.string.bet));
        } else {
            foldView.setVisibility(View.VISIBLE);
            callView.setText(getString(R.string.call));
            riseView.setText(getString(R.string.rise));
        }
    }

    private void sendOrNotDecision(User currentUser) {
        if (gameHelper.playingPlayerNumber() > 1) {
            if (gameHelper.holdemGame.gameState.equals(HoldemGameState.THE_RIVER) && gameHelper.decisionIterator == 0) {
                List<Integer> winnersId = gameHelper.getWinnersId();
                gameHelper.holdemGame.winnersId = winnersId;
                for (Integer playerId : winnersId) {
                    gameHelper.holdemGame.players.get(playerId).cash += (gameHelper.holdemGame.pot / winnersId.size());
                }
                gameHelper.holdemGame.gameState = HoldemGameState.RESULT;
                gameHelper.holdemGame.foldGame = false;

            } else {
                if (checkRoundState()) {
                    gameHelper.holdemGame.decisionPosition = gameHelper.getNextPlayingUserId(currentUser);
                } else {
                    gameHelper.decisionIterator = gameHelper.playingPlayerNumber();
                    gameHelper.holdemGame.decisionPosition = -2;
                }
            }
        } else {
            gameHelper.holdemGame.foldGame = true;
            if (gameHelper.getWinnerIdOnFolds() != -1) {
                gameHelper.holdemGame.players.get(gameHelper.getWinnerIdOnFolds()).cash += gameHelper.holdemGame.pot;
            }
            gameHelper.holdemGame.winnersId = new ArrayList<Integer>();
            gameHelper.holdemGame.winnersId.add(gameHelper.getWinnerIdOnFolds());
            gameHelper.holdemGame.gameState = HoldemGameState.RESULT;

        }
        incrementGameState(5);
        serverSession.sendAllAccept(new PokerCommand(gameHelper.holdemGame));
        updateGame();

    }


    private void incrementGameState(int incrementValue) {
        gameHelper.holdemGame.gameStateIncrement += incrementValue;
    }


    private void prepareNextRound() {
        gameHelper.playersRoundReady = 0;
        gameHelper.holdemGame.gameState = HoldemGameState.NULL;
        gameHelper.holdemGame.dealerPosition = gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition);
        gameHelper.decisionIterator = gameHelper.playingPlayerNumber();
        gameHelper.holdemGame.tableCards = new ArrayList<Card>();
        gameHelper.holdemGame.callValue = 0;
        gameHelper.giveOutIterator = 0;
        gameHelper.holdemGame.pot = 0;
        clearPlayerState(true);
        addBlindsToPot();
        incrementGameState(5);
        serverSession.sendAllAccept(new PokerCommand(gameHelper.holdemGame));
        updateGame();
    }

    private void addBlindsToPot() {
        gameHelper.holdemGame.players.get(gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition)).cash -= gameHelper.holdemGame.smallBlind;
        gameHelper.holdemGame.players.get(gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition)).betInRound += gameHelper.holdemGame.smallBlind;
        gameHelper.holdemGame.players.get(gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition + 1)).cash -= (gameHelper.holdemGame.smallBlind * 2);
        gameHelper.holdemGame.players.get(gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition + 1)).betInRound += (gameHelper.holdemGame.smallBlind * 2);
        gameHelper.holdemGame.pot = gameHelper.holdemGame.smallBlind * 3;
        gameHelper.holdemGame.callValue = (gameHelper.holdemGame.smallBlind * 2);
    }

    private void giveOutCard(Card card) {
        if (gameHelper.giveOutIterator < gameHelper.holdemGame.players.size() * 2) {
            int userPosition = (gameHelper.giveOutIterator + (gameHelper.holdemGame.dealerPosition + 1)) % gameHelper.holdemGame.players.size();
            gameHelper.holdemGame.players.get(userPosition).cards.add(card);
        } else {
            gameHelper.holdemGame.tableCards.add(card);
        }
        if (gameHelper.holdemGame.tableCards.size() >= 0 && gameHelper.holdemGame.tableCards.size() < 3) {
            gameHelper.holdemGame.gameState = HoldemGameState.HOLE_CARDS;
        } else if (gameHelper.holdemGame.tableCards.size() == 3) {
            gameHelper.holdemGame.gameState = HoldemGameState.THE_FLOP;
        } else if (gameHelper.holdemGame.tableCards.size() == 4) {
            gameHelper.holdemGame.gameState = HoldemGameState.THE_TURN;
        } else {
            gameHelper.holdemGame.gameState = HoldemGameState.THE_RIVER;
        }
        gameHelper.holdemGame.decisionPosition = -1;
        gameHelper.holdemGame.gameStateIncrement++;
        serverSession.sendAllAccept(new PokerCommand(gameHelper.holdemGame));
        gameHelper.giveOutIterator++;
        if (gameHelper.giveOutIterator == gameHelper.holdemGame.players.size() * 2 || gameHelper.giveOutIterator == (gameHelper.holdemGame.players.size() * 2 + 3)
                || gameHelper.giveOutIterator == (gameHelper.holdemGame.players.size() * 2 + 4)
                || gameHelper.giveOutIterator == (gameHelper.holdemGame.players.size() * 2 + 5)) {
            User player = gameHelper.holdemGame.players.get(gameHelper.holdemGame.dealerPosition);
            if (gameHelper.giveOutIterator != gameHelper.holdemGame.players.size() * 2) {
                gameHelper.holdemGame.callValue = 0;
            } else {
                player = gameHelper.holdemGame.players.get(gameHelper.getNextUserId(gameHelper.getNextUserId(gameHelper.holdemGame.dealerPosition)));
            }
            gameHelper.decisionIterator = gameHelper.playingPlayerNumber();
            gameHelper.holdemGame.gameStateIncrement++;
            sendOrNotDecision(player);
        } else {
            updateGame();
        }


    }


    private void clearGameViewForRoundStart() {
        gameHelper.tableCardsSize = 0;
        gameHelper.handCardsSize = 0;
        for (int i = 0; i < cardTableView.getChildCount(); i++) {
            ((ImageView) cardTableView.getChildAt(i)).setImageBitmap(null);
            ((ImageView) cardTableView.getChildAt(i)).destroyDrawingCache();
        }
        firstHandCard.setImageBitmap(null);
        firstHandCard.destroyDrawingCache();
        secondHandCard.setImageBitmap(null);
        secondHandCard.destroyDrawingCache();
        firstHandCardRevers.setAlpha(0f);
        secondHandCardRevers.setAlpha(0f);
        gameHelper.cardAdapter = null;

    }


    private String createAction(CommandType type, Integer riseValue) {
        if (type == null) return "";
        if (type == CommandType.FOLD) {
            return getString(R.string.fold);
        }
        if (type == CommandType.CALL) {
            return getString(R.string.call);
        }
        if (type == CommandType.CHECK) {
            return getString(R.string.check);
        }
        if (type == CommandType.RISE) {
            return getString(R.string.rise_, riseValue);
        }
        if (type == CommandType.BET) {
            return getString(R.string.bet_, riseValue);
        }
        if (type == CommandType.ALL_IN) {
            return getString(R.string.all_in);
        }
        return "";
    }


    private void updatePlayerState() {
        for (int i = 0; i < gameHelper.holdemGame.players.size(); i++) {
            String state = createAction(gameHelper.holdemGame.players.get(i).lastCommand, gameHelper.holdemGame.players.get(i).riseValue);
            switch (i) {
                case 0:
                    p1Action.setText(state);
                    break;
                case 1:
                    p2Action.setText(state);
                    break;
                case 2:
                    p3Action.setText(state);
                    break;
                case 3:
                    p4Action.setText(state);
                    break;

            }
        }
    }

    private void updatePot() {
        potValue.setText(getString(R.string.pot, gameHelper.holdemGame.pot));
    }

    private boolean checkRoundState() {
        if (gameHelper.decisionIterator != 0) {
            return true;
        } else {
            return false;
        }
    }


    private void clearPlayerState(boolean fold) {
        for (int i = 0; i < gameHelper.holdemGame.players.size(); i++) {
            gameHelper.holdemGame.players.get(i).clearPlayerState(fold);
        }
    }


    private void updateBet() {
        betValue.setText(getString(R.string.bet_, bet));
        if (bet > gameHelper.holdemGame.callValue) {
            acceptBetButton.setEnabled(true);
        } else {
            acceptBetButton.setEnabled(false);
        }
    }

    private void serverGetReady(User user) {
        gameHelper.playersGameReady++;
        if (gameHelper.playersGameReady == gameHelper.holdemGame.players.size() - 1) {
            addBlindsToPot();
            incrementGameState(5);
            for (int i = 1; i < gameHelper.holdemGame.players.size(); i++) {
                serverSession.sendUser(gameHelper.holdemGame.players.get(i), new PokerCommand(gameHelper.holdemGame, i, settings.deck().get()));
            }
        }
        if (gameHelper.playersGameReady > gameHelper.holdemGame.players.size() - 1) {
            int playerId = gameHelper.holdemGame.players.indexOf(user);
            if (playerId != -1) {
                incrementGameState(1);
                serverSession.sendUser(user, new PokerCommand(gameHelper.holdemGame, playerId, settings.deck().get()));
            }
        }
        updateGame();
    }

    private void serverGetRoundReady() {
        gameHelper.holdemGame.gameState = HoldemGameState.CLEAR;
        gameHelper.playersRoundReady++;
        if (gameHelper.playersRoundReady == gameHelper.holdemGame.players.size()) {
            gameHelper.holdemGame.decisionPosition = -2;
            gameHelper.holdemGame.gameStateIncrement++;
            serverSession.sendAllAccept(new PokerCommand(gameHelper.holdemGame));
            updateGame();
        }
    }

    private void showContainer(View container) {
        AnimatorSet dialogAnimatorSet = new AnimatorSet();
        List<Animator> objectAnimators = new ArrayList<Animator>();
        objectAnimators.add(ObjectAnimator.ofFloat(container, "scaleX", 0f, 1.0f));
        objectAnimators.add(ObjectAnimator.ofFloat(container, "scaleY", 0f, 1.0f));
        dialogAnimatorSet.setInterpolator(new LinearInterpolator());
        dialogAnimatorSet.playTogether(objectAnimators);
        dialogAnimatorSet.setDuration(300);
        container.setVisibility(View.VISIBLE);
        dialogAnimatorSet.start();
    }

    //server
    @Override
    public void onServerStart() {
        //TODO error toast for user
        finish();
    }

    @Override
    public void onServerError() {
        //TODO error toast for user
        finish();
    }

    @Override
    public void onClientConnect(User user) {
        //TODO
        Toast.makeText(this, "User back", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClientDisconnect(User user) {
        disconnectContainer.setVisibility(View.VISIBLE);
        Toast.makeText(this, "User disconnect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServerClose() {
        //TODO error toast for user
        finish();
    }

    @Override
    public void onMessage(User user, PokerCommand command) {

        if (command.type.equals(CommandType.GAME_READY)) {
            serverGetReady(user);
            checkAllConnectedAfterInterrups();
        }
        if (command.type.equals(CommandType.ROUND_READY)) {
            serverGetRoundReady();
        }
        if (command.type.equals(CommandType.DEAL_CARD_PROXY)) {
            giveOutCard(command.card);
        }
        if (command.type.equals(CommandType.FOLD) || command.type.equals(CommandType.RISE)
                || command.type.equals(CommandType.CALL) || command.type.equals(CommandType.BET)
                || command.type.equals(CommandType.ALL_IN)) {
            int currentUserId = gameHelper.holdemGame.players.indexOf(user);
            if (command.type.equals(CommandType.FOLD)) {
                gameHelper.holdemGame.players.get(currentUserId).roundState = UserRoundState.FOLD;
                gameHelper.holdemGame.players.get(currentUserId).riseValue = 0;
                gameHelper.holdemGame.players.get(currentUserId).lastCommand = command.type;

            }
            if (command.type.equals(CommandType.RISE) || command.type.equals(CommandType.BET)) {
                gameHelper.holdemGame.players.get(currentUserId).cash -= (command.riseValue - command.lastBet);
                gameHelper.holdemGame.players.get(currentUserId).betInRound += (command.riseValue - command.lastBet);
                gameHelper.holdemGame.players.get(currentUserId).riseValue = command.riseValue;
                gameHelper.holdemGame.pot += (command.riseValue - command.lastBet);
                gameHelper.holdemGame.callValue = command.riseValue;
                gameHelper.decisionIterator = gameHelper.playingPlayerNumber();
                gameHelper.holdemGame.players.get(currentUserId).lastCommand = command.type;
                if (gameHelper.holdemGame.players.get(currentUserId).cash == 0) {
                    gameHelper.holdemGame.players.get(currentUserId).lastCommand = CommandType.ALL_IN;
                }
            }
            if (command.type.equals(CommandType.CALL)) {
                gameHelper.holdemGame.players.get(currentUserId).cash -= (gameHelper.holdemGame.callValue - command.lastBet);
                gameHelper.holdemGame.players.get(currentUserId).betInRound += (gameHelper.holdemGame.callValue - command.lastBet);
                gameHelper.holdemGame.pot += (gameHelper.holdemGame.callValue - command.lastBet);
                gameHelper.holdemGame.players.get(currentUserId).riseValue = 0;
                gameHelper.holdemGame.players.get(currentUserId).lastCommand = gameHelper.holdemGame.callValue == 0 ? CommandType.CHECK : CommandType.CALL;
                if (gameHelper.holdemGame.players.get(currentUserId).cash == 0) {
                    gameHelper.holdemGame.players.get(currentUserId).lastCommand = CommandType.ALL_IN;
                }
            }
            gameHelper.decisionIterator--;
            sendOrNotDecision(user);
        }

    }

    //client

    protected ClientDiscoveryAdapter clientDiscoveryAdapter = new ClientDiscoveryAdapter() {

        @Override
        public void onServerLost(ServerUser user) {
            finish();
        }

        @Override
        public void onServerMessage(PokerCommand command) {
            if (command.type.equals(CommandType.GAME_SETUP)) {
                settings.deckTmp().put(command.deckValue);
                firstHandCardRevers.setImageLevel(PokerApp_.getInstance().getDeck());
                secondHandCardRevers.setImageLevel(PokerApp_.getInstance().getDeck());
                gameHelper.playerId = command.playerId;
                gameHelper.holdemGame = command.game;
                statusBarUpdatePlayersNumber();
                updateStatusBarBlindAndDeal();
                updatePlayerState();
                updateStatusBarCash();
                updateGame();
            }
            if (command.type.equals(CommandType.GAME_DATA)) {
                gameHelper.holdemGame = command.game;
                updateGame();
            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        QuitDialogFragment_.builder().requestCode(QUIT_REQUEST).build().show(getFragmentManager(), "quit");
    }

    @Override
    public void finish() {
        if (serverSession.isEnabled()) {
            serverSession.close();
        }
        clientSession.close();
        super.finish();
    }

    @Override
    public void onQuit(int requestCode, boolean tickOption) {

        if (requestCode == QUIT_REQUEST) {
            finish();
            updateMoneyAfterGame();
        }
        if (requestCode == HIDE_BLEND_REQUEST) {
            incrementGameState(1);
            holdemBlendView.setVisibility(View.GONE);
            gameHelper.tutorial = true;
            settings.showBlend().put(!tickOption);
            updateGame();
        }
    }

    @Override
    public void onClose() {
        if (gameHelper.isGameOver()) {
            updateMoneyAfterGame();
            if (gameHelper.holdemGame.players.get(gameHelper.playerId).cash > 0) {
                GameOverActivity_.intent(this).won(true).start();
            } else {
                GameOverActivity_.intent(this).won(false).start();
            }
        }
        if (host) {
            List<Integer> gameOverIds = gameHelper.gameOverPlayersIds();
//            if(gameOverIds.size() > 0) {
//                for (Integer id : gameOverIds) {
//                    if()
//                }
//            }
            clearGameViewForRoundStart();
            prepareNextRound();

        } else {
            clearGameViewForRoundStart();
            updateGame();
        }
    }

    @Click(R.id.disconnected_continue_button)
    protected void disconnectPlayerClick() {
        disconnectContainer.setVisibility(View.GONE);
        serverSession.forgetInactiveUsers();
        updatePlayersList(true);
        gameHelper.playersGameReady = 0;
        serverGetReady(null);
    }

    private void updatePlayersList(boolean withHost) {
        User hostUser = gameHelper.holdemGame.players.get(0);
        gameHelper.holdemGame.players = new ArrayList<>();
        gameHelper.holdemGame.players.addAll(serverSession.getAcceptedUsers());
        if (withHost) {
            gameHelper.holdemGame.players.add(0, hostUser);
        }
    }

    private void checkAllConnectedAfterInterrups() {
        if (disconnectContainer.getVisibility() == View.GONE) {
            return;
        }
        if (serverSession.getDisconnectedUsers().size() == 0) {
            disconnectContainer.setVisibility(View.GONE);
        }
    }

    private void updateMoneyAfterGame() {
        int cash = settings.cash().get();
        cash += gameHelper.holdemGame.players.get(gameHelper.playerId).cash - gameHelper.holdemGame.entryFee;
        settings.cash().put(cash);
    }
}

