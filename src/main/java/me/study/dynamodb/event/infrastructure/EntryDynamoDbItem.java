package me.study.dynamodb.event.infrastructure;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.study.dynamodb.event.domain.EventPrize;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Getter
@Setter
@NoArgsConstructor
@DynamoDbBean
public class EntryDynamoDbItem {
    private String partitionKey;
    private String sortKey;
    private long userId;
    private EventPrize prize;

    public EntryDynamoDbItem(long userId, EventPrize prize) {
        this.userId = userId;
        this.prize = prize;
    }

    public EntryDynamoDbItem(long userId) {
        this.userId = userId;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("pk")
    public String getPartitionKey() {
        return "u#" + userId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("sk")
    public String getSortKey() {
        return "entry";
    }

    @DynamoDbAttribute("prize")
    @DynamoDbSecondaryPartitionKey(indexNames = "prize")
    public EventPrize getPrize() {
        return prize;
    }

}
