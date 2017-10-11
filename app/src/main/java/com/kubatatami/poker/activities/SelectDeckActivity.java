package com.kubatatami.poker.activities;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.kubatatami.poker.R;
import com.kubatatami.poker.data.Settings_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_select_deck)
public class SelectDeckActivity extends BaseActivity {


    protected void selectDeck(int deck) {
        Intent data = new Intent();
        data.putExtra("deckView", deck);
        setResult(RESULT_OK, data);
        finish();
    }

    @AfterViews

    protected void start() {
        setAnimationButton(findViewById(R.id.select_deck_1), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDeck(1);
            }
        });

        setAnimationButton(findViewById(R.id.select_deck_2), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDeck(2);
            }
        });

        setAnimationButton(findViewById(R.id.select_deck_3), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDeck(3);
            }
        });
        animateBackground(R.drawable.settings_bgr, findViewById(R.id.main_background));
    }


}
