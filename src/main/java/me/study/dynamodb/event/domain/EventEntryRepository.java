package me.study.dynamodb.event.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Repository
@RequiredArgsConstructor
public class EventEntryRepository {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    void register(EventEntry entry) {

    }

}
