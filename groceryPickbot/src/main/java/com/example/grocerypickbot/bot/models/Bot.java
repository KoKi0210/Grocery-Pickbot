package com.example.grocerypickbot.bot.models;

import com.example.grocerypickbot.product.models.Location;

/**
 * Represents a bot with an ID and a default location.
 *
 * @param id              the unique identifier of the bot
 * @param defaultLocation the default location of the bot
 */
public record Bot(
        String id,
        Location defaultLocation
) {
}


