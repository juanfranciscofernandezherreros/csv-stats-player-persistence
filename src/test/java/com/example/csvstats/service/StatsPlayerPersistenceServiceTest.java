package com.example.csvstats.service;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.entity.StatsPlayer;
import com.example.csvstats.mapper.StatsPlayerMapper;
import com.example.csvstats.repository.StatsPlayerUpsertRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatsPlayerPersistenceServiceTest {

 @Test
 void upsertsByNaturalKeyAtomically() {
  var repo = mock(StatsPlayerUpsertRepository.class);
  var mapper = mock(StatsPlayerMapper.class);
  var service = new StatsPlayerPersistenceService(repo, mapper);
  var value = mock(StatsPlayerValue.class);
  var entity = new StatsPlayer();

  when(mapper.toEntity(eq(value), any(StatsPlayer.class))).thenReturn(entity);

  service.persist(value);

  verify(repo).upsert(entity);
 }
}
