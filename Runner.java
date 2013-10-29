import java.io.*;
import java.util.*;

public class Runner {	
	static int m, n; //rows, columns
	static LinkedList<Point> queue = new LinkedList<Point>();
	static LinkedList<Point> unprocessed = new LinkedList<Point>(); //queue of new regions to floodfill
	static LinkedList<Point> origins = new LinkedList<Point>();
	static ArrayList<Region> regions = new ArrayList<Region>();
	static Path initialPath = new Path();
	static LinkedList<PathPart> finalPath = new LinkedList<PathPart>();
	static ListIterator<PathPart> i;
	static int curRegionIndex = 0;
	static Region curRegion;
	static Region nextRegion;
	static long start;
	static long timelimit = 5000;
	static int totalArea = 0;
	
	static boolean[][] adjmat;
	static ArrayList<Region>[] adjlist;
	
	public Runner(){
		queue = new LinkedList<Point>();
		unprocessed = new LinkedList<Point>(); //queue of new regions to floodfill
		origins = new LinkedList<Point>();
		regions = new ArrayList<Region>();
		initialPath = new Path();
		finalPath = new LinkedList<PathPart>();
		curRegionIndex = 0;
		timelimit = 1000;
		totalArea = 0;
	}
	
	public static void main(String args[]) throws IOException{
		processInput();
		floodFill(Point.grid[0][0]);
		//printRegions();
		constructGraph();
		start = System.currentTimeMillis();
		findInitialPath(initialPath, regions.get(1));
		//printInitialPath();
		constructInitialPath();
		extendPath();
		printOutput();
	}
	
	/*********************
	  
	 Method: void processInput();
	 Brief: Processes input (dimensions of grid, location of obstacles)
	 
	*********************/
	
	public static void processInput() throws IOException{
		BufferedReader f = new BufferedReader(new FileReader("input.in"));
		StringTokenizer st = new StringTokenizer(f.readLine());
		Point.m = m = Integer.parseInt(st.nextToken());
		Point.n = n = Integer.parseInt(st.nextToken());
		Point.grid = new Point[Point.m][Point.n];
		regions.add(new Region(0));
		
		//initializes grid, 'X' stands for an obstacle
		for(int i = 0; i < m; ++i){
			String s = f.readLine();
			for(int j = 0; j < n; ++j){
				Point.grid[i][j] = new Point(i, j, s.charAt(j) == 'X');
			}
		}
		
		for(int i = 0; i < m; ++i){
			for(int j = 0; j < n; ++j){
				Point.grid[i][j].getNeighbors();
			}
		}
	}
	
	/*********************
	  
	 Method: void floodFill(int row, int column);
	 Brief: Divides the grid into regions
	 
	*********************/
	
	public static void floodFill(Point o){
		int row = o.row;
		int column = o.column;
		//if this point has already been visited, return
		if(Point.grid[row][column].id != 0) return;
		regions.add(new Region(regions.size()));
		
		queue.add(o);
		while(!queue.isEmpty()){
			Point p = queue.pollFirst();
			if(p.id != 0) continue;
			totalArea++;
			p.id = regions.size() - 1;
			regions.get(regions.size()-1).area++;
			qUp(p, p.up);
			qLeft(p, p.left);
			qDown(p, p.down);
			qRight(p, p.right);
		}
		while(!unprocessed.isEmpty()){
			floodFill(find(origins.poll(), unprocessed.poll()));
		}
	}
	
	/*********************
	  
	 Method: void qUp(Point p);
	 Brief: Determines whether the square above is a new region and queues it.
	 
	*********************/
	
	public static void qUp(Point p, Point up){
		if(up == Point.NULL || up.isObstacle || up.id != 0) return;
		else if(!up.leftFree && !up.rightFree){
			unprocessed.add(up);
			origins.add(p);
		}
		else if((!p.leftFree && !up.rightFree) || (!p.rightFree && !up.leftFree)){
			unprocessed.add(up);
			origins.add(p);
		}
		/*else if(up.row>0 && up.row<m-1 && up.column>0 && up.column<n-1){
			
		}*/
		else{
			queue.add(up);
		}
	}
	
