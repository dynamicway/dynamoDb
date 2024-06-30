package me.study.dynamodb.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventEntry {
    private final long userId;
    private final EventPrize prize;
}
