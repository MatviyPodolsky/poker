package com.kubatatami.poker.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kuba on 30/09/14.
 */
public class ServerUser extends User {

    public Collection<User> userList;
    public GameType gameType;
    public int entryFee;
    public boolean gameActive;

    public ServerUser() {
    }

    public ServerUser(User user) {
        this.id = user.id;
        this.name = user.name;
        this.image=user.image;
    }


}
