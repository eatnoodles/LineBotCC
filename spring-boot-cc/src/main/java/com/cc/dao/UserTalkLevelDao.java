/**
 * 
 */
package com.cc.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cc.entity.UserTalkLevel;
import com.cc.entity.key.UserTalkLevelKey;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
@Repository
public interface UserTalkLevelDao extends CrudRepository<UserTalkLevel, UserTalkLevelKey> {

}
