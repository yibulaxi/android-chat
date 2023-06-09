/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfire.chat.kit.contact.viewholder.header;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import cn.wildfire.chat.kit.*;
import cn.wildfire.chat.kit.contact.UserListAdapter;
import cn.wildfire.chat.kit.contact.model.FriendRequestValue;
import cn.wildfirechat.remote.ChatManager;

@SuppressWarnings("unused")
public class FriendRequestViewHolder extends HeaderViewHolder<FriendRequestValue> {
    TextView unreadRequestCountTextView;
    private FriendRequestValue value;


    public FriendRequestViewHolder(Fragment fragment, UserListAdapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        bindViews(itemView);
    }

    private void bindViews(View itemView) {
        unreadRequestCountTextView = itemView.findViewById(R.id.unreadFriendRequestCountTextView);
    }

    @Override
    public void onBind(FriendRequestValue value) {
        this.value = value;
        int count = ChatManager.Instance().getUnreadFriendRequestStatus();
        if (count > 0) {
            unreadRequestCountTextView.setVisibility(View.VISIBLE);
            unreadRequestCountTextView.setText("" + count);
        } else {
            unreadRequestCountTextView.setVisibility(View.GONE);
        }
    }
}
