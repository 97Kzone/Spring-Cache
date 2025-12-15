package kzone.cache;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest extends RedisTestContainerSupport {

    @Test
    void test() {
        redisTemplate.opsForValue().set("myKey", "myvalue");
        String result = redisTemplate.opsForValue().get("myKey");
        System.out.println("result = " + result);
    }

    void test2() {
        String result = redisTemplate.opsForValue().get("myKey");
        System.out.println("result = " + result);
    }

    void test3() {
        String result = redisTemplate.opsForValue().get("myKey");
        System.out.println("result = " + result);
    }

}
