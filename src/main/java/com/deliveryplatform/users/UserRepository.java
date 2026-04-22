package com.deliveryplatform.users;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findById(UUID id);

    @EntityGraph(attributePaths = {"profile"})
    List<User> findAll();

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
