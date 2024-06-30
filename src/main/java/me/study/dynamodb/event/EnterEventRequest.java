package me.study.dynamodb.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnterEventRequest {
    private final long userId;
    private final EventPrize prize;
}
