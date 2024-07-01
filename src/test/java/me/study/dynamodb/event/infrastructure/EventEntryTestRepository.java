package me.study.dynamodb.event.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(EventEntryTestRepository.class);
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
            entryDynamoDbTable.deleteTable();
        } catch (Exception e) {
            log.error("delete table has an error.", e);
        }
        try {
            entryDynamoDbTable.createTable();
        } catch (Exception e) {
            log.error("create table has an error.", e);
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

}
