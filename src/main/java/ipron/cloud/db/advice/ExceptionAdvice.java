package ipron.cloud.db.advice;

import ipron.cloud.db.common.CommonCode;
import ipron.cloud.db.model.response.ResultModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.util.Map;

/**
 * @author jw.lee
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice /*extends ResponseEntityExceptionHandler*/ {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            {
                    HttpMessageNotReadableException.class,
                    MethodArgumentNotValidException.class,
                    HttpRequestMethodNotSupportedException.class
            }
    )
    public Map<String, Object> badRequest(Exception e) {
        log.error("bad request exception.\n", e);
        return ResultModel.error(CommonCode.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Map<String, Object> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("no handle found exception.\n", e);
        return ResultModel.error(CommonCode.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handleRuntimeException(final RuntimeException e) {
        log.error("runtime exception.\n", e);
        return ResultModel.error(CommonCode.COM_ERROR_INTERNAL_SERVER, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleCommonException(final Exception e) {
        log.error("common exception.\n", e);
        return ResultModel.error(CommonCode.COM_ERROR_INTERNAL_SERVER, e.getMessage());
    }
}
