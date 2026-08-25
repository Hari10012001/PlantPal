package com.plantpal.repository;

import com.plantpal.dto.response.AdminUserResponse;
import com.plantpal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByOrderByIdAsc();

    @Query("SELECT new com.plantpal.dto.response.AdminUserResponse(" +
           "u.id, u.fullName, u.email, u.role, COUNT(p), u.createdAt) " +
           "FROM User u LEFT JOIN Plant p ON p.user = u " +
           "GROUP BY u.id, u.fullName, u.email, u.role, u.createdAt " +
           "ORDER BY u.id ASC")
    List<AdminUserResponse> findAllUsersWithPlantCount();
}