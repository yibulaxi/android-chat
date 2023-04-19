/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package cn.wildfire.chat.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wildfire.chat.app.login.model.LoginResult;
import cn.wildfire.chat.app.login.model.PCSession;
import cn.wildfire.chat.kit.AppServiceProvider;
import cn.wildfire.chat.kit.ChatManagerHolder;
import cn.wildfire.chat.kit.Config;
import cn.wildfire.chat.kit.WfcUIKit;
import cn.wildfire.chat.kit.favorite.FavoriteItem;
import cn.wildfire.chat.kit.group.GroupAnnouncement;
import cn.wildfire.chat.kit.net.BooleanCallback;
import cn.wildfire.chat.kit.net.OKHttpHelper;
import cn.wildfire.chat.kit.net.SimpleCallback;
import cn.wildfire.chat.kit.net.base.StatusResult;
import cn.wildfire.chat.kit.voip.conference.model.ConferenceInfo;
import cn.wildfirechat.AppServiceUrl;
import cn.wildfirechat.chat.BuildConfig;
import cn.wildfirechat.model.Conversation;
import cn.wildfirechat.remote.ChatManager;
import cn.wildfirechat.remote.GeneralCallback;
import cn.wildfirechat.remote.GeneralCallback2;
import okhttp3.MediaType;

public class AppService implements AppServiceProvider {
    private static final AppService Instance = new AppService();

    private AppService() {

    }

    public static AppService Instance() {
        return Instance;
    }

    public interface LoginCallback {
        void onUiSuccess(LoginResult loginResult);

        void onUiFailure(int code, String msg);
    }

