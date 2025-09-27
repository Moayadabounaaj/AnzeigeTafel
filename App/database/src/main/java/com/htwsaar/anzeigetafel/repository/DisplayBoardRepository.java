package com.htwsaar.anzeigetafel.repository;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayBoardRepository extends JpaRepository<DisplayBoard, Integer> {

}
