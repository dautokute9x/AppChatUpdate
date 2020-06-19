package com.example.chatappdemo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappdemo.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapterGroup extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ArrayList<String> listGroup;
    private Context context;
    private AdapterListener adapterListener;

    public RecyclerViewAdapterGroup(ArrayList<String> listGroup, Context context, AdapterListener adapterListener) {
        this.listGroup = listGroup;
        this.context = context;
        this.adapterListener = adapterListener;
    }

    public interface AdapterListener{
        void OnClick();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_group_recyclerview, parent, false);
            return new HeaderViewHolder(itemView);
        } else
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.imgItemadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.OnClick();
                }
            });
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            ((ItemViewHolder) holder).tv_title_group.setText("");
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Click item",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return listGroup.size() + 1;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgItemadd;
        TextView tv_header;

        public HeaderViewHolder(View view) {
            super(view);
            imgItemadd = (CircleImageView) view.findViewById(R.id.imgAdd);
            tv_header = view.findViewById(R.id.tv_Add);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title_group;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_title_group = itemView.findViewById(R.id.recycler_item_text);
        }
    }
}
