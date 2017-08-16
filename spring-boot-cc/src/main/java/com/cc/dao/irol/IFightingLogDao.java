package com.cc.dao.irol;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cc.entity.irol.FightingLog;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
@Repository
public interface IFightingLogDao extends CrudRepository<FightingLog, Long> {

	@Query("select 1 a from FightingLog a where a.lineId = :lineId and a.irol.id = :irolId and a.monster.id = :monsterId and a.status = 1 order by lastDttm desc")
	public FightingLog findLastLog(@Param("lineId")String lineId, @Param("irolId")Long irolId, @Param("monsterId")Long monsterId);

}
