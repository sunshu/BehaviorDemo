package nus.me.behaviordemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nus on 16-10-10.
 */

public class MyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Info> list = new ArrayList<>();

    public MyAdapter(Context mContext, ArrayList<Info> list) {
        this.mContext = mContext;
        this.list = list;
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView tv;
        ImageView iv;
        public Holder(View itemView) {
            super(itemView);
            tv =  (TextView) itemView.findViewById(R.id.tv);
            iv = (ImageView) itemView.findViewById(R.id.iv);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.item, parent,false);

        Holder holder = new Holder(item);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Holder h = (Holder) holder;
        h.tv .setText( list.get(position).title);
        Picasso.with(mContext).load(list.get(position).url_img).into(h.iv);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
