package me.study.dynamodb.event.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventEntry {
    private final long userId;
    private final EventPrize prize;
}
