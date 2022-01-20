package com.bluetooth.printer.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AdapteraSample extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected ArrayList<BluetoothDevice> listItem;

    protected final int EMPTY = 0;
    protected final int NON_EMPTY = 1;
    protected final RecyclerListener recyclerListener;


    public AdapteraSample(ArrayList<BluetoothDevice> listItem, RecyclerListener recyclerListener) {
        this.listItem = listItem;
        this.recyclerListener = recyclerListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VHSample(getlayoutInflater(parent.getContext())
                .inflate(R.layout.adapter_sample, parent, false), recyclerListener);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHSample) {
            ((VHSample) holder).setData(listItem.get(position));
        }
    }


    @Override
    public int getItemCount() {
        if (listItem != null && listItem.size() > 0) {
            return listItem.size();
        } else {
            return 1;
        }
    }

    public ArrayList<BluetoothDevice> getListItem() {
        return listItem;
    }

    @Override
    public int getItemViewType(int position) {
        if (listItem.size() != 0) {
            return NON_EMPTY;
        } else {
            return EMPTY;
        }
    }

    protected LayoutInflater getlayoutInflater(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater;
    }


}
