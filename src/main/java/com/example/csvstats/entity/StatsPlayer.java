package com.example.csvstats.entity;

import jakarta.persistence.*;

@Entity
@Table(name="stats_player", uniqueConstraints=@UniqueConstraint(name="uk_stats_player_match_player_team", columnNames={"match_id","name","team"}))
public class StatsPlayer {
 @Id @SequenceGenerator(name="stats_player_seq_gen",sequenceName="stats_player_seq",allocationSize=100)
 @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="stats_player_seq_gen")
 private Long id;
 @Column(name="match_id",nullable=false) private String matchId;
 @Column(nullable=false) private String name;
 @Column(nullable=false) private String team;
 private Integer pts, reb, ast;
 private String min;
 private Integer fgm, fga, twopm, twopa, threepm, threepa, ftm, fta;
 @Column(name="plus_minus") private Integer plusMinus;
 @Column(name="or") private Integer or;
 private Integer dr, pf, st;
 @Column(name="to") private Integer to;
 private Integer bs, ba, tfs;
 @Column(name="source_event_id",nullable=false) private String sourceEventId;

 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public String getMatchId(){return matchId;} public void setMatchId(String v){matchId=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getTeam(){return team;} public void setTeam(String v){team=v;}
 public Integer getPts(){return pts;} public void setPts(Integer v){pts=v;}
 public Integer getReb(){return reb;} public void setReb(Integer v){reb=v;}
 public Integer getAst(){return ast;} public void setAst(Integer v){ast=v;}
 public String getMin(){return min;} public void setMin(String v){min=v;}
 public Integer getFgm(){return fgm;} public void setFgm(Integer v){fgm=v;}
 public Integer getFga(){return fga;} public void setFga(Integer v){fga=v;}
 public Integer getTwopm(){return twopm;} public void setTwopm(Integer v){twopm=v;}
 public Integer getTwopa(){return twopa;} public void setTwopa(Integer v){twopa=v;}
 public Integer getThreepm(){return threepm;} public void setThreepm(Integer v){threepm=v;}
 public Integer getThreepa(){return threepa;} public void setThreepa(Integer v){threepa=v;}
 public Integer getFtm(){return ftm;} public void setFtm(Integer v){ftm=v;}
 public Integer getFta(){return fta;} public void setFta(Integer v){fta=v;}
 public Integer getPlusMinus(){return plusMinus;} public void setPlusMinus(Integer v){plusMinus=v;}
 public Integer getOr(){return or;} public void setOr(Integer v){or=v;}
 public Integer getDr(){return dr;} public void setDr(Integer v){dr=v;}
 public Integer getPf(){return pf;} public void setPf(Integer v){pf=v;}
 public Integer getSt(){return st;} public void setSt(Integer v){st=v;}
 public Integer getTo(){return to;} public void setTo(Integer v){to=v;}
 public Integer getBs(){return bs;} public void setBs(Integer v){bs=v;}
 public Integer getBa(){return ba;} public void setBa(Integer v){ba=v;}
 public Integer getTfs(){return tfs;} public void setTfs(Integer v){tfs=v;}
 public String getSourceEventId(){return sourceEventId;} public void setSourceEventId(String v){sourceEventId=v;}
}
