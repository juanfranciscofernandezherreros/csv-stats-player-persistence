package com.example.csvstats.service;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.entity.StatsPlayer;
import com.example.csvstats.mapper.StatsPlayerMapper;
import com.example.csvstats.repository.StatsPlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatsPlayerPersistenceService {
 private final StatsPlayerRepository repository; private final StatsPlayerMapper mapper;
 public StatsPlayerPersistenceService(StatsPlayerRepository repository, StatsPlayerMapper mapper){this.repository=repository;this.mapper=mapper;}
 @Transactional
 public void persist(StatsPlayerValue value) {
  StatsPlayer entity = repository.findByMatchIdAndNameAndTeam(value.getMatchId(),value.getName(),value.getTeam()).orElseGet(StatsPlayer::new);
  repository.save(mapper.toEntity(value,entity));
 }
}
