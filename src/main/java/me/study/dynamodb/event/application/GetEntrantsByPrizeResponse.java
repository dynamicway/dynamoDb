package me.study.dynamodb.event.application;

import me.study.dynamodb.event.domain.EventPrize;

import java.util.List;

public record GetEntrantsByPrizeResponse(
        List<Long> userIds,
        EventPrize prize
) {
}
