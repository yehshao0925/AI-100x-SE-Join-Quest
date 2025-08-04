package com.example.chinesechess;

import java.util.HashMap;
import java.util.Map;
import com.example.chinesechess.Piece.Type;
import com.example.chinesechess.Piece.Color;

public class Board {
    private Map<Position, Piece> pieces;
    private boolean moveLegal;
    private boolean inCheck;
    private boolean inCheckmate;
    private boolean isCapture;

    public Board() {
        this.pieces = new HashMap<>();
        this.moveLegal = false;
        this.inCheck = false;
        this.inCheckmate = false;
        this.isCapture = false;
    }

    public void setPiece(Piece piece, int row, int col) {
        pieces.put(new Position(row, col), piece);
    }

    public Piece getPiece(int row, int col) {
        return pieces.get(new Position(row, col));
    }

    public void clearBoard() {
        pieces.clear();
    }

    public void movePiece(int startRow, int startCol, int endRow, int endCol) {
        Position startPos = new Position(startRow, startCol);
        Position endPos = new Position(endRow, endCol);
        Piece pieceToMove = pieces.get(startPos);

        if (pieceToMove == null) {
            this.moveLegal = false;
            return;
        }

        this.moveLegal = false; // Assume illegal by default
        this.isCapture = false; // Reset capture status for each move

        boolean isBasicMoveLegal = false; // New local variable

        if (pieceToMove.getType() == Type.GENERAL) {
            isBasicMoveLegal = isValidGeneralMove(this.pieces, pieceToMove, startRow, startCol, endRow, endCol, false);
        } else if (pieceToMove.getType() == Type.GUARD) {
            isBasicMoveLegal = isValidGuardMove(this.pieces, pieceToMove, startRow, startCol, endRow, endCol, false);
        } else if (pieceToMove.getType() == Type.ELEPHANT) {
            isBasicMoveLegal = isValidElephantMove(this.pieces, pieceToMove, startRow, startCol, endRow, endCol, false);
        } else if (pieceToMove.getType() == Type.ROOK) {
            // Rook moves any number of spaces orthogonally.
            // Movement can be blocked by intervening pieces.
            boolean isOrthogonal = (startRow == endRow && startCol != endCol) || (startRow != endRow && startCol == endCol);

            if (isOrthogonal) {
                if (isPathClear(this.pieces, startPos, endPos)) { // Pass this.pieces
                    Piece targetPiece = pieces.get(endPos);
                    if (targetPiece == null || targetPiece.getColor() != pieceToMove.getColor()) {
                        isBasicMoveLegal = true; // Set local variable
                    }
                }
            }
        } else if (pieceToMove.getType() == Type.HORSE) {
            // Horse moves one step orthogonally, then one step diagonally outwards.
            // It is blocked if there is a piece at the orthogonal step.
            int rowDiff = endRow - startRow;
            int colDiff = endCol - startCol;

            boolean isLShape = (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1) ||
                               (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2);

            if (isLShape) {
                Position blockPos = null;
                if (Math.abs(rowDiff) == 2) { // Vertical move first
                    blockPos = new Position(startRow + (rowDiff / 2), startCol);
                } else { // Horizontal move first
                    blockPos = new Position(startRow, startCol + (colDiff / 2));
                }

                if (!pieces.containsKey(blockPos)) {
                    Piece targetPiece = pieces.get(endPos);
                    if (targetPiece == null || targetPiece.getColor() != pieceToMove.getColor()) {
                        isBasicMoveLegal = true; // Set local variable
                    }
                }
            }
        } else if (pieceToMove.getType() == Type.CANNON) {
            isBasicMoveLegal = isValidCannonMove(this.pieces, pieceToMove, startRow, startCol, endRow, endCol, false);
        } else if (pieceToMove.getType() == Type.SOLDIER) {
            isBasicMoveLegal = isValidSoldierMove(this.pieces, pieceToMove, startRow, startCol, endRow, endCol, false);
        }

        // After determining if the move is legal based on piece rules,
        // check if the move leaves the current player's General in check.
        if (isBasicMoveLegal) { // Use the local variable here
            // Temporarily apply the move to a copy of the board to check for self-check
            Map<Position, Piece> tempPiecesForSelfCheck = new HashMap<>(this.pieces);
            Piece capturedPiece = tempPiecesForSelfCheck.remove(endPos); // Simulate capture
            tempPiecesForSelfCheck.remove(startPos);
            tempPiecesForSelfCheck.put(endPos, pieceToMove);

            // Find the current player's General
            Position currentPlayerGeneralPos = null;
            for (Map.Entry<Position, Piece> entry : tempPiecesForSelfCheck.entrySet()) {
                if (entry.getValue().getType() == Type.GENERAL && entry.getValue().getColor() == pieceToMove.getColor()) {
                    currentPlayerGeneralPos = entry.getKey();
                    break;
                }
            }

            if (currentPlayerGeneralPos != null && isGeneralInCheck(pieceToMove.getColor(), tempPiecesForSelfCheck)) {
                this.moveLegal = false; // Move is illegal if it leaves own General in check
            } else {
                // If the move is legal and doesn't leave own General in check, then perform the actual move
                if (capturedPiece != null) {
                    this.isCapture = true;
                } else {
                    this.isCapture = false;
                }
                pieces.remove(startPos);
                pieces.put(endPos, pieceToMove);
                this.moveLegal = true; // Set to true only if all checks pass and move is performed
            }
        }
    }

