package me.study.dynamodb.event.infrastructure;

import lombok.extern.slf4j.Slf4j;
import me.study.dynamodb.event.domain.EnterEventException;
import me.study.dynamodb.event.domain.EventEntry;
import me.study.dynamodb.event.domain.EventEntryRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactUpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValuesOnConditionCheckFailure;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.util.Optional;

@Repository
@Slf4j
public class EventEntryDynamoDbRepository implements EventEntryRepository {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<EventDynamoDbTable> eventDynamoDbTable;
    private final DynamoDbTable<EntryDynamoDbTable> entryDynamoDbTable;

    private EventEntryDynamoDbRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.eventDynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(EventDynamoDbTable.class));
        this.entryDynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(EntryDynamoDbTable.class));
    }

    @Override
    public void register(EventEntry entry) {
        TransactUpdateItemEnhancedRequest<EventDynamoDbTable> updateStockRequest = TransactUpdateItemEnhancedRequest.builder(EventDynamoDbTable.class)
                                                                                                                    .item(new EventDynamoDbTable())
                                                                                                                    .ignoreNulls(true)
                                                                                                                    .conditionExpression(Expression.builder().expression("currentEntries < maximumEntries").build())
                                                                                                                    .returnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD)
                                                                                                                    .build();
        TransactPutItemEnhancedRequest<EntryDynamoDbTable> registerEntryRequest = TransactPutItemEnhancedRequest.builder(EntryDynamoDbTable.class)
                                                                                                                .item(new EntryDynamoDbTable(entry.getUserId(), entry.getPrize()))
                                                                                                                .conditionExpression(Expression.builder().expression("attribute_not_exists(pk) AND attribute_not_exists(sk)").build())
                                                                                                                .returnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD)
                                                                                                                .build();
        try {
            dynamoDbEnhancedClient.transactWriteItems(t -> t.addUpdateItem(eventDynamoDbTable, updateStockRequest)
                                                            .addPutItem(entryDynamoDbTable, registerEntryRequest));
        } catch (TransactionCanceledException e) {
            for (int i = 0; i < e.cancellationReasons().size(); i++) {
                if (e.cancellationReasons().get(i).code().equals("ConditionalCheckFailed")) {
                    if (i == 0) {
                        throw new EnterEventException("Reached the maximum entries.");
                    } else if (i == 1) {
                        throw new EnterEventException("Already entered.");
                    }
                }
            }
        }
    }

    @Override
    public Optional<EventEntry> getEntryByUserId(long userId) {
        return Optional.ofNullable(entryDynamoDbTable.getItem(new EntryDynamoDbTable(userId)))
                       .map(i -> new EventEntry(userId, i.getPrize()));
    }

}
