package com.cc.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cc.entity.UserImgFunc;

/**
 * @author Caleb Cheng
 *
 */
@Transactional
@Repository
public interface UserImgFuncDao extends CrudRepository<UserImgFunc, String> {

}
