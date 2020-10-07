package com.example.calldetect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calldetect.R;
import com.example.calldetect.models.ItemDrawer;

import java.util.List;

public class ItemDrawerAdapter extends BaseAdapter {

    private List<ItemDrawer> listItems;
    private OnItemClickListener listener;

    public ItemDrawerAdapter (List<ItemDrawer> listItems) {
        this.listItems = listItems;
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public ItemDrawer getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemDrawer itemDrawer = listItems.get(position);

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_drawer,
                    parent, false);
        }

        ((ImageView)convertView.findViewById(R.id.logo_item)).setImageDrawable(itemDrawer.getIcon());
        ((TextView)convertView.findViewById(R.id.title_item)).setText(itemDrawer.getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });

        return convertView;
    }
}
