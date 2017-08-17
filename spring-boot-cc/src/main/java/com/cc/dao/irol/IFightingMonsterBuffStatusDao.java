package com.cc.dao.irol;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cc.entity.irol.FightingMonsterBuffStatus;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
@Repository
public interface IFightingMonsterBuffStatusDao extends CrudRepository<FightingMonsterBuffStatus, Long> {

}
