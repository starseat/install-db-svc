package ipron.cloud.db.utils;

import com.mongodb.BasicDBObject;

/**
 * @author jw.lee
 */
public class MongoUtils {
    public BasicDBObject makeBsonObject(String json) {
        return BasicDBObject.parse(json);
    }

    public String makeJsonObject(BasicDBObject dbObj) {
        return dbObj.toJson();
    }

    public static MongoUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final MongoUtils INSTANCE = new MongoUtils();
    }
}
