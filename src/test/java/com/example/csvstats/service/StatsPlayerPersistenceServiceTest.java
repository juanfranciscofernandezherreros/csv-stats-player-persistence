package com.example.csvstats.service;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.entity.StatsPlayer;
import com.example.csvstats.mapper.StatsPlayerMapper;
import com.example.csvstats.repository.StatsPlayerUpsertRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

 @Test
 void mapsAndPersistsOneJdbcBatch() {
  var repo = mock(StatsPlayerUpsertRepository.class);
  var mapper = mock(StatsPlayerMapper.class);
  var service = new StatsPlayerPersistenceService(repo, mapper);
  var firstValue = mock(StatsPlayerValue.class);
  var secondValue = mock(StatsPlayerValue.class);
  var first = new StatsPlayer();
  var second = new StatsPlayer();

  when(mapper.toEntity(eq(firstValue), any(StatsPlayer.class))).thenReturn(first);
  when(mapper.toEntity(eq(secondValue), any(StatsPlayer.class))).thenReturn(second);

  service.persistBatch(List.of(firstValue, secondValue));

  verify(repo).upsertBatch(List.of(first, second));
 }

 @Test
 void emptyBatchDoesNotTouchMapperOrDatabase() {
  var repo = mock(StatsPlayerUpsertRepository.class);
  var mapper = mock(StatsPlayerMapper.class);

  new StatsPlayerPersistenceService(repo, mapper).persistBatch(List.of());

  verifyNoInteractions(repo, mapper);
 }
}
