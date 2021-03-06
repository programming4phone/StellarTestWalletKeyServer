package com.programming4phone.stellar.wallet.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	/**
	 * Connection factory for a Redis instance running on localhost within
	 * a Windows Docker container.
	 * To start the container enter these commands from a Windows commmand prompt.
	 * <br><code>docker pull redis</code>
	 * <br><code>docker run --name some-redis –p 6379:6379 -d redis</code>
	 * <br><br>
	 * When finished, enter these commands from a Windows commmand prompt.
	 * <br><code>docker stop some-redis</code>
	 * <br><code>docker rm some-redis</code>
	 * @return RedisConnectionFactory
	 */
	@Bean
	public RedisConnectionFactory redisCF() {
		JedisConnectionFactory cf = new JedisConnectionFactory();
		                                                                                                                                                 
		cf.setHostName("127.0.0.1");
		//cf.setHostName("10.0.75.1"); // ipconfig use Ip addr of DockerNAT
		cf.setPort(6379);
		return cf;
	}
	
	/**
	 * Create a StringRedisTemplate for the specific RedisConnectionFactory.
	 * @param cf RedisConnectionFactory
	 * @return StringRedisTemplate
	 */
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
		return new StringRedisTemplate(cf);
	}
	
	/**
	 * Create a RestTemplate.
	 * @return RestTemplate
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
