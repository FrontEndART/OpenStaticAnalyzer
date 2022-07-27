public class ReturningArray {
	private static int array[] = {1, 2, 3, 5, 8, 13};
	public static int[] get() {
		return array;
	}
	
	public static void main(String args[]) {}
}

class User {
	public static void toDo() {
		ReturningArray.get()[2]=2;
	}
}