    // Helper method for isGeneralInCheck that takes a custom pieces map
    public boolean isGeneralInCheck(Color generalColor, Map<Position, Piece> currentPieces) {
        Position generalPos = null;
        Color attackingColor = (generalColor == Color.RED) ? Color.BLACK : Color.RED;

        // Find the General's position on the currentPieces map
        for (Map.Entry<Position, Piece> entry : currentPieces.entrySet()) {
            if (entry.getValue().getType() == Type.GENERAL && entry.getValue().getColor() == generalColor) {
                generalPos = entry.getKey();
                break;
            }
        }

        if (generalPos == null) {
            return false; // General not found (should not happen in a valid game state)
        }

        // Check if any opponent piece can attack the General's position on the currentPieces map
        for (Map.Entry<Position, Piece> entry : currentPieces.entrySet()) {
            Piece attackingPiece = entry.getValue();
            Position attackingPiecePos = entry.getKey();

            if (attackingPiece.getColor() == attackingColor) {
                // Temporarily set isCapture to false before checking each potential attack
                // This is important because isValidXMove methods might set it.
                boolean originalIsCapture = this.isCapture; // Store original isCapture
                this.isCapture = false; // Reset for check calculation

                boolean canAttack = false;
                // Need to pass currentPieces to isValidXMove methods
                if (attackingPiece.getType() == Type.GENERAL) {
                    canAttack = isValidGeneralMove(currentPieces, attackingPiece, attackingPiecePos.getRow(), attackingPiecePos.getCol(), generalPos.getRow(), generalPos.getCol(), true);
                } else if (attackingPiece.getType() == Type.GUARD) {
                    canAttack = isValidGuardMove(currentPieces, attackingPiece, attackingPiecePos.getRow(), attackingPiecePos.getCol(), generalPos.getRow(), generalPos.getCol(), true);
                } else if (attackingPiece.getType() == Type.ELEPHANT) {
                    canAttack = isValidElephantMove(currentPieces, attackingPiece, attackingPiecePos.getRow(), attackingPiecePos.getCol(), generalPos.getRow(), generalPos.getCol(), true);
                } else if (attackingPiece.getType() == Type.ROOK) {
                    boolean isOrthogonal = (attackingPiecePos.getRow() == generalPos.getRow() && attackingPiecePos.getCol() != generalPos.getCol()) ||
                                           (attackingPiecePos.getCol() == generalPos.getCol() && attackingPiecePos.getRow() != generalPos.getRow());
                    if (isOrthogonal && isPathClear(currentPieces, attackingPiecePos, generalPos)) {
                        canAttack = true;
                    }
                } else if (attackingPiece.getType() == Type.HORSE) {
                    int rowDiff = generalPos.getRow() - attackingPiecePos.getRow();
                    int colDiff = generalPos.getCol() - attackingPiecePos.getCol();

                    boolean isLShape = (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1) ||
                                       (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2);

                    if (isLShape) {
                        Position blockPos = null;
                        if (Math.abs(rowDiff) == 2) { // Vertical move first
                            blockPos = new Position(attackingPiecePos.getRow() + (rowDiff / 2), attackingPiecePos.getCol());
                        } else { // Horizontal move first
                            blockPos = new Position(attackingPiecePos.getRow(), attackingPiecePos.getCol() + (colDiff / 2));
                        }

                        if (!currentPieces.containsKey(blockPos)) {
                            canAttack = true;
                        }
                    }
                } else if (attackingPiece.getType() == Type.CANNON) {
                    boolean isOrthogonal = (attackingPiecePos.getRow() == generalPos.getRow() && attackingPiecePos.getCol() != generalPos.getCol()) ||
                                           (attackingPiecePos.getCol() == generalPos.getCol() && attackingPiecePos.getRow() != generalPos.getRow());
                    if (isOrthogonal) {
                        int piecesInPath = countPiecesInPath(currentPieces, attackingPiecePos, generalPos);
                        if (piecesInPath == 1) {
                            canAttack = true;
                        }
                    }
                } else if (attackingPiece.getType() == Type.SOLDIER) {
                    canAttack = isValidSoldierMove(currentPieces, attackingPiece, attackingPiecePos.getRow(), attackingPiecePos.getCol(), generalPos.getRow(), generalPos.getCol(), true);
                }

                if (canAttack) {
                    this.isCapture = originalIsCapture; // Restore original isCapture state
                    return true;
                }
                this.isCapture = originalIsCapture; // Restore original isCapture state
            }
        }
        return false;
    }

