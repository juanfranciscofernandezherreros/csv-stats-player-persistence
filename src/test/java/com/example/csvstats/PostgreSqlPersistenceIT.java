package com.example.csvstats;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.repository.StatsPlayerRepository;
import com.example.csvstats.service.StatsPlayerPersistenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(properties={"spring.kafka.listener.auto-startup=false","spring.kafka.bootstrap-servers=localhost:9092","spring.kafka.properties.schema.registry.url=mock://stats-player-it"})
class PostgreSqlPersistenceIT {
 @Container static final PostgreSQLContainer<?> POSTGRES=new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("stats").withUsername("stats").withPassword("stats");
 @DynamicPropertySource static void props(DynamicPropertyRegistry r){r.add("spring.datasource.url",POSTGRES::getJdbcUrl);r.add("spring.datasource.username",POSTGRES::getUsername);r.add("spring.datasource.password",POSTGRES::getPassword);}
 @Autowired StatsPlayerPersistenceService service; @Autowired StatsPlayerRepository repository;
 @Test void migrationAndUpsertWork(){
  service.persist(value("e1",10)); service.persist(value("e2",20));
  assertEquals(1,repository.count());
  var saved=repository.findByMatchIdAndNameAndTeam("m1","Player","Team").orElseThrow();
  assertEquals(20,saved.getPts()); assertEquals("e2",saved.getSourceEventId());
 }
 private StatsPlayerValue value(String eventId,int pts){
  return StatsPlayerValue.newBuilder().setSourceEventId(eventId).setMatchId("m1").setName("Player").setTeam("Team").setPts(pts).setMin("30:00").build();
 }
}
