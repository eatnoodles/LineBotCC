package com.cc.dao.irol;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

	@Query("select a from FightingLog a where a.lineId = :lineId and a.fightingIrolStatus.irol.id = :irolId and a.fightingMonsterStatus.monster.id = :monsterId and a.status = 1 order by a.lastDttm desc")
	public List<FightingLog> findLog(@Param("lineId")String lineId, @Param("irolId")Long irolId, @Param("monsterId")Long monsterId, Pageable pageable);

	default FightingLog findLastLog(String lineId, Long irolId, Long monsterId) {
		List<FightingLog> logs = findLog(lineId, irolId, monsterId, new PageRequest(0, 1));
		if (logs == null || logs.size() != 1) {
			return null;
		}
		return logs.get(0);
	}
}
