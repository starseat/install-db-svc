package ipron.cloud.db.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author jw.lee
 * IDS: Install DB Service
 */
@Getter
@AllArgsConstructor
public enum CommonCode {
    // error - 4xx
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "COM40000", "Bad Request"),
    INVALID_PARAM(HttpStatus.BAD_REQUEST.value(), "COM40002", "Invalid Parameter"),
    INVALID_TYPE(HttpStatus.BAD_REQUEST.value(), "COM40003", "Invalid Type"),

    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "COM40400", "Not Found"),

    DUPLICATED_DATA(HttpStatus.CONFLICT.value(),"COM40901","Duplicated data"),

    // error - 5xx
    COM_ERROR_INTERNAL_SERVER(HttpStatus.INTERNAL_SERVER_ERROR.value(), "COM50000", "Internal Server Error"),

    DB_ERROR_CONNECTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL50000", "Database Connection Error"),
    DB_ERROR_EXECUTE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL50001", "Database Execute Error"),
    DB_ERROR_VALIDATOR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL50002", "Database Validator Error"),

    DB_ERROR_CREATE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL50011", "Create table(collection) Error"),
    DB_ERROR_INSERT(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL50012", "Create init data Error"),
    DB_ERROR_GET(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL50013", "Get data Error"),

    // success
    SUCCESS(HttpStatus.OK.value(), "IDS20000", "success")
    ;

    private final int status;
    private final String code;
    private final String title;
}