	/*********************
	  
	 Method: void qLeft(Point p);
	 Brief: Determines whether the square to the left is a new region and queues it.
	 
	*********************/
	
	public static void qLeft(Point p, Point left){
		if(left == Point.NULL || left.isObstacle || left.id != 0) return;
		else if(!left.upFree && !left.downFree){
			unprocessed.add(left);
			origins.add(p);
		}
		else if((!p.upFree && !left.downFree) || (!p.downFree && !left.upFree)){
			unprocessed.add(left);
			origins.add(p);
		}
		else{
			queue.add(left);
		}
	}
	
	/*********************
	  
	 Method: void qDown(Point p);
	 Brief: Determines whether the square below is a new region and queues it.
	 
	*********************/
	
	public static void qDown(Point p, Point down){
		if(down == Point.NULL || down.isObstacle || down.id != 0) return;
		else if(!down.leftFree && !down.rightFree){
			unprocessed.add(down);
			origins.add(p);
		}
		else if((!p.leftFree && !down.rightFree) || (!p.rightFree && !down.leftFree)){
			unprocessed.add(down);
			origins.add(p);
		}
		else{
			queue.add(down);
		}
	}
	
	/*********************
	  
	 Method: void qRight(Point p);
	 Brief: Determines whether the square above is a new region and queues it.
	 
	*********************/
	
	public static void qRight(Point p, Point right){
		if(right == Point.NULL || right.isObstacle || right.id != 0) return;
		else if(!right.upFree && !right.downFree){
			unprocessed.add(right);
			origins.add(p);
		}
		else if((!p.upFree && !right.downFree) || (!p.downFree && !right.upFree)){
			unprocessed.add(right);
			origins.add(p);
		}
		else{
			queue.add(right);
		}
	}
	
	/*********************
	  
	 Method: Point find(Point o, Point p);
	 Brief: Makes a new region for the corridor and returns the end of the corridor
	 Details: O is the original point. P is the point to start searching from.
	 
	*********************/
	
	public static Point find(Point o, Point p){
		if(p.numFree > 2 || p.id != 0) return p;
		regions.add(new Region(regions.size()));
		regions.get(regions.size()-1).area++;
		p.id = regions.size() - 1;
		if(p.numFree == 3) return Point.NULL;
		LinkedList<Point> next = new LinkedList<Point>();
		next.add(p);
		while(!next.isEmpty()){
			Point tmp = next.poll();
			if(tmp.numFree > 2) return tmp;
			if(tmp.numFree == 3) return Point.NULL;
			totalArea++;
			tmp.id = regions.size() - 1;
			if(isValid(o, tmp.up)){
				next.add(tmp.up);
				regions.get(regions.size()-1).area++;
				continue;
			}
			if(isValid(o, tmp.right)){
				next.add(tmp.right);
				regions.get(regions.size()-1).area++;
				continue;
			}
			if(isValid(o, tmp.down)){
				next.add(tmp.down);
				regions.get(regions.size()-1).area++;
				continue;
			}
			if(isValid(o, tmp.left)){
				next.add(tmp.left);
				regions.get(regions.size()-1).area++;
				continue;
			}
			return tmp;
		}
		return Point.NULL;
	}
	
	/*********************
	  
	 Method: boolean isValid(Point o, Point p);
	 Brief: Used in find; determines whether the point will be part of the corridor
	 
	*********************/
	
	public static boolean isValid(Point o, Point p){
		return !p.isObstacle && p != o && p.id != regions.size() - 1;
	}
	
	/*********************
	  
	 Method: void printRegions();
	 Brief: Prints out the grid, showing all of the regions
	 
	*********************/
	
