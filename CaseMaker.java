import java.io.*;
import java.util.*;

public class CaseMaker {
	static int n, m, density;
	public static void main(String args[]) throws IOException{
		BufferedReader f=new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("input.in")));
		StringTokenizer st=new StringTokenizer(f.readLine());
		m=Integer.parseInt(st.nextToken());
		n=Integer.parseInt(st.nextToken());
		density=Integer.parseInt(st.nextToken());
		test();
	}
	
	public static void adjust(int x, int d){
		n = x;
		m = x;
		density = d;
	}
	
	public static void test() throws IOException{
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("input.in")));
		out.print(m + " " + n + "\n");
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				if(0<=j&&j<3&&0<=i&&i<3){
					out.print(".");
				}
				else{
					int x=(int)(Math.random()*100);
					if(x<density) out.print("X");
					else out.print(".");
				}
			}
			out.println();
		}
		out.close();
	}
}
