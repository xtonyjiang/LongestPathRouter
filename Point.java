class Point{
	static Point[][] grid;
	int id = 0;
	static int m;
	static int n;
	
	int direction = -1;
	int row;
	int column;
	
	boolean isObstacle = false;
	
	boolean upFree = true;
	boolean rightFree = true;
	boolean downFree = true;
	boolean leftFree = true;
	
	boolean upLeftFree = false;
	boolean upRightFree = false;
	boolean downLeftFree = false;
	boolean downRightFree = false;
	int numFree = 4;
	
	Point up;
	Point left;
	Point down;
	Point right;
	
	boolean inPath = false;
	boolean test = false;
	
	static final Point ORIGIN = new Point(0, 0);
	static Point NULL = new Point(-1, -1, true);
	
	public Point(int row, int column){
		this.row = row;
		this.column = column;
	}
	
	public Point(int row, int column, boolean obstacle){
		this.row = row;
		this.column = column;
		this.isObstacle = obstacle;
		if(obstacle) id = -1;
	}
	
	/*********************
	  
	 Method: getNeighbors();
	 Brief: Looks for neighbors that aren't obstacles by corner and by edge
	 
	*********************/
	
	void getNeighbors(){
		numFree = 4;
		if(row > 0) up = grid[row-1][column];
		else{
			up = NULL;
		}
		if(up.isObstacle){
			upFree = false;
			numFree--;
		}
		
		if(column > 0) left = grid[row][column-1];
		else{
			left = NULL;
		}
		if(left.isObstacle){
			leftFree = false;
			numFree--;
		}
		
		if(row < m - 1) down = grid[row+1][column];
		else{
			down = NULL;
		}
		if(down.isObstacle){
			downFree = false;
			numFree--;
		}
		
		if(column < n - 1) right = grid[row][column+1];
		else{
			right = NULL;
		}
		if(right.isObstacle){
			rightFree = false;
			numFree--;
		}
		
		if(row>0&&column>0){
			if(!grid[row-1][column-1].isObstacle){
				grid[row][column].upLeftFree = true;
			}
		}
		if(row>0&&column<n-1){
			if(!grid[row-1][column+1].isObstacle){
				grid[row][column].upRightFree = true;
			}
		}
		if(row<m-1&&column>0){
			if(!grid[row+1][column-1].isObstacle){
				grid[row][column].downLeftFree = true;
			}
		}
		if(row<m-1&&column<n-1){
			if(!grid[row+1][column+1].isObstacle){
				grid[row][column].downRightFree = true;
			}
		}
	}
	boolean equals(Point p){
		if(this.row==p.row && this.column==p.column) return true;
		return false;
	}
	public String toString(){
		return("("+row+","+column+"): " + id);
	}
}