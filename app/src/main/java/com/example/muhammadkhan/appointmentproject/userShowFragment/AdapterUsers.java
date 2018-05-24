package com.example.muhammadkhan.appointmentproject.userShowFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muhammadkhan.appointmentproject.R;
import com.example.muhammadkhan.appointmentproject.DataModel.Users;
import com.example.muhammadkhan.appointmentproject.activities.AppointmentBooking;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Muhammad Khan on 05/11/2017.
 */
public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyViewHolder> {

    private LayoutInflater inflater;
    Context context;
    List<Users> data=new ArrayList<Users>();

    public AdapterUsers(Context context) {
        this.context=context;
    }
    public void addData(List<Users> data) {
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.list_item,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Users user=data.get(position);
        String title=user.getName();
        holder.textView.setText(title);
        if(user.getImageUri().equals("no image")) {
        holder.imageView.setImageResource(R.drawable.profile);
        }
        else {
            Uri uri=Uri.parse(user.getImageUri());
            Picasso.with(context).load(uri).into(holder.imageView);
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    //---------------------------------inner class for items in recycler view---------------------------------------//
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textView;
        private CircleImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView= (TextView) itemView.findViewById(R.id.teacher_name);
            imageView=(CircleImageView) itemView.findViewById(R.id.profile_image);
        }
        //items click listener in Recycler view
        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            Users users=data.get(position);
            Intent intent=new Intent(context, AppointmentBooking.class);
            if(users.getKey().equals("teacher")){
                intent.putExtra("userName",users.getName().toString());
                intent.putExtra("imageUri",users.getImageUri());
                intent.putExtra("userId",users.getUid());
                intent.putExtra("key",users.getKey());
            }
            else {
                intent.putExtra("userName",users.getName().toString());
                intent.putExtra("imageUri",users.getImageUri());
                intent.putExtra("userId",users.getUid());
                intent.putExtra("major",users.getMajor());
                intent.putExtra("year",users.getYear());
                intent.putExtra("key",users.getKey());
            }
            context.startActivity(intent);
        }
    }
    public void setFilter(List<Users> text) {
        data = new ArrayList<>();
        data.addAll(text);
        notifyDataSetChanged();
    }
}
