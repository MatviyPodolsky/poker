package com.kubatatami.poker.dialogs;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.kubatatami.poker.R;
import com.kubatatami.poker.data.Settings;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.model.UserRoundState;
import com.kubatatami.poker.ui.HardwareAccelerationListener;
import com.kubatatami.poker.utils.AvatarUtils;
import com.kubatatami.poker.utils.GameHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 25/11/14.
 */
@EFragment(R.layout.fragment_holdem_result_dialog)
public class HoldemGameResultFragment extends DialogFragment {


    protected AnimatorSet backgroundAnimatorSet;

    @ViewById(R.id.main_background)
    protected ImageView background;
    @ViewById(R.id.result_1_player_container)
    protected View player1Container;
    @ViewById(R.id.result_1_player_hands)
    protected View player1HandsContainer;
    @ViewById(R.id.result_1_player_avatar)
    protected ImageView player1avatar;
    @ViewById(R.id.result_1_player_result)
    protected TextView player1result;
    @ViewById(R.id.result_1_player_result_cash)
    protected TextView player1cash;
    @ViewById(R.id.result_1_player_1_card)
    protected ImageView player1card1;
    @ViewById(R.id.result_1_player_2_card)
    protected ImageView player1card2;

    @ViewById(R.id.result_2_player_container)
    protected View player2Container;
    @ViewById(R.id.result_2_player_hands)
    protected View player2HandsContainer;
    @ViewById(R.id.result_2_player_avatar)
    protected ImageView player2avatar;
    @ViewById(R.id.result_2_player_result)
    protected TextView player2result;
    @ViewById(R.id.result_2_player_result_cash)
    protected TextView player2cash;
    @ViewById(R.id.result_2_player_1_card)
    protected ImageView player2card1;
    @ViewById(R.id.result_2_player_2_card)
    protected ImageView player2card2;

    @ViewById(R.id.result_3_player_container)
    protected View player3Container;
    @ViewById(R.id.result_3_player_hands)
    protected View player3HandsContainer;
    @ViewById(R.id.result_3_player_avatar)
    protected ImageView player3avatar;
    @ViewById(R.id.result_3_player_result)
    protected TextView player3result;
    @ViewById(R.id.result_3_player_result_cash)
    protected TextView player3cash;
    @ViewById(R.id.result_3_player_1_card)
    protected ImageView player3card1;
    @ViewById(R.id.result_3_player_2_card)
    protected ImageView player3card2;

    @ViewById(R.id.result_4_player_container)
    protected View player4Container;
    @ViewById(R.id.result_4_player_hands)
    protected View player4HandsContainer;
    @ViewById(R.id.result_4_player_avatar)
    protected ImageView player4avatar;
    @ViewById(R.id.result_4_player_result)
    protected TextView player4result;
    @ViewById(R.id.result_4_player_result_cash)
    protected TextView player4cash;
    @ViewById(R.id.result_4_player_1_card)
    protected ImageView player4card1;
    @ViewById(R.id.result_4_player_2_card)
    protected ImageView player4card2;


    @ViewById(R.id.result_table_container)
    protected View tableCardsContainer;

    @ViewById(R.id.result_table_1_card)
    protected ImageView table1card;
    @ViewById(R.id.result_table_2_card)
    protected ImageView table2card;
    @ViewById(R.id.result_table_3_card)
    protected ImageView table3card;
    @ViewById(R.id.result_table_4_card)
    protected ImageView table4card;
    @ViewById(R.id.result_table_5_card)
    protected ImageView table5card;


    @ViewById(R.id.result_state_value)
    protected TextView stateView;

    @ViewById(R.id.result_fragment_title)
    protected TextView title;

    @Bean
    protected GameHelper gameHelper;
    @Pref
    protected Settings_ settings;

