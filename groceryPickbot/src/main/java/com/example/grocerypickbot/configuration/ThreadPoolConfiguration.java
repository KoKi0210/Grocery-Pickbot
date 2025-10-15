package com.example.grocerypickbot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration class for setting up thread pool executors.
 */
@Configuration
public class ThreadPoolConfiguration {

  /**
   * Defines a ThreadPoolTaskExecutor bean named "botTaskExecutor"
   * with a core and max pool size of 3.
   *
   * @return the configured ThreadPoolTaskExecutor
   */
  @Bean("botTaskExecutor")
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(3);
    executor.setThreadNamePrefix("BotThread-");
    executor.initialize();
    return executor;
  }
}
