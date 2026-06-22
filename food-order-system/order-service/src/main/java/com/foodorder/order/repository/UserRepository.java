package com.foodorder.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodorder.order.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
