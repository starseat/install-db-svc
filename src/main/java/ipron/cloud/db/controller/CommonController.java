package ipron.cloud.db.controller;

import ipron.cloud.db.common.CommonCode;
import ipron.cloud.db.model.response.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author jw.lee
 */
@Slf4j
@RestController
public class CommonController {

    @GetMapping("/error")
    public Map<String, Object> error(HttpServletRequest req) {
        Object status = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String msg = "internal server error.";
        if (status == null) {
            log.error("[/error] status is empty. send msg: {}", msg);
            return ResultModel.error(CommonCode.COM_ERROR_INTERNAL_SERVER, msg);
        }

        int statusCode = Integer.parseInt(status.toString());
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            msg = "not found resource.";
            log.error("[/error] status is {}. send msg: {}", HttpStatus.NOT_FOUND.value(), msg);
            return ResultModel.error(CommonCode.NOT_FOUND, msg);
        }

        log.error("[/error] Unknown error. send msg: {}", msg);
        return ResultModel.error(CommonCode.COM_ERROR_INTERNAL_SERVER, "internal server error.");
    }
}
