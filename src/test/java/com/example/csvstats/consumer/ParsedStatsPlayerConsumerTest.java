package com.example.csvstats.consumer;

import com.example.csvstats.avro.StatsPlayerKey;
import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.service.StatsPlayerPersistenceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ParsedStatsPlayerConsumerTest {

    @Test
    void forwardsWholeKafkaPollAsOnePersistenceBatch() {
        StatsPlayerPersistenceService service = mock(StatsPlayerPersistenceService.class);
        ParsedStatsPlayerConsumer consumer = new ParsedStatsPlayerConsumer(service);
        StatsPlayerValue first = mock(StatsPlayerValue.class);
        StatsPlayerValue second = mock(StatsPlayerValue.class);

        ConsumerRecord<StatsPlayerKey, StatsPlayerValue> firstRecord =
                new ConsumerRecord<>("stats-player.parsed", 0, 0L, null, first);
        ConsumerRecord<StatsPlayerKey, StatsPlayerValue> secondRecord =
                new ConsumerRecord<>("stats-player.parsed", 0, 1L, null, second);

        consumer.listen(List.of(firstRecord, secondRecord));

        verify(service).persistBatch(List.of(first, second));
    }
}
