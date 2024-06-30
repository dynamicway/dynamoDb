package me.study.dynamodb.event.infrastructure;

import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@DynamoDbBean
@NoArgsConstructor
public class EventDynamoDbTable {
    private long maximumEntries;
    private long currentEntries;
    private String partitionKey;
    private String sortKey;

    public EventDynamoDbTable(long maximumEntries, long currentEntries) {
        this.maximumEntries = maximumEntries;
        this.currentEntries = currentEntries;
        this.partitionKey = "Event";
        this.sortKey = "maxEntries";
    }

    @DynamoDbAttribute("maximumEntries")
    public long getMaximumEntries() {
        return maximumEntries;
    }

    @DynamoDbAttribute("currentEntries")
    public long getCurrentEntries() {
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
