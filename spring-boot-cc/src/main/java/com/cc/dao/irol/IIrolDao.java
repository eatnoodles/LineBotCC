package com.cc.dao.irol;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cc.entity.irol.Irol;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
@Repository
public interface IIrolDao extends CrudRepository<Irol, Long> {
	
	@Query("select a from Irol a where a.name = :name")
	public Irol findByName(@Param("name")String name);
	
}
