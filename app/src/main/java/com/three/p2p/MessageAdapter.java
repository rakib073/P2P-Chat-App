package com.three.p2p;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private List<Message>mChat;
    private Context context;
    private LayoutInflater inflater;
    private View v1,v2;
    MessageAdapter(Context context,List<Message>mChat)
    {
        this.context=context;
        this.mChat=mChat;

    }

    @Override
    public int getCount() {
        return mChat.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              v1=inflater.inflate(R.layout.chat_item_right,viewGroup,false);
              v2=inflater.inflate(R.layout.textitem_left,viewGroup,false);
            if(mChat.get(i).getIdentifier()==1)
            {
                view=v1;
            }
            else view=v2;
        }
        TextView textView=(TextView)view.findViewById(R.id.messageId);
        textView.setText(mChat.get(i).getMsg().toString());

        return view;
    }
}
