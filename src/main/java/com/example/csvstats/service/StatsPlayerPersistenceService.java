package com.example.csvstats.service;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.entity.StatsPlayer;
import com.example.csvstats.mapper.StatsPlayerMapper;
import com.example.csvstats.repository.StatsPlayerUpsertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StatsPlayerPersistenceService {
 private final StatsPlayerUpsertRepository repository;
 private final StatsPlayerMapper mapper;

 public StatsPlayerPersistenceService(StatsPlayerUpsertRepository repository, StatsPlayerMapper mapper) {
  this.repository = repository;
  this.mapper = mapper;
 }

 @Transactional
 public void persist(StatsPlayerValue value) {
  repository.upsert(mapper.toEntity(value, new StatsPlayer()));
 }

 @Transactional
 public void persistBatch(List<StatsPlayerValue> values) {
  if (values.isEmpty()) {
   return;
  }
  List<StatsPlayer> players = values.stream()
          .map(value -> mapper.toEntity(value, new StatsPlayer()))
          .toList();
  repository.upsertBatch(players);
 }
}
