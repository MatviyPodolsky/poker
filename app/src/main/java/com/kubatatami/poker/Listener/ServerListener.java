package com.kubatatami.poker.Listener;

import com.kubatatami.poker.model.PokerCommand;
import com.kubatatami.poker.model.User;

/**
 * @author skad
 * @date 09/03/16
 */
public interface ServerListener {

    public void onServerStart();

    public void onServerError();

    public void onClientConnect(User user);

    public void onClientDisconnect(User user);

    public void onServerClose();

    public void onMessage(User user, PokerCommand command);

}

