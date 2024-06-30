package me.study.dynamodb.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetUserEventEntriesResponse {
    private final long userId;
    private final EventPrize prizeType;

}
