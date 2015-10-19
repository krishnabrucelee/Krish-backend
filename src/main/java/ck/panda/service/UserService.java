package ck.panda.service;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.User;
import ck.panda.util.domain.CRUDService;

/**
 *
 * The UserService interface used for to perform CRUD operations and basic API's related business logic.
 */

@Service
public interface UserService extends CRUDService<User> {

}