    public void passwordLogin(String mobile, String password, LoginCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("password", password);
        //如果是android pad设备，需要改这里，另外需要在ClientService对象中修改设备类型，请在ClientService代码中搜索"android pad"
        //if（当前设备是android pad)
        //  params.put("platform", new Integer(9));
        //else
        params.put("platform", new Integer(2));

        try {
            params.put("clientId", ChatManagerHolder.gChatManager.getClientId());
        } catch (Exception e) {
            e.printStackTrace();
            callback.onUiFailure(-1, "网络出来问题了。。。");
            return;
        }

        OKHttpHelper.post(AppServiceUrl.Login_PWD, params, new SimpleCallback<LoginResult>() {
            @Override
            public void onUiSuccess(LoginResult loginResult) {
                callback.onUiSuccess(loginResult);
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    public void smsLogin(String phoneNumber, String authCode, LoginCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", phoneNumber);
        params.put("code", authCode);
        //Platform_iOS = 1,
        //Platform_Android = 2,
        //Platform_Windows = 3,
        //Platform_OSX = 4,
        //Platform_WEB = 5,
        //Platform_WX = 6,
        //Platform_linux = 7,
        //Platform_iPad = 8,
        //Platform_APad = 9,

        //如果是android pad设备，需要改这里，另外需要在ClientService对象中修改设备类型，请在ClientService代码中搜索"android pad"
        //if（当前设备是android pad)
        //  params.put("platform", new Integer(9));
        //else
        params.put("platform", new Integer(2));

        try {
            params.put("clientId", ChatManagerHolder.gChatManager.getClientId());
        } catch (Exception e) {
            e.printStackTrace();
            callback.onUiFailure(-1, "获取clientId失败");
            return;
        }

        OKHttpHelper.post(AppServiceUrl.Login_SMS, params, new SimpleCallback<LoginResult>() {
            @Override
            public void onUiSuccess(LoginResult loginResult) {
                callback.onUiSuccess(loginResult);
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    public void resetPassword(String mobile, String code, String password, SimpleCallback<StatusResult> callback) {
        Map<String, Object> params = new HashMap<>();
        if (!TextUtils.isEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        params.put("resetCode", code);
        params.put("newPassword", password);

        OKHttpHelper.post(AppServiceUrl.RESET_PWD, params, callback);
    }

    public void changePassword(String oldPassword, String newPassword, SimpleCallback<StatusResult> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("oldPassword", oldPassword);
        params.put("newPassword", newPassword);

        OKHttpHelper.post(AppServiceUrl.CHANGE_PWD, params, callback);

    }

    public interface SendCodeCallback {
        void onUiSuccess();

        void onUiFailure(int code, String msg);
    }

    public void requestAuthCode(String phoneNumber, SendCodeCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("mobile", phoneNumber);
        OKHttpHelper.post(AppServiceUrl.SEND_CODE, params, new SimpleCallback<StatusResult>() {
            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (statusResult.getCode() == 0) {
                    callback.onUiSuccess();
                } else {
                    callback.onUiFailure(statusResult.getCode(), "");
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });

    }

    public void requestResetAuthCode(String phoneNumber, SendCodeCallback callback) {
        Map<String, Object> params = new HashMap<>();
        if (!TextUtils.isEmpty(phoneNumber)) {
            params.put("mobile", phoneNumber);
        }
        OKHttpHelper.post(AppServiceUrl.SEND_CODE_REPEAT, params, new SimpleCallback<StatusResult>() {
            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (statusResult.getCode() == 0) {
                    callback.onUiSuccess();
                } else {
                    callback.onUiFailure(statusResult.getCode(), "");
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });

    }

    public interface ScanPCCallback {
        void onUiSuccess(PCSession pcSession);

        void onUiFailure(int code, String msg);
    }

    public void scanPCLogin(String token, ScanPCCallback callback) {
        OKHttpHelper.post(String.format(AppServiceUrl.LOGIN_PC_SCAN, token), null, new SimpleCallback<PCSession>() {
            @Override
            public void onUiSuccess(PCSession pcSession) {
                if (pcSession.getStatus() == 1) {
                    callback.onUiSuccess(pcSession);
                } else {
                    callback.onUiFailure(pcSession.getStatus(), "");
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    public interface PCLoginCallback {
        void onUiSuccess();

        void onUiFailure(int code, String msg);
    }

    public void confirmPCLogin(String token, String userId, PCLoginCallback callback) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("user_id", userId);
        params.put("token", token);
        params.put("quick_login", 1);
        OKHttpHelper.post(AppServiceUrl.LOGIN_PC_CONFIRM, params, new SimpleCallback<PCSession>() {
            @Override
            public void onUiSuccess(PCSession pcSession) {
                if (pcSession.getStatus() == 2) {
                    callback.onUiSuccess();
                } else {
                    callback.onUiFailure(pcSession.getStatus(), "");
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    public void cancelPCLogin(String token, PCLoginCallback callback) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("token", token);
        OKHttpHelper.post(AppServiceUrl.LOGIN_PC_REJECT, params, new SimpleCallback<PCSession>() {
            @Override
            public void onUiSuccess(PCSession pcSession) {
                if (pcSession.getStatus() == 2) {
                    callback.onUiSuccess();
                } else {
                    callback.onUiFailure(pcSession.getStatus(), "");
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    @Override
    public void getGroupAnnouncement(String groupId, AppServiceProvider.GetGroupAnnouncementCallback callback) {
        //从SP中获取到历史数据callback回去，然后再从网络刷新
        Map<String, Object> params = new HashMap<>(2);
        params.put("groupId", groupId);
        OKHttpHelper.post(AppServiceUrl.GET_GROUP_ANNOUNCEMENT, params, new SimpleCallback<GroupAnnouncement>() {
            @Override
            public void onUiSuccess(GroupAnnouncement announcement) {
                callback.onUiSuccess(announcement);
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    @Override
    public void updateGroupAnnouncement(String groupId, String announcement, AppServiceProvider.UpdateGroupAnnouncementCallback callback) {
        //更新到应用服务，再保存到本地SP中
        Map<String, Object> params = new HashMap<>(2);
        params.put("groupId", groupId);
        params.put("author", ChatManagerHolder.gChatManager.getUserId());
        params.put("text", announcement);
        OKHttpHelper.post(AppServiceUrl.PUT_GROUP_ANNOUNCEMENT, params, new SimpleCallback<GroupAnnouncement>() {
            @Override
            public void onUiSuccess(GroupAnnouncement announcement) {
                callback.onUiSuccess(announcement);
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    @Override
    public void showPCLoginActivity(String userId, String token, int platform) {
        Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".pc.login");
        intent.putExtra("token", token);
        intent.putExtra("isConfirmPcLogin", true);
        intent.putExtra("platform", platform);
        WfcUIKit.startActivity(ChatManager.Instance().getApplicationContext(), intent);
    }

    @Override
    public void uploadLog(SimpleCallback<String> callback) {
        List<String> filePaths = ChatManager.Instance().getLogFilesPath();
        if (filePaths == null || filePaths.isEmpty()) {
            if (callback != null) {
                callback.onUiFailure(-1, "没有日志文件");
            }
            return;
        }
        Context context = ChatManager.Instance().getApplicationContext();
        if (context == null) {
            if (callback != null) {
                callback.onUiFailure(-1, "not init");
            }
            return;
        }
        SharedPreferences sp = context.getSharedPreferences("log_history", Context.MODE_PRIVATE);

        String userId = ChatManager.Instance().getUserId();
        String url = AppServiceUrl.UPLOAD_LOG + userId + "/upload";

        int toUploadCount = 0;
        Collections.sort(filePaths);
        for (int i = 0; i < filePaths.size(); i++) {
            String path = filePaths.get(i);
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }
            // 重复上传最后一个日志文件，因为上传之后，还会追加内容
            if (!sp.contains(path) || i == filePaths.size() - 1) {
                toUploadCount++;
                OKHttpHelper.upload(url, null, file, MediaType.get("application/octet-stream"), new SimpleCallback<Void>() {
                    @Override
                    public void onUiSuccess(Void aVoid) {
                        if (callback != null) {
                            callback.onSuccess(url);
                        }
                        sp.edit().putBoolean(path, true).commit();
                    }

                    @Override
                    public void onUiFailure(int code, String msg) {
                        if (callback != null) {
                            callback.onUiFailure(code, msg);
                        }
                    }
                });
            }
        }
        if (toUploadCount == 0) {
            if (callback != null) {
                callback.onUiFailure(-1, "所有日志都已上传");
            }
        }
    }

    @Override
    public void changeName(String newName, SimpleCallback<Void> callback) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("newName", newName);
        OKHttpHelper.post(AppServiceUrl.CHANGE_NAME, params, new SimpleCallback<Void>() {
            @Override
            public void onUiSuccess(Void aVoid) {
                callback.onUiSuccess(null);
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    @Override
    public void getFavoriteItems(int startId, int count, GetFavoriteItemCallback callback) {
        if (callback == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id", startId);
        params.put("count", count);
        OKHttpHelper.post(AppServiceUrl.GET_FAVORITE_LIST, params, new SimpleCallback<String>() {
            @Override
            public void onUiSuccess(String s) {
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONObject result = obj.getJSONObject("result");
                    boolean hasMore = result.getBoolean("hasMore");
                    JSONArray items = result.getJSONArray("items");

                    List<FavoriteItem> favoriteItems = new ArrayList<>();
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject itemObj = items.getJSONObject(i);
                        Conversation conversation = new Conversation(Conversation.ConversationType.type(itemObj.getInt("convType")), itemObj.getString("convTarget"), itemObj.getInt("convLine"));
                        FavoriteItem item = new FavoriteItem(itemObj.getInt("id"),
                                itemObj.optLong("messageUid"),
                                itemObj.getInt("type"),
                                itemObj.getLong("timestamp"),
                                conversation,
                                itemObj.getString("origin"),
                                itemObj.getString("sender"),
                                itemObj.getString("title"),
                                itemObj.getString("url"),
                                itemObj.getString("thumbUrl"),
                                itemObj.getString("data")
                        );

                        favoriteItems.add(item);
                    }

                    callback.onUiSuccess(favoriteItems, hasMore);

                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onUiFailure(-1, e.getMessage());
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onUiFailure(code, msg);
            }
        });
    }

    @Override
    public void addFavoriteItem(FavoriteItem item, SimpleCallback<Void> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("messageUid", item.getMessageUid());
        params.put("type", item.getFavType());
        params.put("convType", item.getConversation().type.getValue());
        params.put("convTarget", item.getConversation().target);
        params.put("convLine", item.getConversation().line);
        params.put("origin", item.getOrigin());
        params.put("sender", item.getSender());
        params.put("title", item.getTitle());
        params.put("url", item.getUrl());
        params.put("thumbUrl", item.getThumbUrl());
        params.put("data", item.getData());

        OKHttpHelper.post(AppServiceUrl.ADD_FAVORITE, params, callback);
    }

    @Override
    public void removeFavoriteItem(int favId, SimpleCallback<Void> callback) {
        OKHttpHelper.post(String.format(AppServiceUrl.DEL_FAVORITE, favId), null, callback);
    }

    public static void validateConfig(Context context) {
        if (TextUtils.isEmpty(Config.IM_SERVER_HOST)
                || Config.IM_SERVER_HOST.startsWith("http")
                || Config.IM_SERVER_HOST.contains(":")
                || TextUtils.isEmpty(AppServiceUrl.HOST)
                || (!AppServiceUrl.HOST.startsWith("http") && !AppServiceUrl.HOST.startsWith("https"))
                || Config.IM_SERVER_HOST.equals("127.0.0.1")
                || AppServiceUrl.HOST.contains("127.0.0.1")
                || (!Config.IM_SERVER_HOST.contains(AppServiceUrl.IP) && AppServiceUrl.HOST.contains(AppServiceUrl.IP))
                || (Config.IM_SERVER_HOST.contains(AppServiceUrl.IP) && !AppServiceUrl.HOST.contains(AppServiceUrl.IP))
        ) {
            Toast.makeText(context, "配置错误，请检查配置，应用即将关闭...", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(() -> {
                throw new IllegalArgumentException("config error\n 参数配置错误\n请仔细阅读配置相关注释，并检查配置!\n");
            }, 5 * 1000);
        }

        for (String[] ice : Config.ICE_SERVERS) {
            if (!ice[0].startsWith("turn")) {
                Toast.makeText(context, "Turn配置错误，请检查配置，应用即将关闭...", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(() -> {
                    throw new IllegalArgumentException("config error\n 参数配置错误\n请仔细阅读配置相关注释，并检查配置!\n");
                }, 5 * 1000);
            }
        }
    }

    @Override
    public void getMyPrivateConferenceId(GeneralCallback2 callback) {
        if (callback == null) {
            return;
        }
        OKHttpHelper.post(AppServiceUrl.CONFERENCE_PRIVATE_ID, null, new SimpleCallback<String>() {

            @Override
            public void onUiSuccess(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code", -1) == 0) {
                        String conferenceId = object.getString("result");
                        callback.onSuccess(conferenceId);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onFail(-1);
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onFail(code);
            }
        });
    }

    @Override
    public void createConference(ConferenceInfo info, GeneralCallback2 callback) {
        OKHttpHelper.post(AppServiceUrl.CONFERENCE_CREATE, info, new SimpleCallback<String>() {
            @Override
            public void onUiSuccess(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code", -1) == 0) {
                        String conferenceId = object.getString("result");
                        callback.onSuccess(conferenceId);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onFail(-1);
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onFail(code);
            }
        });
    }

    @Override
    public void queryConferenceInfo(String conferenceId, String password, QueryConferenceInfoCallback callback) {
        if (callback == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("conferenceId", conferenceId);
        if (!TextUtils.isEmpty(password)) {
            map.put("password", password);
        }
        OKHttpHelper.post(AppServiceUrl.CONFERENCE_INFO, map, new SimpleCallback<ConferenceInfo>() {

            @Override
            public void onUiSuccess(ConferenceInfo info) {
                callback.onSuccess(info);
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onFail(code, msg);
            }
        });
    }

    @Override
    public void destroyConference(String conferenceId, GeneralCallback callback) {
        OKHttpHelper.post(String.format(AppServiceUrl.CONFERENCE_DESTROY, conferenceId), null, new SimpleCallback<StatusResult>() {

            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (statusResult.isSuccess()) {
                    callback.onSuccess();
                } else {
                    callback.onFail(statusResult.getCode());
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                callback.onFail(code);
            }
        });
    }

    @Override
    public void favConference(String conferenceId, GeneralCallback callback) {
        OKHttpHelper.post(String.format(AppServiceUrl.CONFERENCE_FAVORITE, conferenceId), null, new SimpleCallback<StatusResult>() {
            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (callback != null) {
                    if (statusResult.isSuccess()) {
                        callback.onSuccess();
                    } else {
                        callback.onFail(statusResult.getCode());
                    }
                }

            }

            @Override
            public void onUiFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFail(code);
                }
            }
        });
    }

    @Override
    public void unfavConference(String conferenceId, GeneralCallback callback) {
        OKHttpHelper.post(String.format(AppServiceUrl.CONFERENCE_FAVORITE_NOT, conferenceId), null, new SimpleCallback<StatusResult>() {
            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (callback != null) {
                    if (statusResult.isSuccess()) {
                        callback.onSuccess();
                    } else {
                        callback.onFail(statusResult.getCode());
                    }
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFail(code);
                }
            }
        });
    }

    @Override
    public void isFavConference(String conferenceId, BooleanCallback callback) {
        OKHttpHelper.post(String.format(AppServiceUrl.CONFERENCE_FAVORITE_IS, conferenceId), null, new SimpleCallback<StatusResult>() {
            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (callback != null) {
                    if (statusResult.getCode() == 0) {
                        callback.onSuccess(true);
                    } else if (statusResult.getCode() == 16) {
                        callback.onSuccess(false);
                    } else {
                        callback.onFail(-1, "");
                    }
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFail(code, msg);
                }
            }
        });
    }

    @Override
    public void getFavConferences(FavConferenceCallback callback) {
        OKHttpHelper.post(AppServiceUrl.CONFERENCE_FAVORITE_LSIT, null, new SimpleCallback<List<ConferenceInfo>>() {
            @Override
            public void onUiSuccess(List<ConferenceInfo> favConferences) {
                if (callback != null) {
                    callback.onSuccess(favConferences);
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFail(code, msg);
                }
            }
        });
    }

    @Override
    public void updateConference(ConferenceInfo conferenceInfo, GeneralCallback callback) {
        OKHttpHelper.post(AppServiceUrl.CONFERENCE_INFO_PUT, conferenceInfo, new SimpleCallback<StatusResult>() {
            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFail(code);
                }
            }
        });
    }

    @Override
    public void recordConference(String conferenceId, boolean record, GeneralCallback callback) {
        Map<String, Boolean> params = new HashMap<>();
        params.put("recording", record);
        OKHttpHelper.post(String.format(AppServiceUrl.CONFERENCE_RECORDING, conferenceId), params, new SimpleCallback<StatusResult>() {
            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFail(code);
                }
            }
        });
    }

    @Override
    public void setConferenceFocusUserId(String conferenceId, String userId, GeneralCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", TextUtils.isEmpty(userId) ? "" : userId);
        OKHttpHelper.post(String.format(AppServiceUrl.CONFERENCE_FOCUS, conferenceId), params, new SimpleCallback<StatusResult>() {
            @Override
            public void onUiSuccess(StatusResult statusResult) {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onUiFailure(int code, String msg) {
                if (callback != null) {
                    callback.onFail(code);
                }
            }
        });
    }
}
