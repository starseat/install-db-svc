package ipron.cloud.db.utils;

import com.google.gson.Gson;
import ipron.cloud.db.common.CommonKey;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author jw.lee
 */
public class CommonUtils {

    private Gson gson = new Gson();
    private Instant maxInstant = null;

    public Instant getMaxDate() {
        if (maxInstant == null) {
            TimeZone tz = TimeZone.getTimeZone(CommonKey.InitData.TIME_ZONE);
            DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMdd")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter()
//                .withResolverStyle(ResolverStyle.STRICT)
                .withZone(tz.toZoneId());

            maxInstant = ZonedDateTime.parse("99991231", dateTimeFormatter).toInstant();
        }
        return maxInstant;
    }

    public Map<String, Object> jsonToMap(String json) {
        return (Map<String,Object>) gson.fromJson(json, Map.class);
    }

    public String mapToJson(Map<String, Object> map) {
        return gson.toJson(map);
    }


    public static CommonUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final CommonUtils INSTANCE = new CommonUtils();
    }
}
