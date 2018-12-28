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
    public interface IMsgViewType {
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

    public int getItemViewType(int position){
        PersonChat entity = lists.get(position);

        if(entity.isMeSend()) {
            return IMsgViewType.IMVT_COM_MSG;
        } else {
            return IMsgViewType.IMVT_TO_MSG;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holderView = null;
        PersonChat entity = lists.get(position);
        boolean isTip = entity.isTip();
        boolean isMeSend = entity.isMeSend();
        if(holderView == null) {
            holderView = new HolderView();
            if(isMeSend) {
                //显示自己发送
                convertView = View.inflate(context,R.layout.chat_dialog_right_item,null);
                holderView.tv_me_name = convertView.findViewById(R.id.tv_me_name);   //绑定发送昵称TextView
                holderView.tv_chat_me_message = convertView.findViewById(R.id.tv_chat_me_message);  //绑定消息显示TextView
                holderView.tv_me_name.setText(entity.getName());        //显示昵称
                holderView.tv_chat_me_message.setText(entity.getChatMsg());     //显示消息
            } else {
                //显示接收消息
                convertView = View.inflate(context,R.layout.chat_dialog_left_item,null);
                holderView.tv_name = convertView.findViewById(R.id.tv_name);        //绑定接收昵称TextView
                holderView.tv_chat_message = convertView.findViewById(R.id.tv_chat_message);        //绑定消息显示TextView
                holderView.tv_name.setText(entity.getName());
                holderView.tv_chat_message.setText(entity.getChatMsg());        //显示消息
            }
            if(isTip){
                //显示提示信息
                convertView = View.inflate(context,R.layout.chat_dialog_tip_item,null);
                holderView.tv_tip = convertView.findViewById(R.id.tv_chat_tip);
                holderView.tv_tip.setText(entity.getChatMsg());
            }
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        return convertView;
    }

    class HolderView {
        TextView tv_me_name;
        TextView tv_name;
        TextView tv_chat_me_message;    //绑定自己发送文本框
        TextView tv_chat_message;       //绑定非自己发送文本框
        TextView tv_tip;                //绑定提示语
    }

    public boolean isEnabled(int position) {
        return false;
    }
}
