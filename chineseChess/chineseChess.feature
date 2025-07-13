Feature: Chinese Chess (象棋) Rules
    As a player
    I want to follow standard Chinese Chess rules
    So that I can play the game correctly

    We use (row, col) to indicate positions on a 9×10 board.
    Row 1 is Red’s bottom row; Row 10 is Black’s top row.
    Column 1 is the leftmost column from Red’s perspective.

    Each scenario sets up minimal conditions with relevant pieces.

    #################################################################
    # 1) GENERAL (將/帥)
    #################################################################
    @General
    Scenario: Red moves the General one step forward within the palace (Legal)
        Given the board is empty except for a Red General at (1, 5)
        When Red moves the General from (1, 5) to (2, 5)
        Then the move is legal

    @General
    Scenario: Red moves the General one step sideways within the palace (Legal)
        Given the board is empty except for a Red General at (1, 5)
        When Red moves the General from (1, 5) to (1, 4)
        Then the move is legal

    @General
    Scenario: Red moves the General outside the palace (Illegal)
        Given the board is empty except for a Red General at (3, 5)
        When Red moves the General from (3, 5) to (4, 5)
        Then the move is illegal

    @General
    Scenario: Red General moves into a position to face the enemy General (Illegal)
        Given the board has:
            | Piece         | Position |
            | Red General   | (2, 4)   |
            | Black General | (8, 5)   |
        When Red moves the General from (2, 4) to (2, 5)
        Then the move is illegal

    #################################################################
    # 2) GUARD (士/仕)
    #################################################################
    @Guard
    Scenario: Red moves the Guard one step diagonally within the palace (Legal)
        Given the board is empty except for a Red Guard at (1, 4)
        When Red moves the Guard from (1, 4) to (2, 5)
        Then the move is legal

    @Guard
    Scenario: Red moves the Guard outside the palace (Illegal)
        Given the board is empty except for a Red Guard at (2, 5)
        When Red moves the Guard from (2, 5) to (3, 4)
        Then the move is illegal

    @Guard
    Scenario: Red moves the Guard horizontally (Illegal)
        Given the board is empty except for a Red Guard at (2, 5)
        When Red moves the Guard from (2, 5) to (2, 6)
        Then the move is illegal

    @Guard
    Scenario: Red moves the Guard vertically (Illegal)
        Given the board is empty except for a Red Guard at (1, 4)
        When Red moves the Guard from (1, 4) to (2, 4)
        Then the move is illegal

    #################################################################
    # 3) ROOK (車)
    #################################################################
    @Rook
    Scenario: Red moves the Rook along a clear rank (Legal)
        Given the board is empty except for a Red Rook at (4, 1)
        When Red moves the Rook from (4, 1) to (4, 9)
        Then the move is legal

    @Rook
    Scenario: Red Rook captures a piece along a clear file (Legal)
        Given the board has:
            | Piece       | Position |
            | Red Rook    | (1, 1)   |
            | Black Horse | (10, 1)  |
        When Red moves the Rook from (1, 1) to (10, 1)
        Then the move is a legal capture

    @Rook
    Scenario: Red moves the Rook and attempts to jump over one piece (Illegal)
        Given the board has:
            | Piece         | Position |
            | Red Rook      | (4, 1)   |
            | Black Soldier | (4, 5)   |
        When Red moves the Rook from (4, 1) to (4, 9)
        Then the move is illegal

    #################################################################
    # 4) HORSE (馬/傌)
    #################################################################
    @Horse
    Scenario: Red moves the Horse in an “L” shape with no block (Legal)
        Given the board is empty except for a Red Horse at (3, 3)
        When Red moves the Horse from (3, 3) to (5, 4)
        Then the move is legal

    @Horse
    Scenario: Red moves the Horse and it is blocked by a vertically adjacent piece (Illegal)
        Given the board has:
            | Piece      | Position |
            | Red Horse  | (3, 3)   |
            | Black Rook | (4, 3)   |
        When Red moves the Horse from (3, 3) to (5, 4)
        Then the move is illegal

    @Horse
    Scenario: Red moves the Horse and it is blocked by a horizontally adjacent piece (Illegal)
        Given the board has:
            | Piece         | Position |
            | Red Horse     | (3, 3)   |
            | Black Soldier | (3, 2)   |
        When Red moves the Horse from (3, 3) to (4, 1)
        Then the move is illegal

    #################################################################
    # 5) CANNON (炮)
    #################################################################
    @Cannon
    Scenario: Red moves the Cannon like a Rook to an empty spot (Legal)
        Given the board is empty except for a Red Cannon at (6, 2)
        When Red moves the Cannon from (6, 2) to (6, 8)
        Then the move is legal

    @Cannon
    Scenario: Red captures with the Cannon by jumping over a single piece (Legal)
        Given the board has:
            | Piece         | Position |
            | Red Cannon    | (6, 2)   |
            | Black Soldier | (6, 5)   |
            | Black Guard   | (6, 8)   |
        When Red moves the Cannon from (6, 2) to (6, 8)
        Then the move is a legal capture

    @Cannon
    Scenario: Red moves the Cannon and attempts to capture without a screen (Illegal)
        Given the board has:
            | Piece       | Position |
            | Red Cannon  | (6, 2)   |
            | Black Guard | (6, 8)   |
        When Red moves the Cannon from (6, 2) to (6, 8)
        Then the move is illegal because it is not a capture and the path is blocked

    @Cannon
    Scenario: Red moves the Cannon and attempts to capture over two screens (Illegal)
        Given the board has:
            | Piece         | Position |
            | Red Cannon    | (6, 2)   |
            | Red Soldier   | (6, 4)   |
            | Black Soldier | (6, 5)   |
            | Black Guard   | (6, 8)   |
        When Red moves the Cannon from (6, 2) to (6, 8)
        Then the move is illegal

    #################################################################
    # 6) ELEPHANT (相/象)
    #################################################################
    @Elephant
    Scenario: Red moves the Elephant a 2x2 diagonal with a clear midpoint (Legal)
        Given the board is empty except for a Red Elephant at (1, 3)
        When Red moves the Elephant from (1, 3) to (3, 5)
        Then the move is legal

    @Elephant
    Scenario: Red moves the Elephant and attempts to cross the river (Illegal)
        Given the board is empty except for a Red Elephant at (5, 3)
        When Red moves the Elephant from (5, 3) to (7, 5)
        Then the move is illegal

    @Elephant
    Scenario: Red moves the Elephant and its path is blocked at the midpoint (Illegal)
        Given the board has:
            | Piece        | Position |
            | Red Elephant | (1, 3)   |
            | Black Rook   | (2, 4)   |
        When Red moves the Elephant from (1, 3) to (3, 5)
        Then the move is illegal

    #################################################################
    # 7) SOLDIER/PAWN (兵/卒)
    #################################################################
    @Soldier
    Scenario: Red moves the Soldier one step forward before crossing the river (Legal)
        Given the board is empty except for a Red Soldier at (3, 5)
        When Red moves the Soldier from (3, 5) to (4, 5)
        Then the move is legal

    @Soldier
    Scenario: Red moves the Soldier sideways before crossing the river (Illegal)
        Given the board is empty except for a Red Soldier at (3, 5)
        When Red moves the Soldier from (3, 5) to (3, 4)
        Then the move is illegal

    @Soldier
    Scenario: Red moves the Soldier one step forward after crossing the river (Legal)
        Given the board is empty except for a Red Soldier at (6, 5)
        When Red moves the Soldier from (6, 5) to (7, 5)
        Then the move is legal

    @Soldier
    Scenario: Red moves the Soldier sideways after crossing the river (Legal)
        Given the board is empty except for a Red Soldier at (6, 5)
        When Red moves the Soldier from (6, 5) to (6, 4)
        Then the move is legal

    @Soldier
    Scenario: Red moves the Soldier backward (Illegal)
        Given the board is empty except for a Red Soldier at (6, 5)
        When Red moves the Soldier from (6, 5) to (5, 5)
        Then the move is illegal

    @Soldier
    Scenario: Red Soldier on the final rank can only move sideways (Legal)
        Given the board is empty except for a Red Soldier at (10, 5)
        When Red moves the Soldier from (10, 5) to (10, 4)
        Then the move is legal

    #################################################################
    # 8) WINNING, LOSING AND DRAWING (輸贏和)
    #################################################################
    @Winning
    Scenario: Red puts the Black General in check
        Given the board has:
            | Piece         | Position |
            | Red Rook      | (5, 5)   |
            | Black General | (10, 5)  |
        When Red moves the Rook from (5, 5) to (9, 5)
        Then the Black General is in check

    @Winning
    Scenario: Red checkmates the Black General, winning the game
        Given the board has:
            | Piece         | Position |
            | Red Rook      | (9, 4)   |
            | Red Horse     | (8, 7)   |
            | Black General | (10, 6)  |
            | Black Guard   | (10, 5)  |
        When Red moves the Rook from (9, 4) to (10, 4)
        Then Black is in checkmate and Red wins
