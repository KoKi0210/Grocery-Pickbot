package com.example.grocerypickbot.bot.models;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the availability status of a bot.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BotAvailability {
  private Bot bot;
  private AtomicBoolean available = new AtomicBoolean(true);
}

