package com.imad.simpledalleclient;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>>,
        Loader.OnLoadCanceledListener<ArrayList<String>>  {

    private static final String LOG_TAG = "IMAGE FETCHER";
    private ProgressBar progressBar;
    private RecyclerView imageList;
    private static final int LOADER_ID = 10000;
    private static final String KEY_PARAM = "dog";
    private LoaderManager loaderManager;
    private String prompt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        this.prompt = getIntent().getStringExtra("Query");
        Log.d("Prompt", prompt);

        this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        this.imageList = (RecyclerView) this.findViewById(R.id.images);
        this.progressBar.setVisibility(View.GONE);
        this.loaderManager = LoaderManager.getInstance(this);

        Log.i(LOG_TAG, "load images");
        LoaderManager.LoaderCallbacks<ArrayList<String>> loaderCallbacks = this;
        // Arguments:
        Bundle args = new Bundle();
        args.putString(KEY_PARAM, prompt);

        Loader<ArrayList<String>> loader = this.loaderManager.initLoader(LOADER_ID, args, loaderCallbacks);
        try {
            loader.registerOnLoadCanceledListener(this); // Loader.OnLoadCanceledListener
        } catch(IllegalStateException e) {
            // There is already a listener registered
        }
        loader.forceLoad(); // Start Loading..
    }

    @NonNull
    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader");

        this.progressBar.setVisibility(View.VISIBLE); // To show
        if(id == LOADER_ID) {
            String param1 = (String) args.get(KEY_PARAM);
            this.progressBar.setProgress(50);
            return new ImageAsyncTaskLoader(ResultActivity.this, param1);
        }
        throw new RuntimeException(".[..].");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<String>> loader, ArrayList<String> data) {
        Log.i(LOG_TAG, "onLoadFinished");
        Log.i(LOG_TAG, data.toString());

        if(loader.getId() == LOADER_ID) {
            // Destroy a Loader by ID.
            this.loaderManager.destroyLoader(loader.getId());
            this.progressBar.setProgress(100);
            this.progressBar.setVisibility(View.GONE);
            if(data.size() == 0){
                Toast.makeText(loader.getContext(), "Failed to Complete Request", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            B64ImageAdapter imageAdapter = new B64ImageAdapter(data, this.prompt);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            this.imageList.setLayoutManager(layoutManager);
            imageList.setAdapter(imageAdapter);
            findViewById(R.id.loadAnim).setVisibility(View.GONE);
            imageList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String>> loader) {
            Log.i(LOG_TAG, "onLoaderReset");
        }

    @Override
    public void onLoadCanceled(@NonNull Loader<ArrayList<String>> loader) {
        Log.i(LOG_TAG, "onLoadCanceled");

        if(loader.getId() == LOADER_ID) {
            // Destroy a Loader by ID.
            this.loaderManager.destroyLoader(loader.getId());
            this.progressBar.setVisibility(View.GONE); // To hide
        }
    }
}
