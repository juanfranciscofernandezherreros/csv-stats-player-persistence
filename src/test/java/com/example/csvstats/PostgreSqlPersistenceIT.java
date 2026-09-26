package com.example.csvstats;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.repository.StatsPlayerRepository;
import com.example.csvstats.service.StatsPlayerPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(properties={
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.properties.schema.registry.url=mock://stats-player-it"
})
class PostgreSqlPersistenceIT {

 @Container
 static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
         .withDatabaseName("stats").withUsername("stats").withPassword("stats");

 @DynamicPropertySource
 static void props(DynamicPropertyRegistry r) {
  r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
  r.add("spring.datasource.username", POSTGRES::getUsername);
  r.add("spring.datasource.password", POSTGRES::getPassword);
 }

 @Autowired StatsPlayerPersistenceService service;
 @Autowired StatsPlayerRepository repository;

 @BeforeEach
 void cleanDatabase() {
  repository.deleteAll();
 }

 @Test
 void insertAndReimportUpdateSameNaturalKey() {
  service.persist(value("e1",10));
  service.persist(value("e2",20));

  assertEquals(1, repository.count());
  var saved = repository.findByMatchIdAndNameAndTeam("m1","Player","Team").orElseThrow();
  assertEquals(20, saved.getPts());
  assertEquals("e2", saved.getSourceEventId());
 }

 @Test
 void concurrentRedeliveryKeepsSinglePlayerRow() throws Exception {
  StatsPlayerValue event = value("e-concurrent", 30);
  CountDownLatch start = new CountDownLatch(1);
  ExecutorService executor = Executors.newFixedThreadPool(2);
  try {
   Future<?> first = executor.submit(() -> persistAfter(start, event));
   Future<?> second = executor.submit(() -> persistAfter(start, event));
   start.countDown();
   first.get();
   second.get();

   assertEquals(1, repository.count());
   var saved = repository.findByMatchIdAndNameAndTeam("m1","Player","Team").orElseThrow();
   assertEquals(30, saved.getPts());
   assertEquals("e-concurrent", saved.getSourceEventId());
  } finally {
   executor.shutdownNow();
  }
 }

 private void persistAfter(CountDownLatch start, StatsPlayerValue event) {
  try {
   start.await();
   service.persist(event);
  } catch (InterruptedException interrupted) {
   Thread.currentThread().interrupt();
   throw new IllegalStateException(interrupted);
  }
 }

 private StatsPlayerValue value(String eventId,int pts) {
  return StatsPlayerValue.newBuilder()
          .setSourceEventId(eventId)
          .setMatchId("m1")
          .setName("Player")
          .setTeam("Team")
          .setPts(pts)
          .setMin("30:00")
          .build();
 }
}
