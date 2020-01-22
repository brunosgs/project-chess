package application;

import chess.ChessPiece;

public class UI {
	public static void printBoard(ChessPiece[][] pieces) {
		for (int l = 0; l < pieces.length; l++) {
			System.out.print((8 - l) + " ");
			for (int c = 0; c < pieces.length; c++) {
				UI.printPiece(pieces[l][c]);
			}
			System.out.print("\n");
		}
		System.out.println("  a b c d e f g h");
	}

	private static void printPiece(ChessPiece piece) {
		if (piece == null) {
			System.out.print("-");
		} else {
			System.out.print(piece);
		}

		System.out.print(" ");
	}
}
