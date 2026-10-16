package com.scaffolding.userserviceapi.repos;

import com.scaffolding.userserviceapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
