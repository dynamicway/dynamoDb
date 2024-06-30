package me.study.dynamodb.event.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventEntry {
    private final long userId;
    private final EventPrize prize;
}
