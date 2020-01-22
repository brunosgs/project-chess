package application;

import java.util.Scanner;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Main {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();

		while (true) {
			UI.printBoard(chessMatch.getPieces());

			System.out.print("\nSource: ");
			ChessPosition source = UI.readChessPosition(scan);
			System.out.print("\nTarget: ");
			ChessPosition target = UI.readChessPosition(scan);

			ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
		}
	}
}
