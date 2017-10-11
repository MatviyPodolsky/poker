package com.kubatatami.poker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.kubatatami.poker.R;
import com.kubatatami.poker.adapters.AvatarAdapter;
import com.kubatatami.poker.data.Settings_;
import com.kubatatami.poker.utils.AvatarUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kuba on 28/09/14.
 */
@EActivity(R.layout.activity_choose_avatar)
public class ChooseAvatarActivity extends BaseActivity {

    protected final static int SELECT_PICTURE = 10002;

    @Pref
    protected Settings_ settings;

    protected AvatarAdapter adapter;

    @ViewById(R.id.avatars_grid_view)
    GridView avatarsGridView;


    @AfterViews
    protected void start() {
        adapter = new AvatarAdapter(this);
        avatarsGridView.setAdapter(adapter);
        adapter.addAll(getAssetsName());
        animateBackground(R.drawable.join_game_bgr, findViewById(R.id.main_background));
        avatarsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView image =  (ImageView)parent.getChildAt(position);
                Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                try {
                    settings.image().put(AvatarUtils.createThumbnail(bitmap));
                    ChooseAvatarActivity.this.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private List<String> getAssetsName(){
        List<String> assets = null;
        try {
            assets = new ArrayList<String>(Arrays.asList(getAssets().list("avatar_assets")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assets;
    }




    @Background
    protected void saveThumb(Intent data){
        try {
            String imageBase64;
            Uri selectedImage = data.getData();
            if(selectedImage!=null) {
                imageBase64 = AvatarUtils.createThumbnail(this, selectedImage);
            }else{
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                imageBase64 = AvatarUtils.createThumbnail(imageBitmap);
            }
            settings.image().put(imageBase64);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnActivityResult(SELECT_PICTURE)
    protected void onPictureResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            saveThumb(data);
            finish();
        }
    }


    @Click(R.id.settings_take_photo)
    protected void takeAPhoto(){
        pickPhoto();
    }



    protected void pickPhoto() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.select_or_take_picture));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }



}
