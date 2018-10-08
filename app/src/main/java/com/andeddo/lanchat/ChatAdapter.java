package com.andeddo.lanchat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<PersonChat> lists;

    public ChatAdapter(Context context,List<PersonChat> lists){
        super();
        this.context = context;
        this.lists = lists;
    }

    /**
     * 是否是自己发送的消息
     */
    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;// 收到对方的消息
        int IMVT_TO_MSG = 1;// 自己发送出去的消息
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holderView = null;
        PersonChat entity = lists.get(position);
        boolean isMeSend = entity.isMeSend();
        if(holderView == null) {
            holderView = new HolderView();
            if(isMeSend) {
                convertView = View.inflate(context,R.layout.chat_dialog_right_item,null);
                holderView.tv_chat_me_message = convertView.findViewById(R.id.tv_chat_me_message);
                holderView.tv_chat_me_message.setText(entity.getChatMsg());
            } else {
                convertView = View.inflate(context,R.layout.chat_dialog_left_item,null);
                holderView.tv_chat_message = convertView.findViewById(R.id.tv_chat_message);
                holderView.tv_chat_message.setText(entity.getChatMsg());
            }
        }
        return null;
    }

    class HolderView {
        TextView tv_chat_me_message;    //绑定自己发送文本框
        TextView tv_chat_message;       //绑定非自己发送文本框
    }

    public boolean isEnabled(int position) {
        return false;
    }
}
