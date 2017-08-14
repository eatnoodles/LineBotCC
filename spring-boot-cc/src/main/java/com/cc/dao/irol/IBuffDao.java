package com.cc.dao.irol;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cc.entity.irol.Buff;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
@Repository
public interface IBuffDao extends CrudRepository<Buff, Long> {

}
