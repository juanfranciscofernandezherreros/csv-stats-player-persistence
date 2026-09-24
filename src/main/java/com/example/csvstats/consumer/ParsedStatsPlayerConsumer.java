package com.example.csvstats.consumer;

import com.example.csvstats.avro.StatsPlayerKey;
import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.service.StatsPlayerPersistenceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ParsedStatsPlayerConsumer {
 private final StatsPlayerPersistenceService service;
 public ParsedStatsPlayerConsumer(StatsPlayerPersistenceService service){this.service=service;}
 @KafkaListener(topics="${app.kafka.topics.parsed-stats-player}",groupId="${spring.kafka.consumer.group-id}")
 public void listen(ConsumerRecord<StatsPlayerKey, StatsPlayerValue> record){ service.persist(record.value()); }
}
