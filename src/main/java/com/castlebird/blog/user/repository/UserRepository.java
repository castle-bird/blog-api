package com.castlebird.blog.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.castlebird.blog.user.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);
}
