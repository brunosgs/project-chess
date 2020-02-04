package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Pawn;
import chess.pieces.Rook;

public class ChessMatch {
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
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

	public boolean isCheck() {
		return check;
	}

	public boolean isCheckMate() {
		return checkMate;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	private void initialSetup() {
		// Piece white
		placeNewPiece('a', 1, new Rook(this.board, Color.WHITE));
		placeNewPiece('e', 1, new King(this.board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(this.board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(this.board, Color.WHITE));
		placeNewPiece('b', 2, new Pawn(this.board, Color.WHITE));
		placeNewPiece('c', 2, new Pawn(this.board, Color.WHITE));
		placeNewPiece('d', 2, new Pawn(this.board, Color.WHITE));
		placeNewPiece('e', 2, new Pawn(this.board, Color.WHITE));
		placeNewPiece('f', 2, new Pawn(this.board, Color.WHITE));
		placeNewPiece('g', 2, new Pawn(this.board, Color.WHITE));
		placeNewPiece('h', 2, new Pawn(this.board, Color.WHITE));

		// Piece black
		placeNewPiece('a', 8, new Rook(this.board, Color.BLACK));
		placeNewPiece('e', 8, new King(this.board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(this.board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(this.board, Color.BLACK));
		placeNewPiece('b', 7, new Pawn(this.board, Color.BLACK));
		placeNewPiece('c', 7, new Pawn(this.board, Color.BLACK));
		placeNewPiece('d', 7, new Pawn(this.board, Color.BLACK));
		placeNewPiece('e', 7, new Pawn(this.board, Color.BLACK));
		placeNewPiece('f', 7, new Pawn(this.board, Color.BLACK));
		placeNewPiece('g', 7, new Pawn(this.board, Color.BLACK));
		placeNewPiece('h', 7, new Pawn(this.board, Color.BLACK));
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

		Piece capturedPiece = makeMove(source, target);

		if (testCheck(this.currentPlayer)) {
			undoMove(source, target, capturedPiece);

			throw new ChessException("You can't put yourself in check");
		}

		this.check = (testCheck(opponent(this.currentPlayer))) ? true : false;

		if (testCheckMate(opponent(this.currentPlayer))) {
			this.checkMate = true;
		} else {
			nextTurn();
		}

		return (ChessPiece) capturedPiece;
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) this.board.removePiece(source);

		p.increaseMoveCount();

		Piece capturedPiece = this.board.removePiece(target);

		this.board.placePiece(p, target);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}

		return capturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) this.board.removePiece(target);

		p.decreaseMoveCount();
		this.board.placePiece(p, source);

		if (capturedPiece != null) {
			this.board.placePiece(capturedPiece, target);

			this.capturedPieces.remove(capturedPiece);

			this.piecesOnTheBoard.add(capturedPiece);
		}
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

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> listPieceOnTheBoard = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());

		for (Piece piece : listPieceOnTheBoard) {
			if (piece instanceof King) {
				return (ChessPiece) piece;
			}
		}

		throw new IllegalStateException("There is no " + color + " king on the board!");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());

		for (Piece piece : opponentPieces) {
			boolean[][] mat = piece.possibleMoves();

			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}

		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}

		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());

		for (Piece piece : list) {
			boolean[][] mat = piece.possibleMoves();

			for (int l = 0; l < this.board.getRows(); l++) {
				for (int c = 0; c < this.board.getColumns(); c++) {
					if (mat[l][c]) {
						Position source = ((ChessPiece) piece).getChessPosition().toPosition();
						Position target = new Position(l, c);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);

						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}
}
