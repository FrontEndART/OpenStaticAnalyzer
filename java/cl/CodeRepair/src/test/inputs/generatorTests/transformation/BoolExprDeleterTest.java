package booleanexpressiondeletetest;
public class BoolExprDeleterTest {
	public static void main(String[] args){
		String asd = "10";
		
		//false
		if(asd.equals("11") && args != null && 10 != Integer.valueOf(asd).intValue()){
			asd = "200";
		}
		
		//true
		if(asd.equals("10") || args != null || 10 != Integer.valueOf(asd).intValue()){
			asd = "20";
		}
		
		//true
		if(!(!asd.equals("10") && asd.equals("10")) || asd.equals("10")){
			asd += "0";
		}
	}
}
