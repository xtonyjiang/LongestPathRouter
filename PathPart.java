
public class PathPart {
	Point first;
	Point last;
	String direction="?";
	public PathPart(Point f, Point dest){
		first = f;
		last = dest;
		if(f == Point.NULL){
			direction = " ";
		}
		else if(first.row==last.row){
			if(last.column-first.column==1) direction="R";
			else direction="L";
		}
		else{
			if(last.row-first.row==1) direction="D";
			else direction="U";
		}
	}
	public String toString(){
		return direction;
	}
}
