package ipron.cloud.db.model.response;

import ipron.cloud.db.common.CommonCode;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 공통 결과 형식
 * @author jw.lee
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResultModel {

    /**
     * 응답 성공 결과 : true/false
     */
    protected boolean result;

    /**
     * 응답 코드 번호
     */
    protected String code;

    /**
     * http 응답 코드
     */
    protected int status;

    /**
     * 응답 타이틀
     */
    protected String title;

    /**
     * 응답 메시지
     */
    protected String msg;

    protected Map<String, Object> data;

    public static Map<String, Object> success() {
        return toMap(ResultModel.builder()
                .result(Boolean.TRUE)
                .status(CommonCode.SUCCESS.getStatus())
                .code(CommonCode.SUCCESS.getCode())
                .title(CommonCode.SUCCESS.getTitle())
                .msg("")
                .build()
        );
    }

    public static Map<String, Object> success(String msg) {
        return toMap(ResultModel.builder()
                .result(Boolean.TRUE)
                .status(CommonCode.SUCCESS.getStatus())
                .code(CommonCode.SUCCESS.getCode())
                .title(CommonCode.SUCCESS.getTitle())
                .msg(msg)
                .build()
        );
    }

    public static Map<String, Object> error(CommonCode commonCode, String msg) {
        return toMap(ResultModel.builder()
                .result(Boolean.TRUE)
                .status(commonCode.getStatus())
                .code(commonCode.getCode())
                .title(commonCode.getTitle())
                .msg(msg)
                .build()
        );
    }

    public static Map<String, Object> setData(ResultModel model, Object data) {
        Map<String, Object> map = toMap(model);
        map.put("data", data);
        return map;
    }

    public static Map<String, Object> setData(Map<String, Object> map, Object data) {
        map.put("data", data);
        return map;
    }

    public static Map<String, Object> toMap(ResultModel model) {
        // int capacity = (int) ((expected_maximal_number_of_data)/0.75+1);
        // 16: DEFAULT_INITIAL_CAPACITY
        Map<String, Object> map = new HashMap<>(16);
        map.put("result", model.isResult());
        map.put("code", model.getCode());
        map.put("status", model.getStatus());
        map.put("title", model.getTitle());
        map.put("msg", model.getMsg());
        map.put("data", model.getData());
        return map;
    }
}
