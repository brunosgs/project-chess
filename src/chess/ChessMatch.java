package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	private Board board;

	private void initialSetup() {
		this.placeNewPiece('c', 1, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('c', 2, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('d', 2, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('e', 2, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('e', 1, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('d', 1, new King(this.board, Color.WHITE));

		this.placeNewPiece('c', 7, new Rook(this.board, Color.BLACK));
		this.placeNewPiece('c', 8, new Rook(this.board, Color.BLACK));
		this.placeNewPiece('d', 7, new Rook(this.board, Color.BLACK));
		this.placeNewPiece('e', 7, new Rook(this.board, Color.BLACK));
		this.placeNewPiece('e', 8, new Rook(this.board, Color.BLACK));
		this.placeNewPiece('d', 8, new King(this.board, Color.BLACK));
	}

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

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();

		this.validateSourcePosition(source);

		Piece capturePiece = makeMove(source, target);

		return (ChessPiece) capturePiece;
	}

	private Piece makeMove(Position source, Position target) {
		Piece p = this.board.removePiece(source);
		Piece capturedPiece = this.board.removePiece(target);

		this.board.placePiece(p, target);

		return capturedPiece;
	}

	private void validateSourcePosition(Position position) {
		if (!this.board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position!");
		}
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
}
