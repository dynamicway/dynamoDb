package me.study.dynamodb.event.infrastructure;

import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAtomicCounter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@NoArgsConstructor
@DynamoDbBean
public class EventDynamoDbItem {
    private String partitionKey = "Event";
    private String sortKey = "maximumEntries";
    private Long maximumEntries;
    private Long currentEntries;

    public EventDynamoDbItem(long maximumEntries) {
        this.maximumEntries = maximumEntries;
    }

    @DynamoDbAttribute("maximumEntries")
    public Long getMaximumEntries() {
        return maximumEntries;
    }

    @DynamoDbAtomicCounter
    @DynamoDbAttribute("currentEntries")
    public Long getCurrentEntries() {
        return currentEntries;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("pk")
    public String getPartitionKey() {
        return partitionKey;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("sk")
    public String getSortKey() {
        return sortKey;
    }

}
