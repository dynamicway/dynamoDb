package me.study.dynamodb.event.infrastructure;

import lombok.extern.slf4j.Slf4j;
import me.study.dynamodb.event.domain.EnterEventException;
import me.study.dynamodb.event.domain.EventEntry;
import me.study.dynamodb.event.domain.EventRepository;
import me.study.dynamodb.event.domain.EventPrize;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactUpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class EventDynamoDbRepository implements EventRepository {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<EventDynamoDbItem> eventDynamoDbTable;
    private final DynamoDbTable<EntryDynamoDbItem> entryDynamoDbTable;

    private EventDynamoDbRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.eventDynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(EventDynamoDbItem.class));
        this.entryDynamoDbTable = dynamoDbEnhancedClient.table("Event", TableSchema.fromBean(EntryDynamoDbItem.class));
    }

    @Override
    public void register(EventEntry entry) {
        TransactUpdateItemEnhancedRequest<EventDynamoDbItem> updateStockRequest = TransactUpdateItemEnhancedRequest.builder(EventDynamoDbItem.class)
                                                                                                                   .item(new EventDynamoDbItem())
                                                                                                                   .ignoreNulls(true)
                                                                                                                   .conditionExpression(Expression.builder().expression("currentEntries < maximumEntries").build())
                                                                                                                   .build();
        TransactPutItemEnhancedRequest<EntryDynamoDbItem> registerEntryRequest = TransactPutItemEnhancedRequest.builder(EntryDynamoDbItem.class)
                                                                                                               .item(new EntryDynamoDbItem(entry.getUserId(), entry.getPrize()))
                                                                                                               .conditionExpression(Expression.builder().expression("attribute_not_exists(pk) AND attribute_not_exists(sk)").build())
                                                                                                               .build();
        try {
            dynamoDbEnhancedClient.transactWriteItems(t -> t.addUpdateItem(eventDynamoDbTable, updateStockRequest)
                                                            .addPutItem(entryDynamoDbTable, registerEntryRequest));
        } catch (TransactionCanceledException e) {
            log.error("Entry register has an error.", e);
            // CancellationReasons의 순서는 Transaction 연산 순서와 같음. (https://docs.aws.amazon.com/ko_kr/amazondynamodb/latest/APIReference/API_TransactWriteItems.html)
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
        return Optional.ofNullable(entryDynamoDbTable.getItem(new EntryDynamoDbItem(userId)))
                       .map(i -> new EventEntry(userId, i.getPrize()));
    }

    @Override
    public List<EventEntry> getEntriesByPrize(EventPrize prize) {
        SdkIterable<Page<EntryDynamoDbItem>> pagedResult = entryDynamoDbTable.index("prize").query(QueryEnhancedRequest.builder()
                                                                                                                       .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                                                                                                                                                                         .partitionValue(prize.name())
                                                                                                                                                                         .build()))
                                                                                                                       .build());

        return pagedResult.stream()
                          .flatMap(page -> page.items().stream()
                                               .map(item -> new EventEntry(item.getUserId(), item.getPrize())))
                          .toList();
    }

}
