package com.bluetooth.printer.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.printer.bluetooth.libsocketbluetoothprinter.R;

public class VHSample extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected final RecyclerListener recyclerListener;
    private TextView textView;

    public VHSample(View itemView, RecyclerListener recyclerListener) {
        super(itemView);
        this.recyclerListener = recyclerListener;
        itemView.setOnClickListener(this);
        textView = findView(R.id.name);
    }

    public void setData(BluetoothDevice data) {
        itemView.setTag(data);
        textView.setText(data.getName() + "\n" + data.getAddress()+"\n");
    }

    protected <K extends View> K findView(int id) {
        return (K) itemView.findViewById(id);
    }

    protected LayoutInflater getlayoutInflater(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater;
    }


    @Override
    public void onClick(View view) {
        recyclerListener.onItemClick(itemView.getTag());
    }
}
