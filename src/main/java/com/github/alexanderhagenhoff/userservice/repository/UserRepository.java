package com.github.alexanderhagenhoff.userservice.repository;

import com.github.alexanderhagenhoff.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailHash(String emailHash);

    User findByEmailHash(String emailHash);

    Optional<User> findByExternalSubjectId(String externalSubjectId);
}
