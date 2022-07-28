package com.imad.simpledalleclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageAsyncTaskLoader extends AsyncTaskLoader {

    private String prompt;

    public ImageAsyncTaskLoader(@NonNull Context context, String query) {
        super(context);
        this.prompt = query;
    }

    @Nullable
    @Override
    public ArrayList<String> loadInBackground() {
        ArrayList<String> ImageStrings = new ArrayList<>();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"prompt\":\""+this.prompt+"\"}");
        Request request = new Request.Builder()
                .url("https://bf.dallemini.ai/generate")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() == null){
                Log.d("RESPONSE RECEIVED", "Empty Response Received :(");
                return ImageStrings;
            }
            Log.d("RESPONSE RECEIVED", String.valueOf(response.body().charStream()));
            Gson gson = new Gson();
            APIResponse apiResponse = gson.fromJson(response.body().string(), APIResponse.class);
            List<String> images = apiResponse.getImages();
            Log.println(Log.INFO, "ImageASyncTaskLoader", images.toString());
            int nimages = images.size();
            for(int i = 0; i < nimages; i++) {
                ImageStrings.add(images.get(i).replaceAll("\n",""));
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("REQUEST FAILED","Could Not Complete request");
        }
        SystemClock.sleep(2000);
        return ImageStrings;
    }
}
