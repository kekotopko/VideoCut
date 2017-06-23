package com.example.tm.videocut;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jlab13 on 21.06.2017.
 */

public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        CardView cv;
        CardView cv2;
        ImageView videoView;
        TextView videoName;
        ImageView videoView1;
        TextView videoName1;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.card_view);
            cv2=  (CardView)v.findViewById(R.id.cardView2);
            videoView = v.findViewById(R.id.videoViewList);
            videoName = v.findViewById(R.id.videoNameTextView);
            videoView1 = v.findViewById(R.id.videoViewList2);
            videoName1 = v.findViewById(R.id.VideoNametextView2);
        }
    }

    List<String[]> allvideos;

    ChooseAdapter(List<String> videos){
        allvideos = new ArrayList<>();
        for (int i = 0; i < videos.size(); i++) {
           String[] line = new String[2];
            line[0]=videos.get(i);
            if (i+1 < videos.size()){
                line[1] = videos.get(i+1);
                i++;
            }
            allvideos.add(line);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_adapetr, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            File file = new File(allvideos.get(position)[0]);
            holder.videoName.setText(file.getName());
            final String uri = allvideos.get(position)[0];

                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(uri,
                MediaStore.Images.Thumbnails.MINI_KIND);
            holder.videoView.setImageBitmap(thumb);
            //holder.videoView.seekTo(100);
            holder.cv.setOnClickListener(new View.OnClickListener(){
                @Override
                 public void onClick(View v){
                    Intent intent = new Intent(v.getContext(), Cut.class);
                    intent.putExtra("uri",uri);
                    v.getContext().startActivity(intent);
                 }});

        if (allvideos.get(position)[1] != null){
            File file2 = new File(allvideos.get(position)[1]);
            holder.videoName1.setText(file2.getName());
            final String uri1 = allvideos.get(position)[1];
            Bitmap thumb1 = ThumbnailUtils.createVideoThumbnail(uri1,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            holder.videoView1.setImageBitmap(thumb1);
           // holder.videoView1.seekTo(100);
            holder.cv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(v.getContext(), Cut.class);
                    intent.putExtra("uri",uri1);
                    v.getContext().startActivity(intent);
                }});
        } else {
            holder.cv2.removeAllViews();
        }



    }

    @Override
    public int getItemCount() {
        return allvideos.size();
    }
}
