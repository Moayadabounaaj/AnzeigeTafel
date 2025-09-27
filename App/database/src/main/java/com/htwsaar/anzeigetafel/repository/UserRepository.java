package com.htwsaar.anzeigetafel.repository;

import com.htwsaar.anzeigetafel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
