package com.hyphenate.chatuidemo.section.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.DemoConstant;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.livedatas.MessageChangeLiveData;
import com.hyphenate.chatuidemo.common.widget.ArrowItemView;
import com.hyphenate.chatuidemo.common.widget.SwitchItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.chatuidemo.section.dialog.EditTextDialogFragment;
import com.hyphenate.chatuidemo.section.group.fragment.GroupEditFragment;
import com.hyphenate.chatuidemo.section.group.viewmodels.GroupDetailViewModel;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class GroupDetailActivity extends BaseInitActivity implements EaseTitleBar.OnBackPressListener, View.OnClickListener, SwitchItemView.OnCheckedChangeListener {
    private static final int REQUEST_CODE_ADD_USER = 0;
    private EaseTitleBar titleBar;
    private EaseImageView ivGroupAvatar;
    private TextView tvGroupName;
    private TextView tvGroupIntroduction;
    private TextView tvGroupMemberTitle;
    private TextView tvGroupMemberNum;
    private TextView tvGroupInvite;
    private ArrowItemView itemGroupName;
    private ArrowItemView itemGroupShareFile;
    private ArrowItemView itemGroupNotice;
    private ArrowItemView itemGroupIntroduction;
    private ArrowItemView itemGroupHistory;
    private SwitchItemView itemGroupNotDisturb;
    private SwitchItemView itemGroupTop;
    private TextView tvGroupRefund;
    private String groupId;
    private EMGroup group;
    private GroupDetailViewModel viewModel;

    public static void actionStart(Context context, String groupId) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_chat_group_detail;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        ivGroupAvatar = findViewById(R.id.iv_group_avatar);
        tvGroupName = findViewById(R.id.tv_group_name);
        tvGroupIntroduction = findViewById(R.id.tv_group_introduction);
        tvGroupMemberTitle = findViewById(R.id.tv_group_member_title);
        tvGroupMemberNum = findViewById(R.id.tv_group_member_num);
        tvGroupInvite = findViewById(R.id.tv_group_invite);
        itemGroupName = findViewById(R.id.item_group_name);
        itemGroupShareFile = findViewById(R.id.item_group_share_file);
        itemGroupNotice = findViewById(R.id.item_group_notice);
        itemGroupIntroduction = findViewById(R.id.item_group_introduction);
        itemGroupHistory = findViewById(R.id.item_group_history);
        itemGroupNotDisturb = findViewById(R.id.item_group_not_disturb);
        itemGroupTop = findViewById(R.id.item_group_top);
        tvGroupRefund = findViewById(R.id.tv_group_refund);

        group = DemoHelper.getInstance().getGroupManager().getGroup(groupId);
        initGroupView();
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(this);
        tvGroupMemberTitle.setOnClickListener(this);
        tvGroupMemberNum.setOnClickListener(this);
        tvGroupInvite.setOnClickListener(this);
        itemGroupName.setOnClickListener(this);
        itemGroupShareFile.setOnClickListener(this);
        itemGroupNotice.setOnClickListener(this);
        itemGroupIntroduction.setOnClickListener(this);
        itemGroupHistory.setOnClickListener(this);
        itemGroupNotDisturb.setOnCheckedChangeListener(this);
        itemGroupTop.setOnCheckedChangeListener(this);
        tvGroupRefund.setOnClickListener(this);
    }

    private void initGroupView() {
        if(group == null) {
            finish();
            return;
        }
        tvGroupName.setText(group.getGroupName());
        itemGroupName.getTvContent().setText(group.getGroupName());
        tvGroupMemberNum.setText(getString(R.string.em_chat_group_detail_member_num, group.getMemberCount()));
        tvGroupRefund.setText(getResources().getString(isOwner() ? R.string.em_chat_group_detail_dissolve : R.string.em_chat_group_detail_refund));
        tvGroupIntroduction.setText(group.getDescription());
        itemGroupNotDisturb.getSwitch().setChecked(group.isMsgBlocked());
        EMConversation conversation = DemoHelper.getInstance().getConversation(groupId, EMConversation.EMConversationType.GroupChat, true);
        String extField = conversation.getExtField();
        itemGroupTop.getSwitch().setChecked(!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField));
        tvGroupInvite.setVisibility(group.getMemberCount() <= 0 ? View.VISIBLE : View.GONE);
        tvGroupInvite.setVisibility(isCanInvite() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel = new ViewModelProvider(this).get(GroupDetailViewModel.class);
        viewModel.getGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EMGroup>() {
                @Override
                public void onSuccess(EMGroup data) {
                    group = data;
                    initGroupView();
                }
            });
        });
        viewModel.getRefreshObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    loadGroup();
                }
            });
        });
        viewModel.getMessageChangeObservable().observe(this, event -> {
            if(event.isGroupChange()) {
                loadGroup();
            }
        });
        viewModel.getLeaveGroupObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    finish();
                    MessageChangeLiveData.getInstance().postValue(EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP_LEAVE));
                }
            });
        });
        loadGroup();
    }

    private void loadGroup() {
        viewModel.getGroup(groupId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_group_member_title :// 群成员
                GroupMemberTypeActivity.actionStart(mContext, groupId, isOwner());
                break;
            case R.id.tv_group_invite ://邀请群成员
                GroupPickContactsActivity.actionStartForResult(mContext, groupId, isOwner(), REQUEST_CODE_ADD_USER);
                break;
            case R.id.item_group_name ://群名称
                showGroupNameDialog();
                break;
            case R.id.item_group_share_file ://共享文件
                GroupSharedFilesActivity.actionStart(mContext, groupId);
                break;
            case R.id.item_group_notice ://群公告
                showAnnouncementDialog();
                break;
            case R.id.item_group_introduction ://群介绍
                showIntroductionDialog();
                break;
            case R.id.item_group_history ://查找聊天记录
                showToast("查找聊天记录");
                break;
            case R.id.tv_group_refund ://退出群组
                if(isOwner()) {
                    viewModel.destroyGroup(groupId);
                }else {
                    viewModel.leaveGroup(groupId);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.item_group_not_disturb ://消息免打扰
                showToast("消息免打扰");
                break;
            case R.id.item_group_top ://消息置顶
                showToast("消息置顶");
                break;
        }
    }

    private void showGroupNameDialog() {
        EditTextDialogFragment.showDialog(mContext,
                getString(R.string.em_chat_group_detail_name),
                group.getGroupName(), new EditTextDialogFragment.ConfirmClickListener(){

                    @Override
                    public void onConfirmClick(View view, String content) {
                        if(!TextUtils.isEmpty(content)) {
                            viewModel.setGroupName(groupId, content);
                        }
                    }
                });
    }

    private void showAnnouncementDialog() {
        GroupEditFragment.showDialog(mContext,
                getString(R.string.em_chat_group_detail_announcement),
                group.getAnnouncement(),
                getString(R.string.em_chat_group_detail_announcement_hint),
                new GroupEditFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        //修改群公告
                        viewModel.setGroupAnnouncement(groupId, content);
                    }
                });
    }

    private void showIntroductionDialog() {
        GroupEditFragment.showDialog(mContext,
                getString(R.string.em_chat_group_detail_introduction),
                group.getDescription(),
                getString(R.string.em_chat_group_detail_introduction_hint),
                new GroupEditFragment.OnSaveClickListener() {
                    @Override
                    public void onSaveClick(View view, String content) {
                        //修改群介绍
                        viewModel.setGroupDescription(groupId, content);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (resultCode) {
                case REQUEST_CODE_ADD_USER :
                    loadGroup();
                    break;
            }
        }
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    /**
     * 是否有邀请权限
     * @return
     */
    private boolean isCanInvite() {
        return GroupHelper.isCanInvite(group);
    }

    /**
     * 是否是管理员
     * @return
     */
    private boolean isAdmin() {
        return GroupHelper.isAdmin(group);
    }

    /**
     * 是否是群主
     * @return
     */
    private boolean isOwner() {
        return GroupHelper.isOwner(group);
    }
}