    protected Boolean foldGame;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    @AfterViews
    protected void start() {
        animateBackground(R.drawable.settings_bgr, background);
        foldGame = gameHelper.holdemGame.foldGame;
        updatePlayersNumber();
        setFoldOnCards();
        setPlayerState();
        setSelfTextColor();
        setAllPlayerCash();
        if (!foldGame) {
            tableCardsContainer.setVisibility(View.VISIBLE);
            updateTableCards();
            if (gameHelper.holdemGame.winnersId.indexOf(gameHelper.playerId) != -1) {
                title.setText(getString(R.string.you_won_the_hand));
                stateView.setText(getString(R.string.won_, (gameHelper.holdemGame.pot / gameHelper.holdemGame.winnersId.size()) + ""));
                stateView.setTextColor(getResources().getColor(R.color.green));
            } else {
                title.setText(getString(R.string.you_lost_the_hand));
                stateView.setText(getString(R.string.lost_, gameHelper.holdemGame.players.get(gameHelper.playerId).betInRound + ""));
                stateView.setTextColor(getResources().getColor(R.color.red));

            }

        } else {
            tableCardsContainer.setVisibility(View.INVISIBLE);
            if (gameHelper.holdemGame.winnersId.indexOf(gameHelper.playerId) != -1) {
                title.setText(getString(R.string.you_won));
                stateView.setText(getString(R.string.won_, (gameHelper.holdemGame.pot / gameHelper.holdemGame.winnersId.size()) + ""));
                stateView.setTextColor(getResources().getColor(R.color.green));

            } else {
                title.setText(getString(R.string.you_lost));
                stateView.setText(getString(R.string.lost_, gameHelper.holdemGame.players.get(gameHelper.playerId).betInRound + ""));
                stateView.setTextColor(getResources().getColor(R.color.red));
            }
        }
        p1containerClick();
    }

    private void setSelfTextColor() {
        switch (gameHelper.playerId) {
            case 0:
                player1result.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case 1:
                player2result.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case 2:
                player3result.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case 3:
                player4result.setTextColor(getResources().getColor(R.color.yellow));
                break;
        }
    }

    private void setAllPlayerCash() {
        for (int i = 0; i < gameHelper.holdemGame.players.size(); i++) {
            setPlayersCash(i);

        }
    }

    private void setPlayersCash(int playerId) {
        boolean won = gameHelper.holdemGame.winnersId.indexOf(playerId) != -1;
        String text = "";
        if (won) {
            text = getString(R.string.cash_, (gameHelper.holdemGame.pot / gameHelper.holdemGame.winnersId.size()) + "");
        } else {
            text = "-" + getString(R.string.cash_, gameHelper.holdemGame.players.get(playerId).betInRound + "");
        }
        switch (playerId) {
            case 0:
                player1cash.setText(text);
                player1cash.setBackgroundResource(won ? R.drawable.rounded_green_bgr : R.drawable.rounded_red_bgr);
                break;
            case 1:
                player2cash.setText(text);
                player2cash.setBackgroundResource(won ? R.drawable.rounded_green_bgr : R.drawable.rounded_red_bgr);
                break;
            case 2:
                player3cash.setText(text);
                player3cash.setBackgroundResource(won ? R.drawable.rounded_green_bgr : R.drawable.rounded_red_bgr);
                break;
            case 3:
                player4cash.setText(text);
                player4cash.setBackgroundResource(won ? R.drawable.rounded_green_bgr : R.drawable.rounded_red_bgr);
                break;
        }
    }


    private void updateTableCards() {
        table1card.setImageDrawable(gameHelper.holdemGame.tableCards.get(0).getDrawable(getActivity()));
        table2card.setImageDrawable(gameHelper.holdemGame.tableCards.get(1).getDrawable(getActivity()));
        table3card.setImageDrawable(gameHelper.holdemGame.tableCards.get(2).getDrawable(getActivity()));
        table4card.setImageDrawable(gameHelper.holdemGame.tableCards.get(3).getDrawable(getActivity()));
        table5card.setImageDrawable(gameHelper.holdemGame.tableCards.get(4).getDrawable(getActivity()));
    }

