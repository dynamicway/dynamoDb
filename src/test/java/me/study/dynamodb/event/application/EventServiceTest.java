package me.study.dynamodb.event.application;

import me.study.dynamodb.event.domain.EnterEventException;
import me.study.dynamodb.event.domain.EventPrize;
import me.study.dynamodb.event.infrastructure.EntryDynamoDbItem;
import me.study.dynamodb.event.infrastructure.EventDynamoDbItem;
import me.study.dynamodb.event.infrastructure.EventTestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Same;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class EventServiceTest {
    @Autowired
    private EventTestRepository eventTestRepository;

    @Autowired
    private EventService sut;

    @BeforeEach
    void setUp() {
        eventTestRepository.clear();
    }

    @Test
    void can_not_enter_if_reached_the_maximum_entries() {
        eventTestRepository.setCurrentEntrantsToMaximum();
        long userId = 1L;

        assertThatThrownBy(() -> sut.enterEvent(new EnterEventRequest(userId, EventPrize.COUPON)))
                .isInstanceOf(EnterEventException.class)
                .hasMessage("Reached the maximum entries.");
        assertThat(sut.getUserEventEntry(userId)).isNull();
    }

    @Test
    void can_not_enter_if_already_entered() {
        long userId = 1L;
        EventPrize originallyEnteredPrize = EventPrize.POINT;
        sut.enterEvent(new EnterEventRequest(userId, originallyEnteredPrize));

        assertThatThrownBy(() -> sut.enterEvent(new EnterEventRequest(userId, EventPrize.COUPON)))
                .isInstanceOf(EnterEventException.class)
                .hasMessage("Already entered.");
        assertThat(sut.getUserEventEntry(userId)).isEqualTo(new GetUserEventEntryResponse(userId, originallyEnteredPrize));
    }

    @Test
    void enter_event() {
        long userId = 1L;
        EventPrize prize = EventPrize.POINT;

        sut.enterEvent(new EnterEventRequest(userId, prize));

        assertThat(sut.getUserEventEntry(userId)).isEqualTo(new GetUserEventEntryResponse(userId, prize));
    }

    @Test
    void multiple_entries_at_the_same_time_will_only_be_entered_once() {
        long userId = 1L;
        EventPrize prize = EventPrize.POINT;

        runParallelTasks(() -> sut.enterEvent(new EnterEventRequest(userId, prize)), 100);

        List<EntryDynamoDbItem> entries = eventTestRepository.getEntriesByUserId();
        EventDynamoDbItem event = eventTestRepository.getEvent();
        assertThat(entries).hasSize(1);
        assertThat(entries.getFirst()).satisfies(entry -> {
            assertThat(entry.getUserId()).isEqualTo(userId);
            assertThat(entry.getPrize()).isEqualTo(prize);
        });
        assertThat(event.getCurrentEntries()).isOne();
    }

    private void runParallelTasks(Runnable runnable, int taskCount) {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        for (int i = 0; i < taskCount; i++) {
            executorService.submit(() -> {
                try {
                    runnable.run();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void the_maximum_entry_limit_is_met_even_if_multiple_people_enter_at_the_same_time() {
        AtomicLong atomicLong = new AtomicLong();

        runParallelTasks(() -> sut.enterEvent(new EnterEventRequest(atomicLong.incrementAndGet(), EventPrize.COUPON)), 2000);

        List<EntryDynamoDbItem> entries = eventTestRepository.getEntriesByUserId();
        EventDynamoDbItem event = eventTestRepository.getEvent();
        assertThat(entries).hasSize(1000);
        assertThat(event.getCurrentEntries()).isEqualTo(1000);
    }

    @Test
    void get_user_event_entry() {
        long userId = 1L;
        EnterEventRequest enterEventRequest = new EnterEventRequest(userId, EventPrize.COUPON);
        sut.enterEvent(enterEventRequest);

        assertThat(sut.getUserEventEntry(userId)).isEqualTo(new GetUserEventEntryResponse(userId, EventPrize.COUPON));
    }

    @Test
    void if_there_is_no_entry_history_get_null() {
        assertThat(sut.getUserEventEntry(1L)).isNull();
    }

    @Test
    void get_entries_by_prize() {
        sut.enterEvent(new EnterEventRequest(1L, EventPrize.COUPON));
        sut.enterEvent(new EnterEventRequest(3L, EventPrize.COUPON));
        sut.enterEvent(new EnterEventRequest(5L, EventPrize.COUPON));
        sut.enterEvent(new EnterEventRequest(7L, EventPrize.COUPON));
        sut.enterEvent(new EnterEventRequest(2L, EventPrize.POINT));
        sut.enterEvent(new EnterEventRequest(4L, EventPrize.POINT));
        sut.enterEvent(new EnterEventRequest(6L, EventPrize.POINT));

        GetEntrantsByPrizeResponse response = sut.getEntrantsByPrize(EventPrize.POINT);

        assertThat(response.prize()).isEqualTo(EventPrize.POINT);
        assertThat(response.userIds()).containsOnly(2L, 4L, 6L);
    }

}
