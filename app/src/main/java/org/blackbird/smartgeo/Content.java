package org.blackbird.smartgeo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

/**
 * Created by Maurilio Atila on 13/10/15.
 */

public class Content {
    private static final String TAG = "SMARTGEO";

    private String description, label, tag_list, picture;
    private Double latitude, longitude;
    private int user_id;

    public void setDescription(String description) { this.description = description;}
    public void setLabel(String label) {this.label = label;}

    public void setUserId(int user_id){
        this.user_id=user_id;
    }

    public void setTagList(String tag_list){ this.tag_list = tag_list;}

    public void setPicture(String capturedImageURL) {
        Bitmap bm = BitmapFactory.decodeFile(capturedImageURL);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        this.picture = "data:image/png;base64," + Base64.encodeToString(b, Base64.NO_WRAP);
        Log.e(TAG, "picture: " + picture);
        label=picture;
    }

    public void setLatLon(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String json(){
        String contentJSONString = new Gson().toJson(this);
        Log.d("Gson", "user JSON String: " + contentJSONString);
        return contentJSONString;
    }



}
