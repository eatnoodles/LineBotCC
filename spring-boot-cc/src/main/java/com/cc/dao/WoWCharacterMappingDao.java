/**
 * 
 */
package com.cc.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.cc.entity.WoWCharacterMapping;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
public interface WoWCharacterMappingDao extends CrudRepository<WoWCharacterMapping, String> {

}
