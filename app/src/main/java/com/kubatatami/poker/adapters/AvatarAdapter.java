package com.kubatatami.poker.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kubatatami.judonetworking.observers.HolderView;
import com.github.kubatatami.judonetworking.observers.ObserverAdapter;
import com.kubatatami.poker.R;
import com.kubatatami.poker.activities.ChooseAvatarActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by jstasinski on 13/10/14.
 */
public class AvatarAdapter extends ObserverAdapter<String> {


    public AvatarAdapter(ChooseAvatarActivity activity) {
        super(activity, R.layout.item_avatar);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent, Holder.class);
        final Holder holder = (Holder) itemView.getTag();
        final String url = getItem(position);

        ImageLoader.getInstance().displayImage("assets://avatar_assets/" + url, holder.avatarImage);

        return itemView;
    }

    public static class Holder {
        @HolderView(R.id.avatar_item_image)
        ImageView avatarImage;


    }
}