	public static void printRegions(){
		for(int i = 0; i < m; ++i){
			System.out.println();
			for(int j = 0; j < n; ++j){
				if(Point.grid[i][j].isObstacle) System.out.print("X");
				else{
					System.out.print(Point.grid[i][j].id);
				}
			}
		}
		System.out.println();
	}
	
	/*********************
	  
	 Method: void constructGraph();
	 Brief: Makes a graph out of all the regions
	 
	*********************/
	
	public static void constructGraph(){
		constructAdjMat();
		constructAdjList();
	}
	
	/*********************
	  
	 Method: void constructAdjMat();
	 Brief: Makes the adjacency matrix of regions
	 
	*********************/
	
	public static void constructAdjMat(){
		adjmat = new boolean[regions.size()][regions.size()];
		for(int i = 0; i < m; ++i){
			for(int j = 0; j < n; ++j){
				Point p = Point.grid[i][j];
				connect(p, p.up);
				connect(p, p.right);
				connect(p, p.down);
				connect(p, p.left);
			}
		}
	}
	
	/*********************
	  
	 Method: void connect(Point a, Point b);
	 Brief: Sees if the two points are valid to be connected
	 		on the adjacency matrix.
	 
	*********************/
	
	public static void connect(Point a, Point b){
		if(a.isObstacle || b.isObstacle || a.id == b.id) return;
		adjmat[a.id][b.id] = true;
		adjmat[b.id][a.id] = true;
	}
	
	/*********************
	  
	 Method: void constructAdjList();
	 Brief: Makes the adjacency list of regions
	 
	*********************/
	
	public static void constructAdjList(){
		adjlist = new ArrayList[regions.size()];
		for(int i = 1; i < adjlist.length; ++i){
			adjlist[i] = new ArrayList<Region>();
		}
		for(int i = 1; i < regions.size(); ++i){
			for(int j = 1; j < regions.size(); ++j){
				if(adjmat[i][j]) adjlist[i].add(regions.get(j));
			}
		}
	}

	/*********************
	  
	 Method: void findInitialPath();
	 Brief: Finds the regions to traverse with the largest
	 		total area.
	 
	*********************/
	
	public static Path findInitialPath(Path p, Region r){
		if(System.currentTimeMillis() - start > 5000){
			if(p.area > initialPath.area){
				initialPath = p;
			}
			return p;
		}
		for(int i = 0; i < p.apath.size(); ++i){
			if(r == p.apath.get(i)){
				if(System.currentTimeMillis() - start > timelimit){
					if(p.area > initialPath.area){
						initialPath = p;
					}
				}
				return p;
			}
		}
		p.add(r);
		Path ret = new Path();
		for(int i = 0; i < adjlist[r.id].size(); ++i){
			if(System.currentTimeMillis() - start > timelimit){
				if(ret.area > initialPath.area){
					initialPath = ret;
				}
				if(p.area > initialPath.area){
					initialPath = p;
				}
				return ret;
			}
			Path tmp = findInitialPath(new Path(p), adjlist[r.id].get(i));
			if(tmp.area > ret.area) ret = tmp; 
		}
		if(ret.area > initialPath.area){
			initialPath = ret;
		}
		return ret;
	}
	
	/*********************
	  
	 Method: void printInitialPath();
	 Brief: Prints the initial path.
	 
	*********************/
	
	public static void printInitialPath(){
		for(int i = 0; i < initialPath.apath.size(); ++i){
			System.out.print(initialPath.apath.get(i).id + " ");
		}
		System.out.println();
	}
	
	/*********************
	  
	 Method: void constructInitialPath();
	 Brief: Constructs the path found in findInitialPath().
	 
	*********************/

