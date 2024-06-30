package me.study.dynamodb.event.infrastructure;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class EventEntryTestRepository {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<EventDynamoDbTable> eventDynamoDbTable;

    public EventEntryTestRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.eventDynamoDbTable = createTable(EventDynamoDbTable.class);
    }

    private <T> DynamoDbTable<T> createTable(Class<T> tableBeanClass) {
        DynamoDbTable<T> dynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(tableBeanClass));
//        dynamoDbTable.createTable();
        return dynamoDbTable;
    }

    public void setCurrentEntrantsToMaximum() {
        eventDynamoDbTable.putItem(new EventDynamoDbTable(1000, 1000));
    }
}