    private boolean isValidSoldierMove(Map<Position, Piece> currentPieces, Piece pieceToMove, int startRow, int startCol, int endRow, int endCol, boolean isAttackCheck) {
        int rowDiff = endRow - startRow;
        int colDiff = endCol - startCol;

        // Soldiers move forward one point.
        if (pieceToMove.getColor() == Color.RED) {
            if (rowDiff != 1 || colDiff != 0) { // Must move exactly one step forward
                // After crossing the river, can also move sideways.
                if (startRow >= 6 && rowDiff == 0 && Math.abs(colDiff) == 1) {
                    // This is a legal sideways move after crossing the river
                } else {
                    return false; // Not a valid forward or sideways move
                }
            }
            // Cannot move backward
            if (rowDiff < 0) {
                return false;
            }
        } else { // Black Soldier
            if (rowDiff != -1 || colDiff != 0) { // Must move exactly one step forward (downwards)
                // After crossing the river, can also move sideways.
                if (startRow <= 5 && rowDiff == 0 && Math.abs(colDiff) == 1) {
                    // This is a legal sideways move after crossing the river
                } else {
                    return false; // Not a valid forward or sideways move
                }
            }
            // Cannot move backward
            if (rowDiff > 0) {
                return false;
            }
        }

        // Check if target position is occupied by own piece, unless it's an attack check
        if (!isAttackCheck) {
            Piece targetPiece = currentPieces.get(new Position(endRow, endCol));
            if (targetPiece != null && targetPiece.getColor() == pieceToMove.getColor()) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidElephantMove(Map<Position, Piece> currentPieces, Piece pieceToMove, int startRow, int startCol, int endRow, int endCol, boolean isAttackCheck) {
        // Moves two points diagonally.
        boolean isTwoStepDiagonal = Math.abs(startRow - endRow) == 2 && Math.abs(startCol - endCol) == 2;

        if (!isTwoStepDiagonal) {
            return false;
        }

        // Cannot cross the river.
        // Red Elephants cannot move to rows 6-10. Black Elephants cannot move to rows 1-5.
        if (pieceToMove.getColor() == Color.RED && endRow > 5) {
            return false;
        }
        if (pieceToMove.getColor() == Color.BLACK && endRow < 6) {
            return false;
        }

        // Cannot jump over intervening pieces (the “Elephant Eye” must be clear).
        int midRow = (startRow + endRow) / 2;
        int midCol = (startCol + endCol) / 2;
        if (currentPieces.containsKey(new Position(midRow, midCol))) {
            return false; // Elephant eye is blocked
        }

        // Check if target position is occupied by own piece, unless it's an attack check
        if (!isAttackCheck) {
            Piece targetPiece = currentPieces.get(new Position(endRow, endCol));
            if (targetPiece != null && targetPiece.getColor() == pieceToMove.getColor()) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidGeneralMove(Map<Position, Piece> currentPieces, Piece pieceToMove, int startRow, int startCol, int endRow, int endCol, boolean isAttackCheck) {
        boolean isOrthogonalOneStep = (Math.abs(startRow - endRow) == 1 && startCol == endCol) ||
                                      (Math.abs(startCol - endCol) == 1 && startRow == endRow);

        boolean withinPalace = (pieceToMove.getColor() == Color.RED && isWithinRedPalace(endRow, endCol)) ||
                               (pieceToMove.getColor() == Color.BLACK && isWithinBlackPalace(endRow, endCol));

        if (isOrthogonalOneStep && withinPalace) {
            // Temporarily apply the move to check for flying general
            Map<Position, Piece> tempPieces = new HashMap<>(currentPieces); // Use currentPieces for temp copy
            tempPieces.remove(new Position(startRow, startCol));
            tempPieces.put(new Position(endRow, endCol), pieceToMove);

            Position redGeneralPos = null;
            Position blackGeneralPos = null;

            for (Map.Entry<Position, Piece> entry : tempPieces.entrySet()) {
                if (entry.getValue().getType() == Type.GENERAL) {
                    if (entry.getValue().getColor() == Color.RED) {
                        redGeneralPos = entry.getKey();
                    } else {
                        blackGeneralPos = entry.getKey();
                    }
                }
            }

            if (redGeneralPos != null && blackGeneralPos != null &&
                redGeneralPos.getCol() == blackGeneralPos.getCol()) {
                // Generals are in the same column, check if path is clear
                boolean pathClear = true;
                int minRow = Math.min(redGeneralPos.getRow(), blackGeneralPos.getRow());
                int maxRow = Math.max(redGeneralPos.getRow(), blackGeneralPos.getRow());

                for (int r = minRow + 1; r < maxRow; r++) {
                    if (tempPieces.containsKey(new Position(r, redGeneralPos.getCol()))) {
                        pathClear = false;
                        break;
                    }
                }
                if (pathClear) {
                    return false; // Flying General rule violated
                }
            }
            // Check if target position is occupied by own piece, unless it's an attack check
            if (!isAttackCheck) {
                Piece targetPiece = currentPieces.get(new Position(endRow, endCol));
                if (targetPiece != null && targetPiece.getColor() == pieceToMove.getColor()) {
                    return false; // Cannot capture own piece
                }
            }
            return true;
        }
        return false;
    }

    private boolean isValidCannonMove(Map<Position, Piece> currentPieces, Piece pieceToMove, int startRow, int startCol, int endRow, int endCol, boolean isAttackCheck) {
        Position startPos = new Position(startRow, startCol);
        Position endPos = new Position(endRow, endCol); // Corrected initialization
        boolean isOrthogonal = (startRow == endRow && startCol != endCol) || (startRow != endRow && startCol == endCol);

        if (!isOrthogonal) {
            return false;
        }

        int piecesInPath = countPiecesInPath(currentPieces, startPos, endPos);
        Piece targetPiece = currentPieces.get(endPos);

        if (targetPiece == null) { // Moving to an empty spot
            if (piecesInPath == 0) {
                return true;
            }
        } else { // Attempting to capture
            if (piecesInPath == 1 && targetPiece.getColor() != pieceToMove.getColor()) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidGuardMove(Map<Position, Piece> currentPieces, Piece pieceToMove, int startRow, int startCol, int endRow, int endCol, boolean isAttackCheck) {
        // Guard moves exactly one point diagonally and must stay within the palace.
        boolean isDiagonalOneStep = Math.abs(startRow - endRow) == 1 && Math.abs(startCol - endCol) == 1;

        if (isDiagonalOneStep) {
            if (pieceToMove.getColor() == Color.RED && isWithinRedPalace(endRow, endCol)) {
                // Check if target position is occupied by own piece, unless it's an attack check
                if (!isAttackCheck) {
                    Piece targetPiece = currentPieces.get(new Position(endRow, endCol));
                    if (targetPiece != null && targetPiece.getColor() == pieceToMove.getColor()) {
                        return false;
                    }
                }
                return true;
            } else if (pieceToMove.getColor() == Color.BLACK && isWithinBlackPalace(endRow, endCol)) {
                // Check if target position is occupied by own piece, unless it's an attack check
                if (!isAttackCheck) {
                    Piece targetPiece = currentPieces.get(new Position(endRow, endCol));
                    if (targetPiece != null && targetPiece.getColor() == pieceToMove.getColor()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private int countPiecesInPath(Map<Position, Piece> currentPieces, Position start, Position end) {
        int count = 0;
        if (start.getRow() == end.getRow()) { // Horizontal move
            int minCol = Math.min(start.getCol(), end.getCol());
            int maxCol = Math.max(start.getCol(), end.getCol());
            for (int col = minCol + 1; col < maxCol; col++) {
                if (currentPieces.containsKey(new Position(start.getRow(), col))) {
                    count++;
                }
            }
        } else if (start.getCol() == end.getCol()) { // Vertical move
            int minRow = Math.min(start.getRow(), end.getRow());
            int maxRow = Math.max(start.getRow(), end.getRow());
            for (int row = minRow + 1; row < maxRow; row++) {
                if (currentPieces.containsKey(new Position(row, start.getCol()))) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isPathClear(Map<Position, Piece> currentPieces, Position start, Position end) {
        if (start.getRow() == end.getRow()) { // Horizontal move
            int minCol = Math.min(start.getCol(), end.getCol());
            int maxCol = Math.max(start.getCol(), end.getCol());
            for (int col = minCol + 1; col < maxCol; col++) {
                if (currentPieces.containsKey(new Position(start.getRow(), col))) {
                    return false;
                }
            }
        } else if (start.getCol() == end.getCol()) { // Vertical move
            int minRow = Math.min(start.getRow(), end.getRow());
            int maxRow = Math.max(start.getRow(), end.getRow());
            for (int row = minRow + 1; row < maxRow; row++) {
                if (currentPieces.containsKey(new Position(row, start.getCol()))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isMoveLegal() {
        return moveLegal;
    }

    public void setMoveLegal(boolean moveLegal) {
        this.moveLegal = moveLegal;
    }

    public boolean isInCheck() {
        return inCheck;
    }

    public void setInCheck(boolean inCheck) {
        this.inCheck = inCheck;
    }

    public boolean isInCheckmate() {
        return inCheckmate;
    }

    public void setInCheckmate(boolean inCheckmate) {
        this.inCheckmate = inCheckmate;
    }

    public boolean isCheckmate(Color generalColor) {
        if (!isGeneralInCheck(generalColor)) {
            return false; // Not in check, so cannot be in checkmate
        }

        // Iterate through all pieces of the generalColor
        for (Map.Entry<Position, Piece> entry : pieces.entrySet()) {
            Piece piece = entry.getValue();
            Position startPos = entry.getKey();

            if (piece.getColor() == generalColor) {
                // Try every possible destination for this piece
                for (int endRow = 1; endRow <= 10; endRow++) {
                    for (int endCol = 1; endCol <= 9; endCol++) {
                        Position endPos = new Position(endRow, endCol);

                        // Create a temporary map to simulate the move
                        Map<Position, Piece> tempPieces = new HashMap<>(this.pieces); // Copy current board state
                        Piece capturedPiece = tempPieces.remove(endPos); // Simulate capture
                        tempPieces.remove(startPos);
                        tempPieces.put(endPos, piece); // Put the piece at the new position

                        boolean isLegalMoveOnTempBoard = false;
                        // Check if the move is legal on the temporary board based on piece rules
                        if (piece.getType() == Type.GENERAL) {
                            isLegalMoveOnTempBoard = isValidGeneralMove(tempPieces, piece, startPos.getRow(), startPos.getCol(), endRow, endCol, false);
                        } else if (piece.getType() == Type.GUARD) {
                            isLegalMoveOnTempBoard = isValidGuardMove(tempPieces, piece, startPos.getRow(), startPos.getCol(), endRow, endCol, false);
                        } else if (piece.getType() == Type.ELEPHANT) {
                            isLegalMoveOnTempBoard = isValidElephantMove(tempPieces, piece, startPos.getRow(), startPos.getCol(), endRow, endCol, false);
                        } else if (piece.getType() == Type.ROOK) {
                            boolean isOrthogonal = (startPos.getRow() == endRow && startPos.getCol() != endCol) || (startPos.getRow() != endRow && startPos.getCol() == endCol);
                            if (isOrthogonal && isPathClear(tempPieces, startPos, endPos)) {
                                Piece targetPiece = this.pieces.get(endPos); // Check original board for target piece
                                if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                                    isLegalMoveOnTempBoard = true;
                                }
                            }
                        } else if (piece.getType() == Type.HORSE) {
                            int rowDiff = endRow - startPos.getRow();
                            int colDiff = endCol - startPos.getCol();
                            boolean isLShape = (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1) ||
                                               (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2);
                            if (isLShape) {
                                Position blockPos = null;
                                if (Math.abs(rowDiff) == 2) {
                                    blockPos = new Position(startPos.getRow() + (rowDiff / 2), startPos.getCol());
                                } else {
                                    blockPos = new Position(startPos.getRow(), startPos.getCol() + (colDiff / 2));
                                }
                                if (!this.pieces.containsKey(blockPos)) { // Check original board for block
                                    Piece targetPiece = this.pieces.get(endPos); // Check original board for target
                                    if (targetPiece == null || targetPiece.getColor() != piece.getColor()) {
                                        isLegalMoveOnTempBoard = true;
                                    }
                                }
                            }
                        } else if (piece.getType() == Type.CANNON) {
                            isLegalMoveOnTempBoard = isValidCannonMove(tempPieces, piece, startPos.getRow(), startPos.getCol(), endRow, endCol, false);
                        } else if (piece.getType() == Type.SOLDIER) {
                            isLegalMoveOnTempBoard = isValidSoldierMove(tempPieces, piece, startPos.getRow(), startPos.getCol(), endRow, endCol, false);
                        }

                        // If the move was legal on the temp board AND it gets the general out of check
                        if (isLegalMoveOnTempBoard && !isGeneralInCheck(generalColor, tempPieces)) {
                            return false; // Found a legal move to escape check, so not checkmate
                        }
                    }
                }
            }
        }
        return true; // No legal moves found to escape check, so it's checkmate
    }

    public boolean isCapture() {
        return isCapture;
    }

    public void setCapture(boolean capture) {
        isCapture = capture;
    }

    private boolean isWithinRedPalace(int row, int col) {
        // Red palace: rows 1-3, columns 4-6 (inclusive)
        return row >= 1 && row <= 3 && col >= 4 && col <= 6;
    }

    private boolean isWithinBlackPalace(int row, int col) {
        // Black palace: rows 8-10, columns 4-6 (inclusive)
        return row >= 8 && row <= 10 && col >= 4 && col <= 6;
    }

    public boolean isGeneralInCheck(Color generalColor) {
        Position generalPos = null;
        Color attackingColor = (generalColor == Color.RED) ? Color.BLACK : Color.RED;

        // Find the General's position
        for (Map.Entry<Position, Piece> entry : pieces.entrySet()) {
            if (entry.getValue().getType() == Type.GENERAL && entry.getValue().getColor() == generalColor) {
                generalPos = entry.getKey();
                break;
            }
        }

        if (generalPos == null) {
            return false; // General not found (should not happen in a valid game state)
        }

        // Check if any opponent piece can attack the General's position
        for (Map.Entry<Position, Piece> entry : pieces.entrySet()) {
            Piece attackingPiece = entry.getValue();
            Position attackingPiecePos = entry.getKey();

            if (attackingPiece.getColor() == attackingColor) {
                // Temporarily set isCapture to false before checking each potential attack
                // This is important because isValidXMove methods might set it.
                boolean originalIsCapture = this.isCapture;
                this.isCapture = false;

                boolean canAttack = false;
                if (attackingPiece.getType() == Type.GENERAL) {
                    // General cannot attack another General directly (Flying General rule handles this)
                    // So, a General cannot put another General in check by moving to its square.
                    // This case is implicitly handled by isValidGeneralMove not returning true for such a move.
                    canAttack = isValidGeneralMove(this.pieces, attackingPiece, attackingPiecePos.getRow(), attackingPiecePos.getCol(), generalPos.getRow(), generalPos.getCol(), true);
                } else if (attackingPiece.getType() == Type.GUARD) {
                    canAttack = isValidGuardMove(this.pieces, attackingPiece, attackingPiecePos.getRow(), attackingPiecePos.getCol(), generalPos.getRow(), generalPos.getCol(), true);
                } else if (attackingPiece.getType() == Type.ELEPHANT) {
                    canAttack = isValidElephantMove(this.pieces, attackingPiece, attackingPiecePos.getRow(), attackingPiecePos.getCol(), generalPos.getRow(), generalPos.getCol(), true);
                } else if (attackingPiece.getType() == Type.ROOK) {
                    boolean isOrthogonal = (attackingPiecePos.getRow() == generalPos.getRow() && attackingPiecePos.getCol() != generalPos.getCol()) ||
                                           (attackingPiecePos.getCol() == generalPos.getCol() && attackingPiecePos.getRow() != generalPos.getRow());
                    if (isOrthogonal && isPathClear(this.pieces, attackingPiecePos, generalPos)) { // Pass this.pieces
                        canAttack = true;
                    }
                } else if (attackingPiece.getType() == Type.HORSE) {
                    int rowDiff = generalPos.getRow() - attackingPiecePos.getRow();
                    int colDiff = generalPos.getCol() - attackingPiecePos.getCol();

                    boolean isLShape = (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1) ||
                                       (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2);

                    if (isLShape) {
                        Position blockPos = null;
                        if (Math.abs(rowDiff) == 2) { // Vertical move first
                            blockPos = new Position(attackingPiecePos.getRow() + (rowDiff / 2), attackingPiecePos.getCol());
                        } else { // Horizontal move first
                            blockPos = new Position(attackingPiecePos.getRow(), attackingPiecePos.getCol() + (colDiff / 2));
                        }

                        if (!this.pieces.containsKey(blockPos)) { // Use this.pieces
                            canAttack = true;
                        }
                    }
                } else if (attackingPiece.getType() == Type.CANNON) {
                    boolean isOrthogonal = (attackingPiecePos.getRow() == generalPos.getRow() && attackingPiecePos.getCol() != generalPos.getCol()) ||
                                           (attackingPiecePos.getCol() == generalPos.getCol() && attackingPiecePos.getRow() != generalPos.getRow());
                    if (isOrthogonal) {
                        int piecesInPath = countPiecesInPath(this.pieces, attackingPiecePos, generalPos);
                        if (piecesInPath == 1) {
                            canAttack = true;
                        }
                    }
                } else if (attackingPiece.getType() == Type.SOLDIER) {
                    canAttack = isValidSoldierMove(this.pieces, attackingPiece, attackingPiecePos.getRow(), attackingPiecePos.getCol(), generalPos.getRow(), generalPos.getCol(), true);
                }

                if (canAttack) {
                    this.isCapture = originalIsCapture; // Restore original isCapture state
                    return true;
                }
                this.isCapture = originalIsCapture; // Restore original isCapture state
            }
        }
        return false;
    }
}
