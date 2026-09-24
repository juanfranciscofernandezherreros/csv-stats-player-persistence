package com.example.csvstats.service;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.entity.StatsPlayer;
import com.example.csvstats.mapper.StatsPlayerMapper;
import com.example.csvstats.repository.StatsPlayerRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.mockito.Mockito.*;

class StatsPlayerPersistenceServiceTest {
 @Test void upsertsByNaturalKey() {
  var repo=mock(StatsPlayerRepository.class); var mapper=mock(StatsPlayerMapper.class); var service=new StatsPlayerPersistenceService(repo,mapper);
  var value=mock(StatsPlayerValue.class); when(value.getMatchId()).thenReturn("m1"); when(value.getName()).thenReturn("p"); when(value.getTeam()).thenReturn("t");
  var entity=new StatsPlayer(); when(repo.findByMatchIdAndNameAndTeam("m1","p","t")).thenReturn(Optional.of(entity)); when(mapper.toEntity(value,entity)).thenReturn(entity);
  service.persist(value); verify(repo).save(entity);
 }
}
