package com.example.grocerypickbot.order.dtos;

import com.example.grocerypickbot.order.models.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collections;
import java.util.List;

/**
 * Data Transfer Object representing the response of an order operation.
 *
 * @param status       the status of the order operation (e.g., SUCCESS, FAIL)
 * @param orderId      the ID of the order (if successful)
 * @param message      a message providing additional information about the order operation
 * @param missingItems a list of items that were missing in the order (if any)
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OrderResponseDto(
    OrderStatus status,
    Long orderId,
    String message,
    List<MissingItemDto> missingItems
) {
  /**
   * Creates a successful OrderResponseDto with the given order ID and message.
   *
   * @param orderId the ID of the successfully created order
   * @param message a message providing additional information about the successful operation
   * @return an OrderResponseDto representing a successful order operation
   */
  public static OrderResponseDto success(Long orderId, String message) {
    return new OrderResponseDto(
        OrderStatus.SUCCESS,
        orderId,
        message,
        Collections.emptyList()
    );
  }

  /**
   * Creates a failed OrderResponseDto with the given message and list of missing items.
   *
   * @param message      a message providing additional information about the failure
   * @param missingItems a list of items that were missing in the order
   * @return an OrderResponseDto representing a failed order operation
   */
  public static OrderResponseDto failed(String message, List<MissingItemDto> missingItems) {
    return new OrderResponseDto(
        OrderStatus.FAIL,
        null,
        message,
        missingItems
    );
  }
}

