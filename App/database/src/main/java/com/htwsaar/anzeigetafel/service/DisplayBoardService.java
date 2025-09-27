package com.htwsaar.anzeigetafel.service;

import com.htwsaar.anzeigetafel.model.DisplayBoard;

import java.util.List;

public interface DisplayBoardService {
    public void crateBoard(DisplayBoard board);
    public void deleteBoard(int id);

    public DisplayBoard getBoard(String boardName);
    public DisplayBoard getBoard(int boardName);

    public List<DisplayBoard> getAllBoards();


}
