package com.example.csvstats;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.entity.StatsPlayer;
import com.example.csvstats.repository.StatsPlayerRepository;
import com.example.csvstats.repository.StatsPlayerUpsertRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
 @Autowired StatsPlayerUpsertRepository upserts;

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

 @Test
 void measuresSequentialVersusJdbcBatchThroughput() {
  List<StatsPlayer> players = benchmarkPlayers(1_000);

  long sequentialStart = System.nanoTime();
  players.forEach(upserts::upsert);
  long sequentialNanos = System.nanoTime() - sequentialStart;
  assertEquals(players.size(), repository.count());

  repository.deleteAll();

  long batchStart = System.nanoTime();
  upserts.upsertBatch(players);
  long batchNanos = System.nanoTime() - batchStart;
  assertEquals(players.size(), repository.count());

  double sequentialThroughput = throughput(players.size(), sequentialNanos);
  double batchThroughput = throughput(players.size(), batchNanos);

  System.out.printf(
          "KAN-22 PLAYER throughput: sequential=%.2f rows/s, jdbc-batch=%.2f rows/s, ratio=%.2fx%n",
          sequentialThroughput,
          batchThroughput,
          batchThroughput / sequentialThroughput
  );

  assertTrue(sequentialThroughput > 0);
  assertTrue(batchThroughput > 0);
 }

 private double throughput(int rows, long nanos) {
  return rows / (nanos / 1_000_000_000.0);
 }

 private List<StatsPlayer> benchmarkPlayers(int count) {
  List<StatsPlayer> players = new ArrayList<>(count);
  for (int index = 0; index < count; index++) {
   StatsPlayer player = new StatsPlayer();
   player.setSourceEventId("benchmark-event");
   player.setMatchId("benchmark-match");
   player.setName("Player-" + index);
   player.setTeam(index % 2 == 0 ? "Home" : "Away");
   player.setPts(index % 40);
   player.setMin("20:00");
   players.add(player);
  }
  return players;
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
