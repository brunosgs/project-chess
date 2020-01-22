package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	private Board board;

	public ChessMatch() {
		this.board = new Board(8, 8);
		this.initialSetup();
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

	private void initialSetup() {
		this.board.placePiece(new Rook(this.board, Color.WHITE), new Position(2, 1));
		this.board.placePiece(new King(this.board, Color.BLACK), new Position(0, 4));
		this.board.placePiece(new King(this.board, Color.WHITE), new Position(7, 4));
	}
}
