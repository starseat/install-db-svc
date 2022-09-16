package ipron.cloud.db.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jw.lee
 */
@Configuration
public class JsonConfig {

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
