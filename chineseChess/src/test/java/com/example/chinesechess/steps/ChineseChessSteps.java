package com.example.chinesechess.steps;

import com.example.chinesechess.Board;
import com.example.chinesechess.Piece;
import com.example.chinesechess.Piece.Color;
import com.example.chinesechess.Piece.Type;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ChineseChessSteps {

    private Board board;
    private int startRow, startCol, endRow, endCol;

    @Given("the board is empty except for a Red General at \\({int}, {int})")
    public void the_board_is_empty_except_for_a_red_general_at(int row, int col) {
        board = new Board();
        board.clearBoard();
        board.setPiece(new Piece(Type.GENERAL, Color.RED), row, col);
    }

    @Given("the board is empty except for a Red Rook at \\({int}, {int})")
    public void the_board_is_empty_except_for_a_red_rook_at(int row, int col) {
        board = new Board();
        board.clearBoard();
        board.setPiece(new Piece(Type.ROOK, Color.RED), row, col);
    }

    @When("Red moves the General from \\({int}, {int}) to \\({int}, {int})")
    public void red_moves_the_general_from_to(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        board.movePiece(startRow, startCol, endRow, endCol);
    }

    @When("Red moves the Rook from \\({int}, {int}) to \\({int}, {int})")
    public void red_moves_the_rook_from_to(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        board.movePiece(startRow, startCol, endRow, endCol);
    }

    @Given("the board is empty except for a Red Guard at \\({int}, {int})")
    public void the_board_is_empty_except_for_a_red_guard_at(int row, int col) {
        board = new Board();
        board.clearBoard();
        board.setPiece(new Piece(Type.GUARD, Color.RED), row, col);
    }

    @When("Red moves the Guard from \\({int}, {int}) to \\({int}, {int})")
    public void red_moves_the_guard_from_to(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        board.movePiece(startRow, startCol, endRow, endCol);
    }

    @Given("the board is empty except for a Red Horse at \\({int}, {int})")
    public void the_board_is_empty_except_for_a_red_horse_at(int row, int col) {
        board = new Board();
        board.clearBoard();
        board.setPiece(new Piece(Type.HORSE, Color.RED), row, col);
    }

    @When("Red moves the Horse from \\({int}, {int}) to \\({int}, {int})")
    public void red_moves_the_horse_from_to(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        board.movePiece(startRow, startCol, endRow, endCol);
    }

    @Given("the board is empty except for a Red Elephant at \\({int}, {int})")
    public void the_board_is_empty_except_for_a_red_elephant_at(int row, int col) {
        board = new Board();
        board.clearBoard();
        board.setPiece(new Piece(Type.ELEPHANT, Color.RED), row, col);
    }

    @When("Red moves the Elephant from \\({int}, {int}) to \\({int}, {int})")
    public void red_moves_the_elephant_from_to(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        board.movePiece(startRow, startCol, endRow, endCol);
    }

    @Given("the board is empty except for a Red Cannon at \\({int}, {int})")
    public void the_board_is_empty_except_for_a_red_cannon_at(int row, int col) {
        board = new Board();
        board.clearBoard();
        board.setPiece(new Piece(Type.CANNON, Color.RED), row, col);
    }

    @Given("the board is empty except for a Red Soldier at \\({int}, {int})")
    public void the_board_is_empty_except_for_a_red_soldier_at(int row, int col) {
        board = new Board();
        board.clearBoard();
        board.setPiece(new Piece(Type.SOLDIER, Color.RED), row, col);
    }

    @When("Red moves the Cannon from \\({int}, {int}) to \\({int}, {int})")
    public void red_moves_the_cannon_from_to(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        board.movePiece(startRow, startCol, endRow, endCol);
    }

    @When("Red moves the Soldier from \\({int}, {int}) to \\({int}, {int})")
    public void red_moves_the_soldier_from_to(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        board.movePiece(startRow, startCol, endRow, endCol);
    }

    @Then("the move is legal")
    public void the_move_is_legal() {
        assertTrue(board.isMoveLegal());
    }

    @Then("the move is illegal")
    public void the_move_is_illegal() {
        assertFalse(board.isMoveLegal());
    }

    @Then("the move is a legal capture")
    public void the_move_is_a_legal_capture() {
        assertTrue(board.isMoveLegal());
        assertTrue(board.isCapture());
    }

    @Then("the move is illegal because it is not a capture and the path is blocked")
    public void the_move_is_illegal_because_it_is_not_a_capture_and_the_path_is_blocked() {
        assertFalse(board.isMoveLegal());
    }

    @Then("the Black General is in check")
    public void the_black_general_is_in_check() {
        assertTrue(board.isGeneralInCheck(Piece.Color.BLACK));
    }

    @Then("Black is in checkmate and Red wins")
    public void black_is_in_checkmate_and_red_wins() {
        assertTrue(board.isCheckmate(Piece.Color.BLACK));
    }

    @Given("the board has:")
    public void the_board_has(io.cucumber.datatable.DataTable dataTable) {
        board = new Board();
        board.clearBoard();
        for (java.util.Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            String pieceType = row.get("Piece");
            String position = row.get("Position"); // e.g., "(2, 4)"

            // Parse piece type and color
            String[] pieceParts = pieceType.split(" ");
            Color color = Color.valueOf(pieceParts[0].toUpperCase());
            Type type = Type.valueOf(pieceParts[1].toUpperCase());

            // Parse position
            String[] posParts = position.replaceAll("[()]", "").split(", ");
            int rowNum = Integer.parseInt(posParts[0]);
            int colNum = Integer.parseInt(posParts[1]);

            board.setPiece(new Piece(type, color), rowNum, colNum);
        }
    }
}
