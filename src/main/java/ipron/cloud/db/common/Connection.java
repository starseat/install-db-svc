package ipron.cloud.db.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jw.lee
 */
@Getter
@Setter
public class Connection {

    private String mongodbConnection = "mongodb://localhost:27017";

    public static Connection getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final Connection INSTANCE = new Connection();
    }
}
