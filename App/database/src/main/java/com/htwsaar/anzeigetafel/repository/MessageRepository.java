package com.htwsaar.anzeigetafel.repository;

import com.htwsaar.anzeigetafel.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface MessageRepository extends JpaRepository<Message, Integer> {

}
