package me.study.dynamodb.event.domain;

import java.util.Optional;

public interface EventEntryRepository {

    void register(EventEntry entry);

    Optional<EventEntry> getUserEventEntry(long userId);
}