    private void setPlayerState() {

        player1result.setText(getStateForPlayerId(0));
        AvatarUtils.showThumb(player1avatar, gameHelper.holdemGame.players.get(0).image);
        AvatarUtils.showThumb(player2avatar, gameHelper.holdemGame.players.get(1).image);

        player2result.setText(getStateForPlayerId(1));
        if (gameHelper.holdemGame.players.size() > 2) {
            player3result.setText(getStateForPlayerId(2));
            AvatarUtils.showThumb(player3avatar, gameHelper.holdemGame.players.get(2).image);


        }
        if (gameHelper.holdemGame.players.size() > 3) {
            player4result.setText(getStateForPlayerId(3));
            AvatarUtils.showThumb(player4avatar, gameHelper.holdemGame.players.get(3).image);


        }
    }

    private String getStateForPlayerId(int playerId) {
        String retValue = gameHelper.holdemGame.players.get(playerId).name + "\n";
        if (gameHelper.holdemGame.players.get(playerId).roundState.equals(UserRoundState.FOLD)) {
            retValue += getString(R.string.fold);
        } else {
            if (foldGame) {
                retValue += getString(R.string.play);
            } else {
                retValue += gameHelper.holdemGame.players.get(playerId).combination;
                updateHandForPlayer(playerId);
            }
        }

        return retValue;
    }


    private void updateHandForPlayer(int playerId) {
        switch (playerId) {
            case 0:
                player1card1.setImageDrawable(gameHelper.holdemGame.players.get(playerId).cards.get(0).getDrawable(getActivity()));
                player1card2.setImageDrawable(gameHelper.holdemGame.players.get(playerId).cards.get(1).getDrawable(getActivity()));
                break;
            case 1:
                player2card1.setImageDrawable(gameHelper.holdemGame.players.get(playerId).cards.get(0).getDrawable(getActivity()));
                player2card2.setImageDrawable(gameHelper.holdemGame.players.get(playerId).cards.get(1).getDrawable(getActivity()));
                break;
            case 2:
                player3card1.setImageDrawable(gameHelper.holdemGame.players.get(playerId).cards.get(0).getDrawable(getActivity()));
                player3card2.setImageDrawable(gameHelper.holdemGame.players.get(playerId).cards.get(1).getDrawable(getActivity()));
                break;
            case 3:
                player4card1.setImageDrawable(gameHelper.holdemGame.players.get(playerId).cards.get(0).getDrawable(getActivity()));
                player4card2.setImageDrawable(gameHelper.holdemGame.players.get(playerId).cards.get(1).getDrawable(getActivity()));
                break;
        }
    }

    private void setFoldOnCards() {
        player1card1.setImageResource(R.drawable.card_back_level_list);
        player2card1.setImageResource(R.drawable.card_back_level_list);
        player3card1.setImageResource(R.drawable.card_back_level_list);
        player4card1.setImageResource(R.drawable.card_back_level_list);
        player1card2.setImageResource(R.drawable.card_back_level_list);
        player2card2.setImageResource(R.drawable.card_back_level_list);
        player3card2.setImageResource(R.drawable.card_back_level_list);
        player4card2.setImageResource(R.drawable.card_back_level_list);
        player1card1.setImageLevel(settings.deckTmp().get());
        player2card1.setImageLevel(settings.deckTmp().get());
        player3card1.setImageLevel(settings.deckTmp().get());
        player4card1.setImageLevel(settings.deckTmp().get());
        player1card2.setImageLevel(settings.deckTmp().get());
        player2card2.setImageLevel(settings.deckTmp().get());
        player3card2.setImageLevel(settings.deckTmp().get());
        player4card2.setImageLevel(settings.deckTmp().get());
    }

