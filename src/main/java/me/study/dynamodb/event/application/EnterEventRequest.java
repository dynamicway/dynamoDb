package me.study.dynamodb.event.application;

import me.study.dynamodb.event.domain.EventPrize;

public record EnterEventRequest(long userId, EventPrize prize) {
}
