package com.htwsaar.anzeigetafel.service;

import com.htwsaar.anzeigetafel.model.DisplayBoard;
import com.htwsaar.anzeigetafel.model.User;
import com.htwsaar.anzeigetafel.repository.DisplayBoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisplayBoardServiceImp implements DisplayBoardService {

    private DisplayBoardRepository displayBoard;

    /**
     * Constructor
     * @param displayBoard
     */
    @Autowired
    public DisplayBoardServiceImp(DisplayBoardRepository displayBoard) {
        this.displayBoard = displayBoard;
    }


    /**
     * Create a new board
     * @param board
     */
    @Override
    public void crateBoard(DisplayBoard board) {
        displayBoard.save(board);
    }

    /**
     * Delete a board
     * @param id
     */
    @Override
    public void deleteBoard(int id) {
        displayBoard.deleteById(id);
    }
    /**
     * Get a board by id
     * @param boardName
     * @return DisplayBoard
     */
    @Override
    public DisplayBoard getBoard(String boardName) {
        if (boardName == null)
            return null;

        List<DisplayBoard> boards = displayBoard.findAll();
        if (boards == null)
            return null;

        Optional<DisplayBoard> boardOptional = boards.stream()
                .filter(a -> a.getBoardName() != null && a.getBoardName().equalsIgnoreCase(boardName))
                .findFirst();

        return boardOptional.orElse(null); // Return null if board not found
    }
    /**
     * Get a board by id
     * @param boardId
     * @return DisplayBoard
     */
    @Override
    public DisplayBoard getBoard(int boardId) {
        DisplayBoard board = displayBoard.findAll().stream().filter(a-> a.getBoardID() == boardId).findFirst().get();
        return board;
    }

    /**
     * Get all boards
     * @return List DisplayBoard
     */
    @Override
    public List<DisplayBoard> getAllBoards() {
        return displayBoard.findAll();
    }
}