	public static void constructInitialPath(){
		if(initialPath.apath.size() > 1){
			curRegion = initialPath.apath.get(0);
			nextRegion = initialPath.apath.get(1);
		}
		else{
			curRegion = initialPath.apath.get(0);
			nextRegion = initialPath.apath.get(0);
		}
		finalPath.add(new PathPart(Point.NULL, Point.grid[0][0]));
		Point.grid[0][0].isObstacle = true;
		while(true){
			Point lastPoint = finalPath.getLast().last;
			lastPoint.getNeighbors();
			if(lastPoint.numFree == 0) break;
			addToPath();
			finalPath.getLast().last.isObstacle = true;
			finalPath.getLast().last.inPath = true;
		}
		finalPath.getLast().last.getNeighbors();
	}
	
	/*********************
	  
	 Method: void addToPath();
	 Brief: Adds another PathPart to the final path.
	 
	*********************/
	
	public static void addToPath(){
		int direction = -1;
		Point lastPoint = finalPath.getLast().last;
		char c = finalPath.getLast().direction.charAt(0);
		if(c == 'U') direction = 0;
		if(c == 'R' || c == ' ') direction = 1;
		if(c == 'D') direction = 2;
		if(c == 'L') direction = 3;
		for(int i = direction - 1; i < direction + 2; ++i){
			int x=(i+4)%4;
			if(x==0){
				if(canAdd(lastPoint.up)){
					direction=0;
					if(lastPoint.up.id == nextRegion.id){
						curRegionIndex++;
						curRegion = nextRegion;
						if(curRegionIndex < initialPath.apath.size() - 1) nextRegion = initialPath.apath.get(curRegionIndex + 1);
					}
					break;
				}
			}
			else if(x==1){
				if(canAdd(lastPoint.right)){
					direction=1;
					if(lastPoint.right.id == nextRegion.id){
						curRegionIndex++;
						curRegion = nextRegion;
						if(curRegionIndex < initialPath.apath.size() - 1) nextRegion = initialPath.apath.get(curRegionIndex + 1);
					}
					break;
				}
			}
			else if(x==2){
				if(canAdd(lastPoint.down)){
					direction=2;
					if(lastPoint.down.id == nextRegion.id){
						curRegionIndex++;
						curRegion = nextRegion;
						if(curRegionIndex < initialPath.apath.size() - 1) nextRegion = initialPath.apath.get(curRegionIndex + 1);
					}
					break;
				}
			}
			else{
				if(canAdd(lastPoint.left)){
					direction=3;
					if(lastPoint.left.id == nextRegion.id){
						curRegionIndex++;
						curRegion = nextRegion;
						if(curRegionIndex < initialPath.apath.size() - 1) nextRegion = initialPath.apath.get(curRegionIndex + 1);
					}
					break;
				}
			}
		}
		if(direction == 0) finalPath.add(new PathPart(lastPoint, lastPoint.up));
		else if(direction == 1)	finalPath.add(new PathPart(lastPoint, lastPoint.right));	
		else if(direction == 2)	finalPath.add(new PathPart(lastPoint, lastPoint.down));
		else finalPath.add(new PathPart(lastPoint, lastPoint.left));
	}
	
	/*********************
	  
	 Method: boolean canAdd(Point p);
	 Brief: Determines whether this point can be added to the final path.
	 
	*********************/
	
	public static boolean canAdd(Point p){
		return !p.isObstacle && (p.id == curRegion.id || p.id == nextRegion.id || (curRegion.id == nextRegion.id));
	}
	
	/*********************
	  
	 Method: void extendPath();
	 Brief: Extends this path to make the final path.
	 
	*********************/

	public static void extendPath(){
		i = finalPath.listIterator();
		i.next();
		while(i.hasNext()){
			PathPart pp = i.next();
			char direction = pp.direction.charAt(0);
			if(direction == ' '){
				pp = i.next();
				direction = pp.direction.charAt(0);
			}
			if(direction == 'U' || direction == 'D') extendVertical(pp);
			else extendHorizontal(pp);
		}
	}
	
	/*********************
	  
	 Method: void extendVertical(PathPart pp);
	 Brief: Attempts to extend a path segment going in the vertical direction.
	 
	*********************/
	
