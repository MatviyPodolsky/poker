package com.kubatatami.poker.adapters;

import com.kubatatami.poker.Listener.ClientDiscoveryListener;
import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.ServerUser;

/**
 * @author skad
 * @date 09/03/16
 */
public class ClientDiscoveryAdapter implements ClientDiscoveryListener {

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onServerFound(ServerUser user) {

    }

    @Override
    public void onServerLost(ServerUser user) {

    }

    @Override
    public void onServerMessage(PokerCommand command) {

    }
}