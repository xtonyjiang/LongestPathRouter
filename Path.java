import java.util.*;

public class Path {
	LinkedList<Region> lpath;
	ArrayList<Region> apath;
	int area = 0;
	public Path(){
		lpath = new LinkedList<Region>();
		apath = new ArrayList<Region>();
	}
	public Path(Path p){
		lpath = new LinkedList<Region>();
		apath = new ArrayList<Region>();
		for(int i = 0; i < p.apath.size(); ++i){
			this.add(p.apath.get(i));
		}
	}
	public void add(Region r){
		apath.add(r);
		area += r.area;
	}
	public void copyPath(){
		for(int i = 0; i < apath.size(); ++i){
			lpath.addLast(apath.get(i));
		}
	}
}
