package me.study.dynamodb.event.domain;

public interface EventEntryRepository {

    void register(EventEntry entry);

}
