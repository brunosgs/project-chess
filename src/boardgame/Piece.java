package boardgame;

public abstract class Piece {
	protected Position position;
	private Board board;

	public Piece(Board board) {
		this.board = board;
		this.position = null;
	}

	protected Board getBoard() {
		return board;
	}

	public abstract boolean[][] possibleMoves();

	public boolean possibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}

	public boolean isThereAnyPossibleMove() {
		boolean[][] mat = possibleMoves();

		for (int l = 0; l < mat.length; l++) {
			for (int c = 0; c < mat.length; c++) {
				if (mat[l][c]) {
					return true;
				}
			}
		}
		
		return false;
	}
}
