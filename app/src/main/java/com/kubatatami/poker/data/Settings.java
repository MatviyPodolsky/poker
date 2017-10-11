package com.kubatatami.poker.data;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Kuba on 28/09/14.
 */
@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface Settings {
    @DefaultString("")
    public String name();

    @DefaultString("")
    public String image();

    @DefaultInt(10000)
    public int cash();

    @DefaultInt(1)
    public int deck();

    @DefaultInt(1)
    public int deckTmp();

    @DefaultInt(1)
    public int smallBlind();

    @DefaultInt(100)
    public int entryFee();

    @DefaultBoolean(true)
    public boolean sound();

    @DefaultBoolean(true)
    public boolean showBlend();
}
