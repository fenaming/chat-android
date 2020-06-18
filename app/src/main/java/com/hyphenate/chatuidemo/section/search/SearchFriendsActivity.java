package com.hyphenate.chatuidemo.section.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.db.DemoDbHelper;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.section.friends.activity.ContactDetailActivity;
import com.hyphenate.chatuidemo.section.friends.adapter.FriendsAdapter;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendsActivity extends SearchActivity {
    private List<EaseUser> mData;
    private List<EaseUser> result;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchFriendsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new SearchFriendAdapter();
    }

    @Override
    protected void initData() {
        super.initData();
        result = new ArrayList<>();
        mData = DemoDbHelper.getInstance(mContext).getUserDao().loadAllEaseUsers();
    }

    @Override
    public void searchMessages(String search) {
        searchResult(search);
    }

    private void searchResult(String search) {
        if(mData == null || mData.isEmpty()) {
            return;
        }

        ThreadManager.getInstance().runOnIOThread(()-> {
            result.clear();
            for (EaseUser user : mData) {
                if(user.getUsername().contains(search) || user.getNickname().contains(search)) {
                    result.add(user);
                }
            }
            runOnUiThread(()-> adapter.setData(result));
        });
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        EaseUser item = ((SearchFriendAdapter)adapter).getItem(position);
        ContactDetailActivity.actionStart(mContext, item);
    }

    private class SearchFriendAdapter extends FriendsAdapter {
        @Override
        public int getEmptyLayoutId() {
            return R.layout.ease_layout_default_no_data;
        }
    }
}