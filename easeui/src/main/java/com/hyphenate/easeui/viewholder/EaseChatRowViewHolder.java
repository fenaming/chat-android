package com.hyphenate.easeui.viewholder;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.util.EMLog;


public class EaseChatRowViewHolder extends EaseMessageAdapter.ViewHolder<EMMessage> implements EaseChatRow.EaseChatRowActionCallback {
    private Context context;
    private EaseChatRow chatRow;
    private EMMessage message;
    private MessageListItemClickListener mItemClickListener;
    private EaseMessageListItemStyle mItemStyle;

    public static EaseChatRowViewHolder create(ViewGroup parent, int viewType,
                                               MessageListItemClickListener itemClickListener,
                                               EaseMessageListItemStyle itemStyle) {
        switch (viewType) {
            case EaseMessageAdapter.MESSAGE_TYPE_RECV_EXPRESSION :
            case EaseMessageAdapter.MESSAGE_TYPE_SENT_EXPRESSION :
                // big expression
                return EaseExpressionViewHolder.create(parent,
                        viewType == EaseMessageAdapter.MESSAGE_TYPE_SENT_EXPRESSION,
                        itemClickListener, itemStyle);
            case EaseMessageAdapter.MESSAGE_TYPE_RECV_IMAGE :
            case EaseMessageAdapter.MESSAGE_TYPE_SENT_IMAGE :
                // image
                return EaseImageViewHolder.create(parent,
                        viewType == EaseMessageAdapter.MESSAGE_TYPE_SENT_IMAGE,
                        itemClickListener, itemStyle);
            case EaseMessageAdapter.MESSAGE_TYPE_RECV_LOCATION :
            case EaseMessageAdapter.MESSAGE_TYPE_SENT_LOCATION :
                // location
                return EaseLocationViewHolder.create(parent,
                        viewType == EaseMessageAdapter.MESSAGE_TYPE_SENT_LOCATION,
                        itemClickListener, itemStyle);
            case EaseMessageAdapter.MESSAGE_TYPE_RECV_VOICE :
            case EaseMessageAdapter.MESSAGE_TYPE_SENT_VOICE :
                // voice
                return EaseVoiceViewHolder.create(parent,
                        viewType == EaseMessageAdapter.MESSAGE_TYPE_SENT_VOICE,
                        itemClickListener, itemStyle);
            case EaseMessageAdapter.MESSAGE_TYPE_RECV_VIDEO :
            case EaseMessageAdapter.MESSAGE_TYPE_SENT_VIDEO :
                // video
                return EaseVideoViewHolder.create(parent,
                        viewType == EaseMessageAdapter.MESSAGE_TYPE_SENT_VIDEO,
                        itemClickListener, itemStyle);
            case EaseMessageAdapter.MESSAGE_TYPE_RECV_FILE :
            case EaseMessageAdapter.MESSAGE_TYPE_SENT_FILE :
                // file
                return EaseFileViewHolder.create(parent,
                        viewType == EaseMessageAdapter.MESSAGE_TYPE_SENT_FILE,
                        itemClickListener, itemStyle);
            default:
                // text
                return EaseTextViewHolder.create(parent,
                        viewType == EaseMessageAdapter.MESSAGE_TYPE_SENT_TXT,
                        itemClickListener, itemStyle);
        }

    }

    public EaseChatRowViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener,
                                 EaseMessageListItemStyle itemStyle) {
        super(itemView);
        // 解决view宽和高不显示的问题
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
        this.context = itemView.getContext();
        this.mItemClickListener = itemClickListener;
        this.mItemStyle = itemStyle;
    }

    @Override
    public void initView(View itemView) {
        this.chatRow = (EaseChatRow) itemView;
    }

    @Override
    public void setData(EMMessage item, int position) {
        message = item;
        chatRow.setUpView(item, position, mItemClickListener, this, mItemStyle);
        handleMessage();
    }

    @Override
    public void onResendClick(EMMessage message) {

    }

    @Override
    public void onBubbleClick(EMMessage message) {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    private void handleMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            handleSendMessage(message);
        } else if (message.direct() == EMMessage.Direct.RECEIVE) {
            handleReceiveMessage(message);
        }
    }

    /**
     * send message
     * @param message
     */
    protected void handleSendMessage(final EMMessage message) {
        // Update the view according to the message current status.
        getChatRow().updateView(message);

        if (message.status() == EMMessage.Status.INPROGRESS) {
            EMLog.i("handleSendMessage", "Message is INPROGRESS");
            if (this.mItemClickListener != null) {
                this.mItemClickListener.onMessageInProgress(message);
            }
        }
    }

    /**
     * receive message
     * @param message
     */
    protected void handleReceiveMessage(EMMessage message) {

    }

    public Context getContext() {
        return context;
    }

    public EaseChatRow getChatRow() {
        return chatRow;
    }
}