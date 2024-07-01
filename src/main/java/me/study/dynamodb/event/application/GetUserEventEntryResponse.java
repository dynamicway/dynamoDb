package me.study.dynamodb.event.application;

import me.study.dynamodb.event.domain.EventPrize;

public record GetUserEventEntryResponse(
        long userId,
        EventPrize prize
) {
}
