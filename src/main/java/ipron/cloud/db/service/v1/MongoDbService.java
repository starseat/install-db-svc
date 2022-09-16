package ipron.cloud.db.service.v1;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import ipron.cloud.db.common.CommonCode;
import ipron.cloud.db.common.CommonKey;
import ipron.cloud.db.common.Connection;
import ipron.cloud.db.model.response.ResultModel;
import ipron.cloud.db.utils.CommonUtils;
import ipron.cloud.db.utils.MongoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.*;

/**
 * @author jw.lee
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MongoDbService {

    private final Gson gson;

    public Map<String, Object> getConnection() {
        String msg = "get mongodb connection.";
        log.info(msg + " [{}]", Connection.getInstance().getMongodbConnection());

        Map<String, Object> map = ResultModel.success();
        map.put("msg", msg);
        map.put("data", Connection.getInstance().getMongodbConnection());
        return map;
    }

    public Map<String, Object> setConnection(String connection) {
        Connection.getInstance().setMongodbConnection(connection);
        String msg = "set mongodb connection.";
        log.info(msg + " [{}]", Connection.getInstance().getMongodbConnection());

        Map<String, Object> map = ResultModel.success();
        map.put("msg", msg);
        return map;
    }

    public Map<String, Object> create(String dbName, List<String> collections) {
        try (MongoClient mongoClient = MongoClients.create(Connection.getInstance().getMongodbConnection())) {
            MongoDatabase db = mongoClient.getDatabase(dbName);
            collections.forEach(db::createCollection);
            // mongoClient.close();
        } catch (Exception e) {
            log.error("create exception.. message: {}", e.getMessage());
            return ResultModel.error(CommonCode.DB_ERROR_CREATE, e.getMessage());
        }

        Map<String, Object> map = ResultModel.success();
        String msg = "create success [" + dbName + "] database. " +
                "and create [" + collections.size() + "] collections.";
        map.put("msg", msg);

        return map;
    }

    public Map<String, Object> getCreatedDb(String dbName) {
        Map<String, Object> dataMap;
        try (MongoClient mongoClient = MongoClients.create(Connection.getInstance().getMongodbConnection())) {
            MongoDatabase db = mongoClient.getDatabase(dbName);

            dataMap = new HashMap<>();
            dataMap.put("db", dbName);
            List<String> collections = new ArrayList<>();
            for (String name : db.listCollectionNames()) {
                collections.add(name);
            }
            dataMap.put("collections", collections);
        } catch (Exception e) {
            log.error("getCreatedDb exception.. message: {}", e.getMessage());
            return ResultModel.error(CommonCode.DB_ERROR_CREATE, e.getMessage());
        }

        return ResultModel.setData(ResultModel.success(), dataMap);
    }

    public Map<String, Object> setValidation(String dbName, String collection, HashMap<String, Object> validationMap) {
        String collMod = MapUtils.getString(validationMap, CommonKey.Validation.COLL_MOD);
        if(ObjectUtils.isEmpty(collMod)) {
            String msg = "'validation.collMod' is empty.";
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }
        if(!collection.equals(collMod)) {
            String msg = "'collection(" + collection + ")' and 'collMod(" + collMod + ")' are not equals.";
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        try (MongoClient mongoClient = MongoClients.create(Connection.getInstance().getMongodbConnection())) {
            MongoDatabase db = mongoClient.getDatabase(dbName);
            String validationJson = gson.toJson(validationMap);
            BasicDBObject basicDbObject = MongoUtils.getInstance().makeBsonObject(validationJson);
            db.runCommand(basicDbObject);
        } catch (Exception e) {
            log.error("setValidation exception.. message: {}", e.getMessage());
            return ResultModel.error(CommonCode.DB_ERROR_EXECUTE, e.getMessage());
        }

        return ResultModel.success();
    }

    public Map<String, Object> getValidation(String dbName, String collection) {
        // String command = "db.getCollectionInfos( {name: '" + collection + "'})[0].options.validator";
        log.debug("getValidation is not support. db: {}, collection: {}", dbName, collection);
        return ResultModel.error(CommonCode.COM_ERROR_INTERNAL_SERVER, "not support.");
    }

    public Map<String, Object> createIndex(String dbName, String collection,
                                           List<String> fields, boolean isUnique) {
        try (MongoClient mongoClient = MongoClients.create(Connection.getInstance().getMongodbConnection())) {
            MongoDatabase db = mongoClient.getDatabase(dbName);
            MongoCollection<Document> docCollection = db.getCollection(collection);

            docCollection.createIndex(Indexes.ascending(fields), new IndexOptions().unique(isUnique));
        } catch (Exception e) {
            log.error("createIndex exception.. message: {}", e.getMessage());
            return ResultModel.error(CommonCode.DB_ERROR_EXECUTE, e.getMessage());
        }

        return ResultModel.success();
    }

    public Map<String, Object> getIndexes(String dbName, String collection) {
        Map<String, Object> dataMap;
        try (MongoClient mongoClient = MongoClients.create(Connection.getInstance().getMongodbConnection())) {
            MongoDatabase db = mongoClient.getDatabase(dbName);
            MongoCollection<Document> docCollection = db.getCollection(collection);

            List<String> indexes = new ArrayList<>();
            for (Document doc : docCollection.listIndexes()) {
                indexes.add(doc.toJson());
            }

            dataMap = new HashMap<>();
            dataMap.put("indexes", indexes);
        } catch (Exception e) {
            log.error("getIndexes exception.. message: {}", e.getMessage());
            return ResultModel.error(CommonCode.DB_ERROR_EXECUTE, e.getMessage());
        }

        return ResultModel.setData(ResultModel.success(), dataMap);
    }

    public Map<String, Object> createInitData(String dbName) {
        String retTntId;
        try (MongoClient mongoClient = MongoClients.create(Connection.getInstance().getMongodbConnection())) {
            MongoDatabase db = mongoClient.getDatabase(dbName);

            MongoCollection<Document> collectionAccount = db.getCollection(CommonKey.Collection.ACCOUNT);
            MongoCollection<Document> collectionUser = db.getCollection(CommonKey.Collection.USER);

            // account 등록 검사
            Document queryAccount = new Document("$and", Arrays.asList(
                    new Document(CommonKey.InitData.NAME, CommonKey.InitData.ACCOUNT),
                    new Document("isMaster", Boolean.TRUE)
            ));
            Document docAccount = collectionAccount.find(queryAccount).first();
            if(docAccount != null) {
                String msg = "'" + CommonKey.InitData.ACCOUNT + "'"
                                + " has already been registered in '" + CommonKey.Collection.ACCOUNT + "' collection.";
                log.error(msg);
                return ResultModel.error(CommonCode.DUPLICATED_DATA, msg);
            }

            // admin user 등록 검사
            Document queryAdmin = new Document("$and", Arrays.asList(
                    new Document("email", CommonKey.InitData.ADMIN_EMAIL),
                    new Document("authLevel", CommonKey.InitData.ADMIN_AUTH_LEVEL)
            ));
            Document docAdmin = collectionUser.find(queryAdmin).first();
            if(docAdmin != null) {
                String msg = "'" + CommonKey.InitData.ADMIN_EMAIL + "'"
                                + "(" + CommonKey.InitData.ADMIN_AUTH_LEVEL + ")"
                                + " has already been registered in 'user' collection.";
                log.error(msg);
                return ResultModel.error(CommonCode.DUPLICATED_DATA, msg);
            }

            // tntId 생성
            ObjectId tntId = new ObjectId();
            retTntId = tntId.toHexString();
            log.info("generate tntId. {}", retTntId);

            // account(common_tenant) 생성
            collectionAccount.insertOne(initAccount(tntId));
            log.info("'{}' creation complete in '" + CommonKey.Collection.ACCOUNT + "' collection.",
                    CommonKey.InitData.ACCOUNT);

            // admin(super admin, user) 생성
            collectionUser.insertOne(initAdmin(tntId, null));
            log.info("'{}'({}) creation complete in 'user' collection.",
                    CommonKey.InitData.ADMIN_EMAIL, CommonKey.InitData.ADMIN_AUTH_LEVEL);
        } catch (Exception e) {
            log.error("createInitData exception.. message: {}", e.getMessage());
            return ResultModel.error(CommonCode.DB_ERROR_INSERT, e.getMessage());
        }

        Map<String, Object> map = ResultModel.success();
        String msg = "created init data in [" + dbName + "]";
        map.put("msg", msg);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(CommonKey.InitData.TNT_ID, retTntId);
        map.put("data", dataMap);

        return map;
    }

    public Map<String, Object> createInitData(String dbName, String collection, Map<String, Object> dataMap) {
        if(!dataMap.containsKey(CommonKey.InitData.NAME)) {
            String msg = "'name' is empty.";
            log.error("createInitData(custom) exception.. message: {}", msg);
            return ResultModel.error(CommonCode.DB_ERROR_INSERT, msg);
        }

        String name = MapUtils.getString(dataMap, CommonKey.InitData.NAME);

        try (MongoClient mongoClient = MongoClients.create(Connection.getInstance().getMongodbConnection())) {
            MongoDatabase db = mongoClient.getDatabase(dbName);

            MongoCollection<Document> collectionDoc = db.getCollection(collection);

            // 등록 검사
            Document query = new Document("$and", Arrays.asList(
                    new Document(CommonKey.InitData.NAME, name)
            ));

            Document doc = collectionDoc.find(query).first();
            if(doc != null) {
                String msg = "'" + name + "'"
                                + " has already been registered in '" + collection + "' collection.";
                log.error(msg);
                return ResultModel.error(CommonCode.DUPLICATED_DATA, msg);
            }

            // data 생성
            collectionDoc.insertOne(generateInitData(dataMap));
            log.info("'{}' creation complete in '{}' collection. (custom data)",
                    name, collection);

        } catch (Exception e) {
            log.error("createInitData(custom) exception.. message: {}", e.getMessage());
            return ResultModel.error(CommonCode.DB_ERROR_INSERT, e.getMessage());
        }

        Map<String, Object> map = ResultModel.success();
        String msg = "created init data in [" + dbName + "]";
        map.put("msg", msg);

        return map;
    }

    public Map<String, Object> getDocuments(String dbName, String collection) {
        List<Map<String, Object>> list = new ArrayList<>();

        try (MongoClient mongoClient = MongoClients.create(Connection.getInstance().getMongodbConnection())) {
            MongoDatabase db = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collectionDoc = db.getCollection(collection);

            if(collection.equals(CommonKey.Collection.ACCOUNT)) {
                Document query = new Document("$and", Arrays.asList(
                            new Document(CommonKey.InitData.NAME, CommonKey.InitData.ACCOUNT),
                            new Document("isMaster", Boolean.TRUE)
                    )
                );

                Document doc = collectionDoc.find(query).first();
                if(doc != null) {
                    list.add(excludeOtherData(CommonUtils.getInstance().jsonToMap(doc.toJson())));
                }
            } else {
                for(Document doc : collectionDoc.find()) {
                    list.add(excludeOtherData(CommonUtils.getInstance().jsonToMap(doc.toJson())));
                }
            }
        } catch (Exception e) {
            log.error("getDocuments({}) exception.. message: {}", collection, e.getMessage());
            return ResultModel.error(CommonCode.DB_ERROR_GET, e.getMessage());
        }

        return ResultModel.setData(ResultModel.success(), list);
    }

    private Document initAccount(ObjectId tntId) {
        Instant today = Instant.now();
        Document account = new Document(CommonKey.InitData.OBJECT_ID, tntId);
        account.append(CommonKey.InitData.NAME, CommonKey.InitData.ACCOUNT);
        account.append("alias", CommonKey.InitData.ACCOUNT);
        account.append("serviceOption", null);
        account.append("expireDate", CommonUtils.getInstance().getMaxDate());
        account.append("enable", Boolean.TRUE);
        account.append("timezone", CommonKey.InitData.TIME_ZONE);
        account.append("isMaster", Boolean.TRUE);
        account.append(CommonKey.InitData.CREATE_USER_ID, null);
        account.append(CommonKey.InitData.CREATE_DATE, today);
        account.append(CommonKey.InitData.UPDATE_USER_ID, null);
        account.append(CommonKey.InitData.UPDATE_DATE, today);

        return account;
    }

    private Document initAccessAuth(Object tntId, ObjectId accessAuthId) {
        Instant today = Instant.now();
        Document accessAuth = new Document(CommonKey.InitData.OBJECT_ID, accessAuthId);
        accessAuth.append(CommonKey.InitData.TNT_ID, tntId);
        accessAuth.append(CommonKey.InitData.NAME, CommonKey.AccessAuthKind.ALL);
        accessAuth.append("kind", CommonKey.AccessAuthKind.ALL);
        accessAuth.append("tag", "");
        accessAuth.append("groups", new ArrayList<>());
        accessAuth.append(CommonKey.InitData.CREATE_USER_ID, null);
        accessAuth.append(CommonKey.InitData.CREATE_DATE, today);
        accessAuth.append(CommonKey.InitData.UPDATE_USER_ID, null);
        accessAuth.append(CommonKey.InitData.UPDATE_DATE, today);

        return accessAuth;
    }

    private Document initAdmin(ObjectId tntId, ObjectId accessAuthId) {
        Instant today = Instant.now();
        Document admin = new Document(CommonKey.InitData.OBJECT_ID, new ObjectId());
        admin.append(CommonKey.InitData.TNT_ID, tntId);
        admin.append("email", CommonKey.InitData.ADMIN_EMAIL);
        admin.append("password", CommonKey.InitData.ADMIN_INIT_PASSWORD);
        admin.append(CommonKey.InitData.NAME, CommonKey.InitData.ADMIN_NAME);
        admin.append("tags", null);
        admin.append("authLevel", CommonKey.InitData.ADMIN_AUTH_LEVEL);
        admin.append("accessAuthId", null);
        admin.append("groupId", null);
        admin.append("extension", CommonKey.InitData.ADMIN_EXTENSION);
        admin.append("didNum", CommonKey.InitData.ADMIN_NUM);
        admin.append("billNum", CommonKey.InitData.ADMIN_NUM);
        admin.append("phoneId", null);
        admin.append("defaultSkillId", null);
        admin.append("skillSets", null);
        admin.append("schedule", null);
        admin.append("mediaOptions", null);
        admin.append("serviceOption", null);
        admin.append("lockYn", Boolean.FALSE);
        admin.append("expireDate", CommonUtils.getInstance().getMaxDate());
        admin.append("lastLoginDate", today);
        admin.append("enable", Boolean.TRUE);
        admin.append(CommonKey.InitData.CREATE_USER_ID, null);
        admin.append(CommonKey.InitData.CREATE_DATE, today);
        admin.append(CommonKey.InitData.UPDATE_USER_ID, null);
        admin.append(CommonKey.InitData.UPDATE_DATE, today);

        return admin;
    }

    private Document generateInitData(Map<String, Object> dataMap) {
        if(dataMap.containsKey(CommonKey.InitData.TNT_ID)) {
            dataMap.put(CommonKey.InitData.TNT_ID, new ObjectId(
                    MapUtils.getString(dataMap, CommonKey.InitData.TNT_ID))
            );
        }
        dataMap.put(CommonKey.InitData.OBJECT_ID, new ObjectId());

        Instant today = Instant.now();
        dataMap.put(CommonKey.InitData.CREATE_USER_ID, null);
        dataMap.put(CommonKey.InitData.CREATE_DATE, today);
        dataMap.put(CommonKey.InitData.UPDATE_USER_ID, null);
        dataMap.put(CommonKey.InitData.UPDATE_DATE, today);

        return new Document(dataMap);
    }

    private Map<String, Object> excludeOtherData(Map<String, Object> map) {
//        for(String key : map.keySet()) {
//            if( !(key.equals(CommonKey.InitData.OBJECT_ID) || key.equals(CommonKey.InitData.NAME) || key.equals(CommonKey.InitData.TNT_ID)) {
//                map.remove(key);
//            }
//        }

        Map<String, Object> newMap = new HashMap<>();
        if(map.containsKey(CommonKey.InitData.OBJECT_ID)) {
            newMap.put(CommonKey.InitData.OBJECT_ID, MapUtils.getObject(map, CommonKey.InitData.OBJECT_ID));
        }
        if(map.containsKey(CommonKey.InitData.TNT_ID)) {
            newMap.put(CommonKey.InitData.TNT_ID, MapUtils.getObject(map, CommonKey.InitData.TNT_ID));
        }
        if(map.containsKey(CommonKey.InitData.NAME)) {
            newMap.put(CommonKey.InitData.NAME, MapUtils.getString(map, CommonKey.InitData.NAME));
        }


        return newMap;
    }
}
