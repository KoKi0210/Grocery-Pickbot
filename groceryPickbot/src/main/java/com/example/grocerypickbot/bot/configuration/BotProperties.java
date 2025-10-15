package com.example.grocerypickbot.bot.configuration;

import com.example.grocerypickbot.bot.models.BotAvailability;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for bots.
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
  private List<BotAvailability> bots;
}
