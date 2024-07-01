package me.study.dynamodb.event.domain;

import java.util.List;
import java.util.Optional;

public interface EventRepository {

    void register(EventEntry entry);

    Optional<EventEntry> getEntryByUserId(long userId);

    List<EventEntry> getEntriesByPrize(EventPrize prize);
}
