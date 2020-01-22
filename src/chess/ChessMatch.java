package chess;

import boardgame.Board;

public class ChessMatch {
	private Board board;

	public ChessMatch() {
		this.board = new Board(8, 8);
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[this.board.getRows()][this.board.getColumns()];

		for (int l = 0; l < this.board.getRows(); l++) {
			for (int c = 0; c < this.board.getColumns(); c++) {
				mat[l][c] = (ChessPiece) this.board.piece(l, c);
			}
		}

		return mat;
	}
}
