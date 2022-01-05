package com.example.rainbowptt;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ale.rainbow.RBLog;

import java.util.LinkedList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyRecyclerViewHolder> {
    public static String contact;

    private final List<String> list;
    private LayoutInflater mInflater;

    public MyRecyclerViewAdapter(Context context, List<String> participants_names) {
        mInflater = LayoutInflater.from(context);
        this.list = participants_names;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.fragment_main_item,
                parent, false);
        MyRecyclerViewHolder holder = new MyRecyclerViewHolder(mItemView, new MyClickListener() {
            @Override
            public void onDelete(String p) {
                contact = p;

                BubbleActivity.deleteContact();
                RBLog.warn("LOG_TAG", contact);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.MyRecyclerViewHolder holder, int position) {
        String mCurrent = list.get(position);
        holder.wordItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MyClickListener listener;
        public final TextView wordItemView;
        public final ImageButton delete;
        //final MyRecyclerViewAdapter mAdapter;

        public MyRecyclerViewHolder(@NonNull View itemView, MyClickListener listener) {
            super(itemView);
            delete = itemView.findViewById(R.id.imageButton);
            wordItemView = itemView.findViewById(R.id.item_title);
            //this.mAdapter = adapter;

            this.listener = listener;
            delete.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.imageButton:
                    LinearLayout rl = (LinearLayout)v.getParent();
                    TextView editText = rl.findViewById(R.id.item_title);
                    String contact = editText.getText().toString().trim();
                    listener.onDelete(contact);
            }

        }
    }
    public interface MyClickListener {
        void onDelete(String p);
    }
}
