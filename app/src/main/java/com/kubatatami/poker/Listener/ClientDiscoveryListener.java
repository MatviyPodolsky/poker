package com.kubatatami.poker.Listener;

import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.ServerUser;

/**
 * @author skad
 * @date 09/03/16
 */
public interface ClientDiscoveryListener {

    public void onStart();

    public void onStop();

    public void onServerFound(ServerUser user);

    public void onServerLost(ServerUser user);

    public void onServerMessage(PokerCommand command);

}