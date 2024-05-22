package com.github.emw7.activemqdbauth.activemq.plugin.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// this is implemented by spring when @Enable*Repositories is used and set to point to the package
//  containing this interface (or an ancestor of it)
@Repository
public interface UserRepository extends CrudRepository<User, String> {
}