    private void updatePlayersNumber() {
        player1Container.setVisibility(View.VISIBLE);
        player1HandsContainer.setVisibility(View.INVISIBLE);
        player2Container.setVisibility(View.VISIBLE);
        player2HandsContainer.setVisibility(View.INVISIBLE);
        player3Container.setVisibility(View.VISIBLE);
        player3HandsContainer.setVisibility(View.INVISIBLE);
        player4Container.setVisibility(View.VISIBLE);
        player4HandsContainer.setVisibility(View.INVISIBLE);
        if (gameHelper.holdemGame.players.size() < 4) {
            player4Container.setVisibility(View.INVISIBLE);
            player4HandsContainer.setVisibility(View.INVISIBLE);
        }
        if (gameHelper.holdemGame.players.size() < 3) {
            player3Container.setVisibility(View.INVISIBLE);
            player3HandsContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void clearClick() {
        player1Container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        player1HandsContainer.setVisibility(View.INVISIBLE);
        player2Container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        player2HandsContainer.setVisibility(View.INVISIBLE);
        player3Container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        player3HandsContainer.setVisibility(View.INVISIBLE);
        player4Container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        player4HandsContainer.setVisibility(View.INVISIBLE);
    }


    @Click(R.id.result_close_button)
    protected void closeButtonClick() {
        ((ResultCloseListener) getActivity()).onClose();
        dismiss();
    }

    @Click(R.id.result_1_player_container)
    protected void p1containerClick() {
        clearClick();
        player1Container.setBackgroundResource(R.drawable.rounded_black_bgr);
        player1HandsContainer.setVisibility(View.VISIBLE);
    }

    @Click(R.id.result_2_player_container)
    protected void p2containerClick() {
        clearClick();
        player2Container.setBackgroundResource(R.drawable.rounded_black_bgr);
        player2HandsContainer.setVisibility(View.VISIBLE);
    }

    @Click(R.id.result_3_player_container)
    protected void p3containerClick() {
        clearClick();
        player3Container.setBackgroundResource(R.drawable.rounded_black_bgr);
        player3HandsContainer.setVisibility(View.VISIBLE);
    }

    @Click(R.id.result_4_player_container)
    protected void p4containerClick() {
        clearClick();
        player4Container.setBackgroundResource(R.drawable.rounded_black_bgr);
        player4HandsContainer.setVisibility(View.VISIBLE);
    }

    public interface ResultCloseListener {
        public void onClose();
    }

    protected void animateBackground(int drawable, View backgroundView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap background = BitmapFactory.decodeResource(backgroundView.getResources(),
                drawable, options);
        if (background.getConfig() != Bitmap.Config.RGB_565) {
            Bitmap newBitmap = background.copy(Bitmap.Config.RGB_565, background.isMutable());
            background.recycle();
            background = newBitmap;
            System.gc();
        }
        backgroundView.setBackgroundDrawable(new BitmapDrawable(backgroundView.getResources(), background));
        backgroundAnimatorSet = new AnimatorSet();
        ObjectAnimator rotateStart = ObjectAnimator.ofFloat(backgroundView, "rotation", 0, 4);
        ObjectAnimator scaleXStart = ObjectAnimator.ofFloat(backgroundView, "scaleX", 1.0f, 1.2f);
        ObjectAnimator scaleYStart = ObjectAnimator.ofFloat(backgroundView, "scaleY", 1.0f, 1.2f);
        rotateStart.setDuration(3000).setRepeatCount(ValueAnimator.INFINITE);
        rotateStart.setRepeatMode(ValueAnimator.REVERSE);
        scaleXStart.setDuration(3000).setRepeatCount(ValueAnimator.INFINITE);
        scaleXStart.setRepeatMode(ValueAnimator.REVERSE);
        scaleYStart.setDuration(3000).setRepeatCount(ValueAnimator.INFINITE);
        scaleYStart.setRepeatMode(ValueAnimator.REVERSE);
        backgroundAnimatorSet.setInterpolator(new LinearInterpolator());
        backgroundAnimatorSet.playTogether(
                rotateStart,
                scaleXStart,
                scaleYStart);
        backgroundAnimatorSet.addListener(new HardwareAccelerationListener(backgroundView));
        backgroundAnimatorSet.start();
    }


}
