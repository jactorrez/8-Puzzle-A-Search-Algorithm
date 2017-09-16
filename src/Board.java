import java.util.ArrayList;

public class Board{
	
	public int[][] board;
	private int[] blankPosition = new int[2];
	private final int inversions;
	private final int N;
	private final int boardSize;
	private Board previous;
	private int steps = 0;
	private int hammingDist = -1;
	private int manhatDist  = -1;
	
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
		
		if(hammingDist != -1)
			return hammingDist;
		
		int amount = 0; 
		
		for(int r = 0; r < N; r++){
			for(int c = 0; c < N; c++){
				int val = board[r][c];
				if(val == 0){
					continue;
				}
				int goalRow = (val-1) / N;
				int goalCol = (val-1) % N;
				if(r != goalRow || c != goalCol){
					amount++;
				}
			}
		}
		
		hammingDist = amount;
		return amount;
	}
	
	// Returns sum of Manhattan distances between blocks and goal: O(N^2)
	public int manhattan(){
		
		if(manhatDist != -1)
			return manhatDist; 
		
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
		manhatDist = sum;
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
		if(y == null)
			return false;
		
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
	
	// Returns the Board used to get to this one
	public Board getPrevious(){
		return previous;
	}
	
	// Sets the Board used to get to this one
	public void setPrevious(Board prev){
		this.previous = prev;
	}
	
	// Returns the amount of steps (moves) made to get to this board
	public int getSteps(){
		return steps;
	}
	
	// Sets the amount of steps (moves) made to get to this board
	public void setSteps(int steps){
		this.steps = steps;
	}
	
	/**
	 * Calculates hash code for the current board
	 */
	public int hashCode(){
		int hash = 17; // nonzero prime constant
		hash = 31 * hash + ((Integer) hamming()).hashCode();
		hash = 31 * hash + ((Integer) manhattan()).hashCode();
		hash = 31 * hash + ((Integer) blankPosition[0]).hashCode();
		hash = 31 * hash + ((Integer) blankPosition[1]).hashCode();
		return hash;
		
	}
	
	/**
	 * Calculates number of inversions present in current board
	 * 
	 * @return number of inversions
	 */
	private int inversions(){
		
		return hamming();
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
		int[][] test = {{8,1,3},
						  {4,0,2},
						  {7,6,5}};	

		int[][] weirdBoard = {{1,0,3},
				  		      {4,5,2},
				  		      {8,7,6}};

		
		Board testBoard = new Board(test);
		Board testBoard2 = new Board(weirdBoard);
		
		System.out.println("Testing board...");
		System.out.println(testBoard2.inversions());
		
	}
}
