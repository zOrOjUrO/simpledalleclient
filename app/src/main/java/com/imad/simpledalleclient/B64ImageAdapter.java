package com.imad.simpledalleclient;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class B64ImageAdapter extends RecyclerView.Adapter<B64ImageAdapter.ViewHolder>
{
    ArrayList<Bitmap> Images= new ArrayList<>();
    String query;

    public B64ImageAdapter(ArrayList<String> B64Images, String prompt)
    {
        this.query = prompt;
        for(String B64Image: B64Images) {
            byte[] decodedString = Base64.decode(B64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            this.Images.add(decodedByte);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position)
    {
        viewHolder.getImageView().setImageBitmap(Images.get(position));
    }


    @Override
    public int getItemCount()
    {
        return Images.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageButton imageButton;
        ImageView imageView;
        //private B64ImageAdapter imageAdapter;
        //private int counter = 0;
        private boolean saveBitmapAsFile(Bitmap bmp, String filename){
            try{
                ContextWrapper cw = new ContextWrapper(imageView.getContext());
                String path = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
                OutputStream fOut = null;
                //counter += 1;
                File file = new File(path, filename+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                fOut = new FileOutputStream(file);

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 95% compression rate
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

                MediaStore.Images.Media.insertImage(imageView.getContext().getContentResolver(), file.getAbsolutePath(),file.getName(),file.getName());
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageButton=itemView.findViewById(R.id.downImageBtn);
            imageView=itemView.findViewById(R.id.imageView);
            imageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //imageAdapter.Images.remove(getAdapterPosition());
                    //imageAdapter.notifyItemRemoved(getAdapterPosition());
                    Snackbar.make(v.getRootView().getRootView(), "Saving Image...", Snackbar.LENGTH_LONG)
                            .setAction("Cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //TODO: implement cancel save
                                }
                            })
                            .show();
                    if(!saveBitmapAsFile(((BitmapDrawable)imageView.getDrawable()).getBitmap(), query))
                        Snackbar.make(v.getRootView(), "Save Failed", Snackbar.LENGTH_SHORT).show();
                    else Snackbar.make(v.getRootView(), "Saved Locally", Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        public ImageView getImageView(){ return imageView;}

    }

}
