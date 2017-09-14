import java.util.ArrayList;

public class Board {
	
	public int[][] board;
	private int[] blankPosition = new int[2];
	private final int inversions;
	private final int N;
	private final int boardSize;
	
	// Constructs a board from an N-by-N array of blocks: O(N^2)
	public Board(int[][] blocks){
		this.N = blocks.length;
		this.boardSize = N * N;
		this.board = new int[N][N];
		
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				board[i][j] = blocks[i][j];
			}
		}
		
		getBlankPosition();
		this.inversions = inversions();
	}
	
	// Returns board size: O(1)
	public int size(){
		return boardSize;
	}
	
	// Returns number of blocks out of place: O(N^2)
	public int hamming(){
		int amount = 0; 
		
		for(int r = 0; r < N; r++){
			for(int c = 0; c < N; c++){
			
				if((c == N-1) && (r != N-1)){
					if((board[r+1][0] != 0) && board[r][c] > board[r+1][0]){
						amount++;
					}
				} else if((c <= N-2 && board[r][c+1] != 0) && board[r][c] > board[r][c+1]){
					amount++;
				}
			}
		}
		return amount;
	}
	
	// Returns sum of Manhattan distances between blocks and goal: O(N^2)
	public int manhattan(){
		int sum = 0;
		
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				if(board[i][j] == 0)
					continue;
				
				int val = board[i][j];
				int row = i;
				int col = j; 
				int goalRow = (val-1) / N;
				int goalCol = (val-1) % N;
	
				int manhatDist = Math.abs(row - goalRow) + Math.abs(col - goalCol);
				sum += manhatDist;
			}
		}
		
		return sum;
	}
	
	// Checks if this board is the goal board
	public boolean isGoal(){
		return (inversions() == 0);
	}
	
	// Checks if this board is solvable: O(1)
	public boolean isSolvable(){
		// Board size is odd
		if(boardSize / 2 != 0){
			// Parity of inversions is odd, will remain odd, so it is unsolvable
			if(inversions % 2 != 0){
				return false;
			} else {
				return true;
			}
		} else {
			int sum = inversions + blankPosition[0];
			
			if(sum % 2 == 0){
				return false;
			} else{
				return true;
			}
		}
	}
	
	// Checks if this board equals board y: O(N^2)
	public boolean equals(Board y){
		int[][] other = y.board;
		int otherBoardSize = y.boardSize;
		boolean isEqual = true;
		
		if(boardSize != otherBoardSize){
			return false;
		} else {
			for(int i = 0; i < N && isEqual; i++){
				for(int j = 0; j < N; j++){
					if(board[i][j] != other[i][j]){
						isEqual = false;
						break;
					}
				}
			}
		}
		return isEqual;
	}
	
	// Returns all neighboring boards: O(1)
	public Iterable<Board> neighbors(){
		ArrayList<Board> neighbors = new ArrayList<>();
		int row = blankPosition[0];
		int col = blankPosition[1];
		boolean hasRightBlock = (col + 1 <= N-1);
		boolean hasLeftBlock = (col - 1 >= 0);
		boolean hasTopBlock = (row - 1 >= 0);
		boolean hasBottomBlock = (row + 1 <= N-1);
		
		if(hasRightBlock){
			Board newBoard = new Board(makeMove(row, col + 1));
			neighbors.add(newBoard);
		} 
		
		if(hasLeftBlock){
			Board newBoard = new Board(makeMove(row, col - 1));
			neighbors.add(newBoard);
		}
		
		if(hasTopBlock){
			Board newBoard = new Board(makeMove(row - 1, col));
			neighbors.add(newBoard);
		}
		
		if(hasBottomBlock){
			Board newBoard = new Board(makeMove(row + 1, col));
			neighbors.add(newBoard);
		}
		return neighbors;
	}
	
	// Outputs string representation of this board: O(N^2)
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int r = 0; r < N; r++){
			for(int c = 0; c < N; c++){
				sb.append(board[r][c] + "  ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	/* --- Utility functions --- */
	
	/**
	 * Calculates number of inversions present in current board
	 * 
	 * @return number of inversions
	 */
	private int inversions(){
		int amount = 0; 
		
		for(int r = 0; r < N; r++){
			for(int c = 0; c < N; c++){
			
				if((c == N-1) && (r != N-1)){
					if((board[r+1][0] != 0) && board[r][c] > board[r+1][0]){
						amount++;
					}
				} else if((c <= N-2 && board[r][c+1] != 0) && board[r][c] > board[r][c+1]){
					amount++;
				}
			}
			
		}
		
		return amount;
	}
	
	/**
	 * Finds the position of the "blank" block in the board
	 * 
	 * @return integer array containing the row at index 0 and column at index 1
	 */
	private void getBlankPosition(){
		boolean found = false;
		
		for(int r = 0; r < N && !found; r++){
			for(int c = 0; c < N; c++){
				if(board[r][c] == 0){
					blankPosition[0] = r;
					blankPosition[1] = c;
					found = true;
					break;
				}
			}
		}

	}
	
	/**
	 * Creates an instance of a neighbor for this board
	 * 
	 * @param blank		position of blank block
	 * @param row		row location of item to move to blank block position
	 * @param col		column location of item to move to blank block position
	 * @return instance of a Board representing a neighbor of current board
	 */
	private int[][] makeMove(int row, int col){
		int[][] boardCopy = copyOfBoard();
		
		int blankRow = blankPosition[0];
		int blankCol = blankPosition[1];
		
		boardCopy[blankRow][blankCol] = boardCopy[row][col];
		boardCopy[row][col] = 0;
		
		return boardCopy;
	}
	
	/**
	 * Makes copy of the current board
	 * 
	 * @return copy of the current board
	 */
	
	private int[][] copyOfBoard(){
		int[][] temp = new int[N][N];
		
		for(int r = 0; r < N; r++){
			for(int c = 0; c < N; c++){
				temp[r][c] = board[r][c];
			}
		}
		
		return temp;
	}
	
	// Unit tests 
	public static void main(String[] args){
		int[][] test = {{1,3,4},
						  {8,0,7},
						  {5,6,2}};

		int[][] goal = {{1,2,3},
				  		    {4,5,6},
				  		    {7,8,0}};

		
		Board testBoard = new Board(test);
		Board testGoal = new Board(goal);
		
		System.out.println("Current board...");
		System.out.println(testBoard);
		
		System.out.println("Neighbors...");
		Iterable<Board> neighbors = testBoard.neighbors();
		
		for(Board b : neighbors){
			System.out.println(b);
		}
	}
}
