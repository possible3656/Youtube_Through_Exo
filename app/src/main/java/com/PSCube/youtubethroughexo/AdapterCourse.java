package com.PSCube.youtubethroughexo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class AdapterCourse extends RecyclerView.Adapter<AdapterCourse.ViewHolder> {
    Context ctx;
    ArrayList<File> fileArrayList;
    OnCourseClickListner onVideoClickListner;

    public AdapterCourse(Context ctx, ArrayList<File> fileArrayList, OnCourseClickListner onVideoClickListner) {
        this.ctx = ctx;
        this.fileArrayList = fileArrayList;
        this.onVideoClickListner = onVideoClickListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_course_subject, parent, false);
        return new ViewHolder(view, onVideoClickListner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.titleVideo.setText(fileArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleVideo;
        OnCourseClickListner onVideoClickListner;

        public ViewHolder(@NonNull View itemView, OnCourseClickListner onVideoClickListner) {
            super(itemView);

            titleVideo = itemView.findViewById(R.id.titleCourse);
            this.onVideoClickListner = onVideoClickListner;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onVideoClickListner.onCourseClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface OnCourseClickListner {
        void onCourseClicked(int position);
    }

}
