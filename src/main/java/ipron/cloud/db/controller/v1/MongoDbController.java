package ipron.cloud.db.controller.v1;

import ipron.cloud.db.common.CommonCode;
import ipron.cloud.db.common.CommonKey;
import ipron.cloud.db.model.response.ResultModel;
import ipron.cloud.db.service.v1.MongoDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jw.lee
 */
@Slf4j
@RestController
@RequestMapping(value = "/db/v1")
@RequiredArgsConstructor
public class MongoDbController {

    private final MongoDbService service;

    @ResponseBody
    @GetMapping("/mongodb/connection")
    public Map<String, Object> getConnection() {
        return service.getConnection();
    }

    /**
     * connection 정보 설정
     * @param paramMap { "connection": <connection string> }
     * @return ResultModel to Map
     */
    @ResponseBody
    @PostMapping("/mongodb/connection")
    public Map<String, Object> setConnection(
            @RequestBody final Map<String, String> paramMap
    ) {
        if(!paramMap.containsKey(CommonKey.Param.CONNECTION)) {
            String msg = errMsgEmptyParam(CommonKey.Param.CONNECTION);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        String connection = paramMap.get(CommonKey.Param.CONNECTION);
        if(ObjectUtils.isEmpty(connection)) {
            String msg = errMsgEmptyParamData(CommonKey.Param.CONNECTION);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        return service.setConnection(connection);
    }
    
    /**
     * mongodb 의 database 생성 및 collection 생성
     * @param dbname 생성할 database 명
     * @param paramMap
     *        {
     *            "collections": [ <collection 1>, <collection 2>, ... ]
     *        }
     * @return ResultModel to Map
     */
    @ResponseBody
    @PostMapping("/mongodb/create/{dbname}")
    public Map<String, Object> create(
            @PathVariable(value = "dbname") final String dbname,
            @RequestBody final Map<String, List<String>> paramMap
    ) {
        if(!paramMap.containsKey(CommonKey.Param.COLLECTIONS)) {
            String msg = errMsgEmptyParam(CommonKey.Param.COLLECTIONS);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        List<String> collections = paramMap.get(CommonKey.Param.COLLECTIONS);
        if(collections == null || collections.isEmpty()) {
            String msg = errMsgEmptyParamData(CommonKey.Param.COLLECTIONS);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        return service.create(dbname, collections);
    }

    /**
     * mongodb 에 생성된 database 및 collection 조회
     * @param dbname 조회할 database 명
     * @return ResultModel to Map
     */
    @ResponseBody
    @GetMapping("/mongodb/create/{dbname}")
    public Map<String, Object> getCreated(
            @PathVariable(value = "dbname") final String dbname
    ) {
        return service.getCreatedDb(dbname);
    }

    /**
     * 해당 collection 에 validation 추가 (json 형태)
     * @param dbname 작업 대상 database
     * @param collection 작업 대상 collection
     * @param paramMap
     *        {
     *            "validation": { <json 형태의 validation 설정 정보> }
     *        }
     * @return ResultModel to Map
     */
    @ResponseBody
    @PostMapping("/mongodb/validation/{dbname}/{collection}")
    public Map<String, Object> setValidation(
            @PathVariable(value = "dbname") final String dbname,
            @PathVariable(value = "collection") final String collection,
            @RequestBody final Map<String, HashMap<String, Object>> paramMap
    ) {
        if(!paramMap.containsKey(CommonKey.Param.VALIDATION)) {
            String msg = errMsgEmptyParam(CommonKey.Param.VALIDATION);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        HashMap<String, Object> validation = paramMap.get(CommonKey.Param.VALIDATION);
        if(ObjectUtils.isEmpty(validation)) {
            String msg = errMsgEmptyParamData(CommonKey.Param.VALIDATION);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        return service.setValidation(dbname, collection, validation);
    }

    /**
     * 해당 collection 의 validation 조회
     * @param dbname 작업 대상 database
     * @param collection 작업 대상 collection
     * @return ResultModel to Map
     */
    @ResponseBody
    @GetMapping("/mongodb/validation/{dbname}/{collection}")
    public Map<String, Object> getValidation(
            @PathVariable(value = "dbname") final String dbname,
            @PathVariable(value = "collection") final String collection
    ) {
        return service.getValidation(dbname, collection);
    }

    /**
     * 해당 collection 에 index 추가 (json 형태)
     * @param dbname 작업 대상 database
     * @param collection 작업 대상 collection
     * @param paramMap
     *        {
     *            "fields": { <collection field name 1>, <collection field name 2>, ... }
     *            "unique": <boolean - true|false>
     *        }
     * @return ResultModel to Map
     */
    @ResponseBody
    @PostMapping("/mongodb/index/{dbname}/{collection}")
    public Map<String, Object> createIndex(
            @PathVariable(value = "dbname") final String dbname,
            @PathVariable(value = "collection") final String collection,
            @RequestBody final Map<String, Object> paramMap
    ) {
        if(!paramMap.containsKey(CommonKey.Param.INDEX_FIELDS)) {
            String msg = errMsgEmptyParam(CommonKey.Param.INDEX_FIELDS);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        @SuppressWarnings("unchecked")
        List<String> fields = (List<String>)paramMap.get(CommonKey.Param.INDEX_FIELDS);
        if(fields == null || fields.isEmpty()) {
            String msg = errMsgEmptyParamData(CommonKey.Param.INDEX_FIELDS);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        return service.createIndex(dbname, collection, fields,
                MapUtils.getBooleanValue(paramMap, CommonKey.Param.INDEX_UNIQUE));
    }

    /**
     * 해당 collection 에 저장된 index 조회
     * @param dbname 작업 대상 database
     * @param collection 작업 대상 collection
     * @return ResultModel to Map
     */
    @ResponseBody
    @GetMapping("/mongodb/index/{dbname}/{collection}")
    public Map<String, Object> getIndexes(
            @PathVariable(value = "dbname") final String dbname,
            @PathVariable(value = "collection") final String collection
    ) {
        return service.getIndexes(dbname, collection);
    }

    /**
     * 생성된 mongodb database 에 기초 데이터 추가
     *   - account(common_tenant), user(super admin)
     * @param dbname 생성할 database 명
     * @return ResultModel to Map
     *        {
     *            "data": {tntId}
     *        }
     */
    @ResponseBody
    @PostMapping("/mongodb/init-data/{dbname}")
    public Map<String, Object> createInitData(
            @PathVariable(value = "dbname") final String dbname
    ) {
        return service.createInitData(dbname);
    }

    /**
     * 생성된 mongodb database 에 기초 데이터 추가
     * @param dbname 작업 대상 database
     * @param collection 작업 대상 collection
     * @param paramMap  data.name 필수 입력
     *        {
     *            "data": <Object>
     *        }
     * @return ResultModel to Map
     */
    @ResponseBody
    @PostMapping("/mongodb/init-data/{dbname}/{collection}")
    public Map<String, Object> createInitData(
            @PathVariable(value = "dbname") final String dbname,
            @PathVariable(value = "collection") final String collection,
            @RequestBody final Map<String, Object> paramMap
    ) {
        if(ObjectUtils.isEmpty(paramMap)) {
            String msg = errMsgEmptyParam(CommonKey.Param.DATA);
            log.error(msg);
            return ResultModel.error(CommonCode.INVALID_PARAM, msg);
        }

        return service.createInitData(dbname, collection, paramMap);
    }

    /**
     * 'account' 기본 정보 조회 (id, name, ...)
     * @param dbname 작업 대상 database
     * @return ResultModel to Map
     * 
     */
    @ResponseBody
    @GetMapping("/mongodb/init-data/{dbname}/account")
    public Map<String, Object> getAccount(
            @PathVariable(value = "dbname") final String dbname

    ) {
        return service.getDocuments(dbname, CommonKey.Collection.ACCOUNT);
    }

    /**
     * collection 기본 정보 조회 (id, name, ...)
     * @param dbname 작업 대상 database
     * @param collection 작업 대상 collection
     * @return ResultModel to Map
     */
    @ResponseBody
    @GetMapping("/mongodb/init-data/{dbname}/{collection}")
    public Map<String, Object> getDocument(
            @PathVariable(value = "dbname") final String dbname,
            @PathVariable(value = "collection") final String collection
    ) {
        return service.getDocuments(dbname, collection);
    }

    private String errMsgEmptyParam(String param) {
        return "'" + param + "' is empty";
    }

    private String errMsgEmptyParamData(String data) {
        return "'" + data + "' data is empty";
    }

}
