package com.example.csvstats.mapper;

import com.example.csvstats.avro.StatsPlayerValue;
import com.example.csvstats.entity.StatsPlayer;
import org.springframework.stereotype.Component;

@Component
public class StatsPlayerMapper {
 public StatsPlayer toEntity(StatsPlayerValue v, StatsPlayer target) {
  target.setSourceEventId(v.getSourceEventId()); target.setMatchId(v.getMatchId()); target.setName(v.getName()); target.setTeam(v.getTeam());
  target.setPts(v.getPts()); target.setReb(v.getReb()); target.setAst(v.getAst()); target.setMin(v.getMin());
  target.setFgm(v.getFgm()); target.setFga(v.getFga()); target.setTwopm(v.getTwopm()); target.setTwopa(v.getTwopa());
  target.setThreepm(v.getThreepm()); target.setThreepa(v.getThreepa()); target.setFtm(v.getFtm()); target.setFta(v.getFta());
  target.setPlusMinus(v.getPlusMinus()); target.setOr(v.getOr()); target.setDr(v.getDr()); target.setPf(v.getPf()); target.setSt(v.getSt());
  target.setTo(v.getTo()); target.setBs(v.getBs()); target.setBa(v.getBa()); target.setTfs(v.getTfs());
  return target;
 }
}
