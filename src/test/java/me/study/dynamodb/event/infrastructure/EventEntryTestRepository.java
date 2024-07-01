package me.study.dynamodb.event.infrastructure;

import me.study.dynamodb.event.domain.EventEntry;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.Map;

@Repository
public class EventEntryTestRepository {
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTable<EventDynamoDbTable> eventDynamoDbTable;
    private final DynamoDbTable<EntryDynamoDbTable> entryDynamoDbTable;

    public EventEntryTestRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.eventDynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(EventDynamoDbTable.class));
        this.entryDynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(EntryDynamoDbTable.class));

        createTable();
    }

    private void createTable() {
        try {
            eventDynamoDbTable.deleteTable();
        } catch (Exception e) {

        }
        try {
            eventDynamoDbTable.createTable();
        } catch (Exception e) {

        }
    }

    public void clear() {
        createTable();
        eventDynamoDbTable.putItem(new EventDynamoDbTable(1000));
    }

    public void setCurrentEntrantsToMaximum() {
        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                                                   .tableName("Event")
                                                   .key(Map.of("pk", AttributeValue.fromS("Event"), "sk", AttributeValue.fromS("maximumEntries")))
                                                   .attributeUpdates(Map.of("currentEntries", AttributeValueUpdate.builder().value(AttributeValue.fromS("1000")).build()))
                                                   .build());
    }

    public void registerEntry(EventEntry eventEntry) {
        entryDynamoDbTable.putItem(new EntryDynamoDbTable(eventEntry.getUserId(), eventEntry.getPrize()));
    }
}
