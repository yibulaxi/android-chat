/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfire.chat.kit.conversation.ext.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import cn.wildfire.chat.kit.conversation.ext.ImageExt;
import cn.wildfire.chat.kit.conversation.ext.ShootExt;
import cn.wildfire.chat.kit.conversation.ext.VoipExt;
import cn.wildfirechat.model.Conversation;

/**
 * Desc 聊天会话底部功能面板
 * 照片、文件、视频通话、位置、名片等
 */
public class ConversationExtManager {
    private static ConversationExtManager instance;
    private List<ConversationExt> conversationExts;

    private ConversationExtManager() {
        conversationExts = new ArrayList<>();
        init();
    }

    public static synchronized ConversationExtManager getInstance() {
        if (instance == null) {
            instance = new ConversationExtManager();
        }
        return instance;
    }

    private void init() {
        registerExt(ImageExt.class);
        registerExt(VoipExt.class);
        registerExt(ShootExt.class);
//        registerExt(FileExt.class);
//        registerExt(LocationExt.class);
//        registerExt(ExampleAudioInputExt.class);
//        registerExt(UserCardExt.class);
    }

    public void registerExt(Class<? extends ConversationExt> clazz) {
        Constructor constructor;
        try {
            constructor = clazz.getConstructor();
            ConversationExt ext = (ConversationExt) constructor.newInstance();
            conversationExts.add(ext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterExt(Class<? extends ConversationExt> clazz) {
        // TODO
    }

    public List<ConversationExt> getConversationExts(Conversation conversation) {
        List<ConversationExt> currentExts = new ArrayList<>();
        for (ConversationExt ext : this.conversationExts) {
            if (!ext.filter(conversation)) {
                currentExts.add(ext);
            }
        }
        return currentExts;
    }
}
