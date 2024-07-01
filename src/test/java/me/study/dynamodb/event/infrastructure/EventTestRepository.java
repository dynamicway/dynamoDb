package me.study.dynamodb.event.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class EventTestRepository {
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTable<EventDynamoDbItem> eventDynamoDbTable;
    private final DynamoDbTable<EntryDynamoDbItem> entryDynamoDbTable;

    public EventTestRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.eventDynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(EventDynamoDbItem.class));
        this.entryDynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(EntryDynamoDbItem.class));

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
        eventDynamoDbTable.putItem(new EventDynamoDbItem(1000));
    }

    public void setCurrentEntrantsToMaximum() {
        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                                                   .tableName("Event")
                                                   .key(Map.of("pk", AttributeValue.fromS("Event"), "sk", AttributeValue.fromS("maximumEntries")))
                                                   .attributeUpdates(Map.of("currentEntries", AttributeValueUpdate.builder().value(AttributeValue.fromS("1000")).build()))
                                                   .build());
    }


    public List<EntryDynamoDbItem> getEntriesByUserId() {
        return entryDynamoDbTable.scan(ScanEnhancedRequest.builder()
                                                          .filterExpression(Expression.builder()
                                                                                      .expression("begins_with(pk, :val)")
                                                                                      .putExpressionValue(":val", AttributeValue.fromS("u"))
                                                                                      .build())
                                                          .consistentRead(true)
                                                          .build())
                                 .items().stream()
                                 .toList();
    }
}
