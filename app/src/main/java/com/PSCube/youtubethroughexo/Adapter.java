package com.PSCube.youtubethroughexo;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Context ctx;
    ArrayList<File> fileArrayList;
    OnVideoClickListner onVideoClickListner;

    public Adapter(Context ctx, ArrayList<File> fileArrayList, OnVideoClickListner onVideoClickListner) {
        this.ctx = ctx;
        this.fileArrayList = fileArrayList;
        this.onVideoClickListner = onVideoClickListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_download, parent, false);
        return new ViewHolder(view, onVideoClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = Uri.fromFile(fileArrayList.get(position));

        Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        holder.videoImage.setImageBitmap(bitmap2);
        String fullname = fileArrayList.get(position).getName();
        String title = fullname.substring(0,
                fileArrayList.get(position).getName().length() - 4);
        holder.titleVideo.setText(title);

        holder.deleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 1/28/21 delete video here
            }
        });


    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoImage, deleteVideo;
        TextView titleVideo;
        OnVideoClickListner onVideoClickListner;

        public ViewHolder(@NonNull View itemView, OnVideoClickListner onVideoClickListner) {
            super(itemView);
            videoImage = itemView.findViewById(R.id.videoImage);
            deleteVideo = itemView.findViewById(R.id.deleteVideo);
            titleVideo = itemView.findViewById(R.id.titleVideo);
            this.onVideoClickListner = onVideoClickListner;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVideoClickListner.onVideoClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface OnVideoClickListner {
        void onVideoClicked(int position);
    }

}
