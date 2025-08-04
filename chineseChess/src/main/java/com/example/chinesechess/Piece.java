package com.example.chinesechess;

public class Piece {
    public enum Type {
        GENERAL, GUARD, ROOK, HORSE, CANNON, ELEPHANT, SOLDIER
    }

    public enum Color {
        RED, BLACK
    }

    private Type type;
    private Color color;

    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
    }

    public Type getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return color + " " + type;
    }
}
