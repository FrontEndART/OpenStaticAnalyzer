package b;
import java.util.Set;
import a.Mutable;

class B {
	public static void main(String args[])
	{
		Set<String> mySet = Mutable.mySet;
		mySet.add("Hihi");
		String array[] = Mutable.array;
		array[0]="Hiszti";
	}
} 