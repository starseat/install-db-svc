package ipron.cloud.db.common;

/**
 * @author jw.lee
 */
public class CommonKey {

    public static class Param {
        public final static String CONNECTION = "connection";
        public final static String COLLECTIONS = "collections";
        public final static String VALIDATION = "validation";
        public final static String INDEX_FIELDS = "fields";
        public final static String INDEX_UNIQUE = "unique";

        public final static String DATA = "data";
    }

    public static class Validation {
        public final static String COLL_MOD = "collMod";
    }

    public static class Collection {
        public final static String ACCOUNT = "account";
        public final static String USER = "user";
    }

    public static class InitData {
        public final static String ACCOUNT = "common_tenant";
        public final static String TIME_ZONE = "Asia/Seoul";

        public final static String ADMIN_EMAIL = "admin@bcloud.co.kr";
        public final static String ADMIN_NAME = "최고 관리자";
        public final static String ADMIN_AUTH_LEVEL = "superadmin";
        public final static String ADMIN_INIT_PASSWORD =
                // 1234
                "308070480bf07a5db77aea47f5c41a7739089822203f64baf31b3156df766ceae62ff8e64429bd024d7321f806f662f24aa12d9b6985f208f86783f741e8730c";
        public final static String ADMIN_EXTENSION = "0000";
        public final static String ADMIN_NUM = "0000000";

        public final static String TNT_ID = "tntId";
        public final static String NAME = "name";

        public final static String OBJECT_ID = "_id";

        public final static String CREATE_USER_ID = "createUserId";
        public final static String CREATE_DATE = "createDate";
        public final static String UPDATE_USER_ID = "updateUserId";
        public final static String UPDATE_DATE = "updateDate";
    }

    public static class AccessAuthKind {
        public final static String ALL = "all";
        public final static String GROUP = "group";
        public final static String FLOW = "flow";
        public final static String QUEUE = "queue";

    }
}
