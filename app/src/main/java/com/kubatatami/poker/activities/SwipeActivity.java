package com.kubatatami.poker.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.kubatatami.judonetworking.observers.ObserverAdapter;
import com.kubatatami.poker.Listener.ServerListener;
import com.kubatatami.poker.PokerApp_;
import com.kubatatami.poker.R;
import com.kubatatami.poker.adapters.ClientDiscoveryAdapter;
import com.kubatatami.poker.connection.ClientSession;
import com.kubatatami.poker.connection.ServerSession;
import com.kubatatami.poker.dialogs.QuitDialogFragment;
import com.kubatatami.poker.dialogs.QuitDialogFragment_;
import com.kubatatami.poker.model.Card;
import com.kubatatami.poker.model.CommandType;
import com.kubatatami.poker.model.HoldemGameState;
import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.ServerUser;
import com.kubatatami.poker.model.User;
import com.kubatatami.poker.model.UserRoundState;
import com.kubatatami.poker.ui.PaperStackLayout;
import com.kubatatami.poker.utils.GameHelper;
import com.kubatatami.poker.utils.ShakeListener;
import com.kubatatami.poker.utils.TableHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_swipe)
public class SwipeActivity extends BaseActivity implements ServerListener, QuitDialogFragment.OnQuitListener {

    @ViewById(R.id.card_stack)
    protected PaperStackLayout cardStack;
    @Bean
    protected ServerSession serverSession;
    @Bean
    protected ClientSession clientSession;
    @Bean
    protected GameHelper gameHelper;
    @Extra
    protected Boolean host = false;

    private ShakeListener shaker;


    @AfterViews
    protected void afterViews() {
        serverSession.setServerListener(this);
        clientSession.setClientDiscoveryListener(clientDiscoveryAdapter);
        if (host && !gameHelper.holdemGame.gameState.equals(HoldemGameState.HOLE_CARDS) && !gameHelper.holdemGame.gameState.equals(HoldemGameState.CLEAR)) {
            gameHelper.holdemGame.callValue = 0;
        }
        gameHelper.lastBet = 0;
        cardStack.setSlideMode(PaperStackLayout.SlideMode.TOP_ONLY);
        cardStack.setListener(new PaperStackLayout.SwipeListenerAdapter() {
            @Override
            public void onSwipeTop(int position) {
                gameHelper.deckPosition = position;
                if (host) {
                    giveOutCard(gameHelper.cardAdapter.getItem(position));
                } else {
                    proxyCardToServer(gameHelper.cardAdapter.getItem(position));
                }
                gameHelper.swiped = true;
                soundManager.playRandomSound(R.raw.slide_01, R.raw.slide_02, R.raw.slide_03, R.raw.slide_04);
                if (gameHelper.giveOutIterator == gameHelper.holdemGame.players.size() * 2 || gameHelper.giveOutIterator == (gameHelper.holdemGame.players.size() * 2 + 3)
                        || gameHelper.giveOutIterator == (gameHelper.holdemGame.players.size() * 2 + 4)
                        || gameHelper.giveOutIterator == (gameHelper.holdemGame.players.size() * 2 + 5)) {
                    if (gameHelper.giveOutIterator != gameHelper.holdemGame.players.size() * 2) {
                        gameHelper.holdemGame.callValue = 0;
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });
        if (gameHelper.cardAdapter == null) {
            gameHelper.cards = new ArrayList<Card>();
            gameHelper.deckPosition = 0;
            gameHelper.giveOutIterator = 0;
            gameHelper.cards.addAll(TableHelper.randomTable());

            gameHelper.cardAdapter = new ObserverAdapter<Card>(this, gameHelper.cards, R.layout.item_card) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View itemView = super.getView(position, convertView, parent);
                    ((ImageView) itemView.findViewById(R.id.card_image)).setImageLevel(PokerApp_.getInstance().getDeck());
                    return itemView;
                }
            };
        }
        cardStack.setAdapter(gameHelper.cardAdapter);
        cardStack.goToItem(gameHelper.deckPosition + 1);

        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        shaker = new ShakeListener(this);
        shaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                if (!gameHelper.swiped) {
                    soundManager.playSound(R.raw.shuffle_3);
                    vibe.vibrate(100);
                    cardStack.shuffle(true);
                }
            }
        });
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
        gameHelper.holdemGame.gameStateIncrement++;
        serverSession.sendAllAccept(new PokerCommand(gameHelper.holdemGame));
        gameHelper.giveOutIterator++;

    }

    private void proxyCardToServer(Card card) {
        clientSession.sendToServer(new PokerCommand(card, CommandType.DEAL_CARD_PROXY));
        gameHelper.giveOutIterator++;
    }

    //server
    @Override
    public void onServerStart() {

    }

    @Override
    public void onServerError() {

    }

    @Override
    public void onClientConnect(User user) {

    }

    @Override
    public void onClientDisconnect(User user) {

    }

    @Override
    public void onServerClose() {

    }

    @Override
    public void onMessage(User user, PokerCommand command) {

    }

    //client
    protected ClientDiscoveryAdapter clientDiscoveryAdapter = new ClientDiscoveryAdapter() {

        @Override
        public void onServerLost(ServerUser user) {
            finish();
        }

        @Override
        public void onServerMessage(PokerCommand command) {
            if (command.type.equals(CommandType.GAME_DATA)) {
                gameHelper.holdemGame = command.game;
            }
        }
    };

    @Override
    public void onResume() {
        shaker.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        shaker.pause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        QuitDialogFragment_.builder().build().show(getFragmentManager(), "quit");
    }

    @Override
    public void onQuit(int requestCode, boolean tickOption) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override

    public void finish(){
        gameHelper.cardAdapter = null;
        clientSession.stop();
        serverSession.close();
        serverSession.setServerListener(null);
        super.finish();
    }
}
