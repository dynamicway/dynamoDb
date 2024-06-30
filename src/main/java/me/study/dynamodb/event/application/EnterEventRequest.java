package me.study.dynamodb.event.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.study.dynamodb.event.domain.EventPrize;

@Getter
@RequiredArgsConstructor
public class EnterEventRequest {
    private final long userId;
    private final EventPrize prize;
}
