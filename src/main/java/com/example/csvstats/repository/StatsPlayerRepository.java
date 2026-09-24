package com.example.csvstats.repository;

import com.example.csvstats.entity.StatsPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StatsPlayerRepository extends JpaRepository<StatsPlayer,Long> {
 Optional<StatsPlayer> findByMatchIdAndNameAndTeam(String matchId,String name,String team);
}
