package sourcecodegeneratortest;

import java.lang.annotation.Documented;
import javax.annotation.Generated;

@ClassPreamble( author = "csiszi", date = "ma", reviewers = {"Csiszi, Snajczo"})
@SuppressWarnings(value = "unchecked")
public class StatementGeneratorExample {
	public enum Vmi {
		EGYIK,
		MASIK,
		HARMADIK
	}
	
	@Generated(value = { "asd, qew" })
	public static class InnerClassTest<T> extends StatementGeneratorExample {
		public void overridedMethodGeneratorTest(@Generated(value = { "" }) String asd){}
	}
	
	public static class GenericAncientGeneratorTest extends InnerClassTest<String> {
		@Override
		public void overridedMethodGeneratorTest(@Generated(value = { "" }) String asd){}
	}
	
	/**
	 * javadoc Comment
	 * 
	 * This is a Javadoc comment, for testing.
	 */
	public static void main(String args[]){
		
		//Array generator test.
		int[][][][] asd = new int[1][2][][];
		int[][] asd2 = new int[][]{{1,2,3},{4,4,4},{5,6,5}};
		String[] asd3 = new String[]{"", "asd", "asd3"}; 
		
		//Instanceof generator test.
		GenericAncientGeneratorTest ac = new GenericAncientGeneratorTest();
		
		if(ac instanceof GenericAncientGeneratorTest)
			System.out.println("Another");
		
		if(ac instanceof InnerClassTest<?>)
			System.out.println("Other");
		
		//Comment and initializer generator test.
		int index = 10;
		int realSize = 15;
		int array[] = new int[realSize]; 
		
		for(int i = 0; i < realSize; i++){
			array[i] = i;
		}
		
		/* This is a nonjavadoc comment for testing */
		System.arraycopy(array, index + 1, array, index, realSize - index - 1);
		array[realSize-1] = 0;
		
		/*
		 * another non-javadoc comment 
		 */
		for(int i = 0; i < realSize; i++){
			System.out.println(array[i]);
		}
		
		//Label and for loop generator test.
		int j = 0;
		for(; j < realSize; j++){
			System.out.println(array[j]);
		}
		
		aktFor:
		for(j = 0; ; j++){
			if(j < realSize)
				break aktFor;
			
			System.out.println(array[j]);
		}
		
		//Test for original tabulated code generation.
		masikFor:
		for(j = 0; ;){
			if(j < realSize)
				break
				
				masikFor;
			
			System.out
			.
			println  (   array
			[          j++         ]   );
		}
		
		for(;;){
			if(j < realSize)
				break;
			
			System.out.println(array[j++]);
		}
		
		synchronized (StatementGeneratorExample
		.
		class) { 
			for(int i : array){
				System.out.println(i);
			}
	    }
		
		do {
			int i = 0;
		} while(index != 0);
	}
}
