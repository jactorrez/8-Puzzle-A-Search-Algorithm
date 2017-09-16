import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import Map.ProbeHashMap;
import PriorityQueue.Entry;
import PriorityQueue.HeapAdaptablePriorityQueue;

public class Solver {
	int optimalSteps;
	Board goalBoard;
	Board[] optimalPath;
	
	HeapAdaptablePriorityQueue<Integer, Board> moves = new HeapAdaptablePriorityQueue<>();
	ProbeHashMap<Board, Integer> closed = new ProbeHashMap<>();	// current distances
	ProbeHashMap<Board, Integer> open = new ProbeHashMap<>();	// final distances
	ProbeHashMap<Board, Entry<Integer, Board>> moveLocator = new ProbeHashMap<>();	// keep pointers to moves in PQ to update
	
	// find a solution to the initial board (using A* search algorithm)
	public Solver(Board initial){
		System.out.println("Solving...");
		if(initial == null)
			throw new NullPointerException("Given board is null");
		
		if(!initial.isSolvable())
			throw new IllegalArgumentException("Given board is unsolvable");
		
		initial.setSteps(0);
		moves.insert(initial.getSteps() + initial.hamming(), initial);
		
		while(!moves.isEmpty()){
			Board currentMin = moves.removeMin().getValue();

			int currentSteps = currentMin.getSteps();
			
			if(currentMin.isGoal()){
				optimalSteps = currentSteps;
				goalBoard = currentMin;
				return;
			}
			
			closed.put(currentMin, currentSteps);
			open.remove(currentMin);
			
			for(Board neighbor : currentMin.neighbors()){
				if((!neighbor.equals(currentMin.getPrevious())) && (closed.get(neighbor) == null)){
					Integer currentCost = open.get(neighbor);
					boolean isInOpenList = currentCost != null;
					if(isInOpenList){
						
						int newCost = (currentSteps + 1) + neighbor.hamming();
						// path relaxation
						if(newCost < currentCost){
							currentCost = newCost;
							moves.replaceKey(moveLocator.get(neighbor), currentCost);
							open.put(neighbor, newCost);
						}
					} else{
						neighbor.setSteps(currentSteps + 1);
						neighbor.setPrevious(currentMin);
						int steps = neighbor.getSteps();
						int cost = steps + neighbor.hamming();
						moveLocator.put(neighbor, moves.insert(cost, neighbor));
						open.put(neighbor, cost);
					}
				}
			}
		}
	}
	
	// Minimum number of moves to solve initial board
	public int moves(){
		return optimalSteps;
	}
	
	// Sequence of boards in a shortest solution
	public Iterable<Board> solution(){
		ArrayList<Board> path = new ArrayList<>();
		Board current = goalBoard;
		while(current != null){
			path.add(current);
			current = current.getPrevious();
		}
		
		Collections.reverse(path);
		
		return path;
	}

	// Solve a slider puzzle
	public static void main(String[] args){
		int[][] board;
		Board testBoard;
		try(FileReader file = new FileReader("boards.txt")){
			Scanner fileScan = new Scanner(new BufferedReader(file));
			int N = fileScan.nextInt();
			board = new int[N][N];
			for(int i = 0; i < N; i++){
				for(int j = 0; j < N; j++){
					board[i][j] = fileScan.nextInt();
				}
			}
			
			testBoard = new Board(board);
			if(testBoard.isSolvable()){
				Solver testSolver = new Solver(testBoard);
				System.out.println("Puzzle solved in optimal " + testSolver.optimalSteps + " steps");
				ArrayList<Board> path = (ArrayList<Board>) testSolver.solution();
				System.out.println("Printing boards followed to solve problem");
				for(Board b : path){
					System.out.println(b);
				}
				
			} else{
				System.out.println("This puzzle is unsolvable");
			}
			
			fileScan.close();
			
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch(IOException e2){
			e2.printStackTrace();
		}
	}
}
