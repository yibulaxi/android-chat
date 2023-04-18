/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfire.chat.kit.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Map;

import cn.wildfire.chat.kit.R;
import cn.wildfire.chat.kit.contact.model.ContactCountFooterValue;
import cn.wildfire.chat.kit.contact.model.FriendRequestValue;
import cn.wildfire.chat.kit.contact.model.UIUserInfo;
import cn.wildfire.chat.kit.contact.viewholder.footer.ContactCountViewHolder;
import cn.wildfire.chat.kit.user.UserInfoActivity;
import cn.wildfire.chat.kit.widget.QuickIndexBar;
import cn.wildfirechat.model.ChannelInfo;
import cn.wildfirechat.model.UserOnlineState;
import cn.wildfirechat.remote.ChatManager;

public class ContactListFragment extends BaseUserListFragment implements QuickIndexBar.OnLetterUpdateListener {
    private boolean pick = false;

    private List<String> filterUserList;
    private static final int REQUEST_CODE_PICK_CHANNEL = 100;

    private boolean isVisibleToUser = false;
    private ContactViewModel contactViewModel;


    @Override
    public void setMenuVisibility(boolean isvisible) {
        super.setMenuVisibility(isvisible);
        this.isVisibleToUser = isvisible;
        if (isvisible) {
            contactViewModel.reloadContact();
            contactViewModel.reloadFriendRequestStatus();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            pick = bundle.getBoolean("pick", false);
            filterUserList = bundle.getStringArrayList("filterUserList");
        }
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            contactViewModel.reloadContact();
            contactViewModel.reloadFriendRequestStatus();
            contactViewModel.reloadFavContact();
        }
    }

    @Override
    protected void afterViews(View view) {
        super.afterViews(view);
        contactViewModel.contactListLiveData().observe(this, userInfos -> {
            showContent();
            if (filterUserList != null) {
                userInfos.removeIf(uiUserInfo -> filterUserList.indexOf(uiUserInfo.getUserInfo().uid) > -1);
            }
            patchUserOnlineState(userInfos);
            userListAdapter.setUsers(userInfos);
        });
        contactViewModel.friendRequestUpdatedLiveData().observe(getActivity(), integer -> userListAdapter.updateHeader(0, new FriendRequestValue(integer)));
        contactViewModel.favContactListLiveData().observe(getActivity(), uiUserInfos -> {
            if (filterUserList != null) {
                uiUserInfos.removeIf(uiUserInfo -> filterUserList.indexOf(uiUserInfo.getUserInfo().uid) > -1);
            }
            patchUserOnlineState(uiUserInfos);
            userListAdapter.setFavUsers(uiUserInfos);
        });

    }

    private void patchUserOnlineState(List<UIUserInfo> list) {
        if (list == null) {
            return;
        }
        Map<String, UserOnlineState> userOnlineStateMap = ChatManager.Instance().getUserOnlineStateMap();
        for (UIUserInfo userInfo : list) {
            UserOnlineState userOnlineState = userOnlineStateMap.get(userInfo.getUserInfo().uid);
            if (userOnlineState != null) {
                String userOnlineDesc = userOnlineState.desc();
                if (!TextUtils.isEmpty(userOnlineDesc)) {
                    userInfo.setDesc(userOnlineDesc);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void initFooterViewHolders() {
        addFooterViewHolder(ContactCountViewHolder.class, R.layout.contact_item_footer, new ContactCountFooterValue());
    }

    @Override
    public void onUserClick(UIUserInfo userInfo) {
        if (pick) {
            Intent intent = new Intent();
            intent.putExtra("userInfo", userInfo.getUserInfo());
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            intent.putExtra("userInfo", userInfo.getUserInfo());
            startActivity(intent);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_PICK_CHANNEL && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            ChannelInfo channelInfo = data.getParcelableExtra("channelInfo");
            intent.putExtra("channelInfo", channelInfo);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
