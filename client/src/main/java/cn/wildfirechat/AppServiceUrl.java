package cn.wildfirechat;

/**
 * Desc 网络请求地址
 */
public class AppServiceUrl {
    /**
     * App Server默认使用的是8888端口，替换为自己部署的服务时需要注意端口别填错了
     * <br>
     * <br>
     * 正式商用时，建议用https，确保token安全
     * <br>
     * <br>
     */
    public static String IP = "103.151.229.106";
//    public static String IP = "wildfirechat.net";
    public static String HOST = "http://" + IP + ":8888";

    public static String Login_PWD = HOST + "/login_pwd";
    public static String Login_SMS = HOST + "/login";
    public static String LOGIN_PC_SCAN = HOST + "/scan_pc/%s";
    public static String LOGIN_PC_CONFIRM = HOST + "/confirm_pc";
    public static String LOGIN_PC_REJECT = HOST + "/cancel_pc";

    public static String RESET_PWD = HOST + "/reset_pwd";
    public static String CHANGE_PWD = HOST + "/change_pwd";
    public static String SEND_CODE = HOST + "/send_code";
    public static String SEND_CODE_REPEAT = HOST + "/send_reset_code";

    public static String GET_GROUP_ANNOUNCEMENT = HOST + "/get_group_announcement";
    public static String PUT_GROUP_ANNOUNCEMENT = HOST + "/put_group_announcement";

    public static String UPLOAD_LOG = HOST + "/logs/";

    public static String CHANGE_NAME = HOST + "/change_name";

    public static String GET_FAVORITE_LIST = HOST + "/fav/list";
    public static String ADD_FAVORITE = HOST + "/fav/add";
    public static String DEL_FAVORITE = HOST + "/fav/del/%s";

    public static String CONFERENCE_PRIVATE_ID = HOST + "/conference/get_my_id";
    public static String CONFERENCE_CREATE = HOST + "/conference/create";
    public static String CONFERENCE_INFO = HOST + "/conference/info";
    public static String CONFERENCE_INFO_PUT = HOST + "/conference/put_info";
    public static String CONFERENCE_DESTROY = HOST + "/conference/destroy/%s";
    public static String CONFERENCE_FAVORITE = HOST + "/conference/fav/%s";
    public static String CONFERENCE_FAVORITE_NOT = HOST + "/conference/unfav/%s";
    public static String CONFERENCE_FAVORITE_IS = HOST + "/conference/is_fav/%s";
    public static String CONFERENCE_FAVORITE_LIST = HOST + "/conference/fav_conferences";

    public static String CONFERENCE_RECORDING = HOST + "/conference/recording/%s";
    public static String CONFERENCE_FOCUS = HOST + "/conference/focus/%s";

}
