package com.cc.dao.irol;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cc.entity.irol.MasterCards;
import com.cc.entity.key.MasterCardsKey;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
@Repository
public interface IMasterCardsDao extends CrudRepository<MasterCards, MasterCardsKey> {

	@Query("select a from MasterCards a where a.id.lineId = :lineId")
	public List<MasterCards> findIrolsByLineId(@Param("lineId")String lineId);
}
