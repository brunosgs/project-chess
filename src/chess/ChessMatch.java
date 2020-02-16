package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
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

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}

	private void initialSetup() {
		// Piece white
		placeNewPiece('a', 1, new Rook(this.board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(this.board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(this.board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(this.board, Color.WHITE));
		placeNewPiece('e', 1, new King(this.board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(this.board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(this.board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(this.board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(this.board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(this.board, Color.WHITE, this));

		// Piece black
		placeNewPiece('a', 8, new Rook(this.board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(this.board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(this.board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(this.board, Color.BLACK));
		placeNewPiece('e', 8, new King(this.board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(this.board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(this.board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(this.board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(this.board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(this.board, Color.BLACK, this));
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

		ChessPiece movedPiece = (ChessPiece) this.board.piece(target);
		this.check = (testCheck(opponent(this.currentPlayer))) ? true : false;

		if (testCheckMate(opponent(this.currentPlayer))) {
			this.checkMate = true;
		} else {
			nextTurn();
		}

		// #Special move en passant
		if (movedPiece instanceof Pawn
				&& (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
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

		// #Special move castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceTower = new Position(source.getRow(), source.getColumn() + 3);
			Position targetTower = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceTower);

			this.board.placePiece(rook, targetTower);

			rook.increaseMoveCount();
		}

		// #Special move castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceTower = new Position(source.getRow(), source.getColumn() - 4);
			Position targetTower = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceTower);

			this.board.placePiece(rook, targetTower);

			rook.increaseMoveCount();
		}

		// #Special move en passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;

				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				} else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}

				capturedPiece = this.board.removePiece(pawnPosition);

				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
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

		// #Special move castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceTower = new Position(source.getRow(), source.getColumn() + 3);
			Position targetTower = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(targetTower);

			this.board.placePiece(rook, sourceTower);

			rook.decreaseMoveCount();
		}

		// #Special move castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceTower = new Position(source.getRow(), source.getColumn() - 4);
			Position targetTower = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(targetTower);

			this.board.placePiece(rook, sourceTower);

			rook.decreaseMoveCount();
		}

		// #Special move en passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece) this.board.removePiece(target);
				Position pawnPosition;

				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				} else {
					pawnPosition = new Position(4, target.getColumn());
				}

				this.board.placePiece(pawn, pawnPosition);
			}
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