	public static void extendVertical(PathPart pp){
		Point first = pp.first;
		Point second = pp.last;
		if(!first.left.isObstacle && !second.left.isObstacle){
			i.previous();
			PathPart one = new PathPart(first, first.left);
			i.add(one);
			PathPart two = new PathPart(first.left, second.left);
			first.left.isObstacle = true;
			second.left.isObstacle = true;
			first.left.inPath = true;
			second.left.inPath = true;
			i.next();
			i.set(two);
			PathPart three = new PathPart(second.left, second);
			i.add(three);
			for(int j = 0; j < 4; ++j){
				i.previous();
			}
		}
		if(!first.right.isObstacle && !second.right.isObstacle){
			i.previous();
			PathPart one = new PathPart(first, first.right);
			i.add(one);
			PathPart two = new PathPart(first.right, second.right);
			first.right.isObstacle = true;
			second.right.isObstacle = true;
			first.right.inPath = true;
			second.right.inPath = true;
			i.next();
			i.set(two);
			PathPart three = new PathPart(second.right, second);
			i.add(three);
			for(int j = 0; j < 4; ++j){
				i.previous();
			}
		}
	}
	
	/*********************
	  
	 Method: void extendHorizontal();
	 Brief: Attempts to extend a path segment going in the horizontal direction.
	 
	*********************/
	
	public static void extendHorizontal(PathPart pp){
		Point first = pp.first;
		Point second = pp.last;
		if(!first.up.isObstacle && !second.up.isObstacle){
			i.previous();
			PathPart one = new PathPart(first, first.up);
			i.add(one);
			PathPart two = new PathPart(first.up, second.up);
			first.up.isObstacle = true;
			second.up.isObstacle = true;
			first.up.inPath = true;
			second.up.inPath = true;
			i.next();
			i.set(two);
			PathPart three = new PathPart(second.up, second);
			i.add(three);
			for(int j = 0; j < 4; ++j){
				i.previous();
			}
		}
		if(!first.down.isObstacle && !second.down.isObstacle){
			i.previous();
			PathPart one = new PathPart(first, first.down);
			i.add(one);
			PathPart two = new PathPart(first.down, second.down);
			first.down.isObstacle = true;
			second.down.isObstacle = true;
			first.down.inPath = true;
			second.down.inPath = true;
			i.next();
			i.set(two);
			PathPart three = new PathPart(second.down, second);
			i.add(three);
			for(int j = 0; j < 4; ++j){
				i.previous();
			}
		}
	}
	
	/*********************
	  
	 Method: void printOutput();
	 Brief: Prints information like the length of the path and time elasped.
	 * @throws IOException 
	 
	*********************/
	
	public static void printOutput() throws IOException{
		//printFinalPath();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("rawOutput")));
		long elapsed = System.currentTimeMillis() - start;
		/*System.out.println("Time elapsed: " + elapsed);
		System.out.println("Length of potential path: " + initialPath.area);
		System.out.println("Length of actual path: " + finalPath.size());
		System.out.println("Total area: " + totalArea);
		System.out.println("Area travelled: " + (100*(double)finalPath.size()/(double)initialPath.area) + "%");*/
		out.println(elapsed);
		out.println(100*(double)finalPath.size()/(double)initialPath.area);
		out.println(initialPath.area);
		out.close();
		System.out.println(initialPath.area);
	}
	
	/*********************
	  
	 Method: void printFinalPath();
	 Brief: Prints the final path.
	 
	*********************/
	
	public static void printFinalPath(){
		for(int i = 0; i < m; ++i){
			System.out.println();
			for(int j = 0; j < n; ++j){
				if(Point.grid[i][j].isObstacle){
					if(Point.grid[i][j].inPath) System.out.print("A");
					else System.out.print("X");
				}
				else{
					System.out.print("0");
				}
			}
		}
		System.out.println();
	}
	
}
