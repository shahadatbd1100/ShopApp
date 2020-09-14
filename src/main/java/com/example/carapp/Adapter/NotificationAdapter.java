package com.example.carapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carapp.HelperClass.NotificationModel;
import com.example.carapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    Context context;
    private List<NotificationModel> notificationModelList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String image = notificationModelList.get(position).getImage();
        String body = notificationModelList.get(position).getBody();
        boolean readed = notificationModelList.get(position).isReaded();

        holder.setData(image,body,readed);

    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView textView;
        private ConstraintLayout notificationLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view_notification);
            textView = itemView.findViewById(R.id.text_view_notification);
            notificationLayout = itemView.findViewById(R.id.notification_layout);
        }

        public void setData(String image,String body,boolean readed){
            Glide.with(itemView.getContext()).load(image).into(imageView);

            if (readed){
                textView.setAlpha(0.5f);
                notificationLayout.setBackgroundColor(itemView.getResources().getColor(R.color.white));
            }else {
                textView.setAlpha(1f);
                notificationLayout.setBackgroundColor(itemView.getResources().getColor(R.color.recycler_view_background2));
            }
            textView.setText(body);
        }
    }
}
