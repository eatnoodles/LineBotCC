/**
 * 
 */
package com.cc.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cc.entity.WoWCharacterMapping;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
@Repository
public interface WoWCharacterMappingDao extends CrudRepository<WoWCharacterMapping, String> {

	@Query("select a from WoWCharacterMapping a where a.name = :name and a.realm = :realm")
	public WoWCharacterMapping findCharacterByName(@Param("name")String name, @Param("realm")String realm);
	
}
