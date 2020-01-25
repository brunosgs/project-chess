package chess;

import java.util.ArrayList;
import java.util.List;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	private int turn;
	private Color currentPlayer;
	private Board board;
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		this.board = new Board(8, 8);
		this.turn = 1;
		this.currentPlayer = Color.WHITE;
		this.initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	private void initialSetup() {
		this.placeNewPiece('c', 2, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('c', 1, new Rook(this.board, Color.WHITE));
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

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[this.board.getRows()][this.board.getColumns()];

		for (int l = 0; l < this.board.getRows(); l++) {
			for (int c = 0; c < this.board.getColumns(); c++) {
				mat[l][c] = (ChessPiece) this.board.piece(l, c);
			}
		}

		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();

		validateSourcePosition(position);

		return this.board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();

		validateSourcePosition(source);
		validateTargetPosition(source, target);

		Piece capturePiece = this.makeMove(source, target);

		nextTurn();

		return (ChessPiece) capturePiece;
	}

	private Piece makeMove(Position source, Position target) {
		Piece p = this.board.removePiece(source);
		Piece capturedPiece = this.board.removePiece(target);

		this.board.placePiece(p, target);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}

		return capturedPiece;
	}

	private void validateSourcePosition(Position position) {
		if (!this.board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position!");
		}

		if (this.currentPlayer != ((ChessPiece) this.board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}

		if (!this.board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}

	private void validateTargetPosition(Position source, Position target) {
		if (!this.board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());

		piecesOnTheBoard.add(piece);
	}

	private void nextTurn() {
		this.turn++;
		this.currentPlayer = (this.currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
}
