package party.threebody.herd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;
@EnableAsync
@SpringBootApplication
public class HerdWebApp {
    /**
     * JDK ConcurrentMap-based Cache, by using Spring's SimpleCacheManager<br>
     * alternatives: GuavaCache, CaffeineCache, EhCache
     *
     * @author hzk
     * @since 2017-08-05
     */
    @Configuration
    @EnableCaching
    public class CacheConfig {

        @Bean
        public SimpleCacheManager cacheManager() {
            SimpleCacheManager cm = new SimpleCacheManager();
            ConcurrentMapCache cb1 = new ConcurrentMapCache("aaa");
            cm.setCaches(Arrays.asList(cb1));
            return cm;

        }

    }
    public static void main(String[] args) {
        SpringApplication.run(HerdWebApp.class, args);
    }

}
