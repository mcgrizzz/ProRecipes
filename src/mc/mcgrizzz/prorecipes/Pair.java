package mc.mcgrizzz.prorecipes;

public class Pair<A, B> {
	
	A valueA;
	B valueB;
	
	public Pair(A valueA, B valueB){
		this.valueA = valueA;
		this.valueB = valueB;
	}
	
	public void setA(A a){
		valueA = a;
	}
	
	public void setB(B b){
		valueB = b;
	}
	
	public A getA(){
		return valueA;
	}
	
	public B getB(){
		return valueB;
	}
	
}
