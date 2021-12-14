class NullPathTest {
    public static int foo(String str) {
        String s = str.intern();
        for (int i=0; i < str.length(); i++) {
	   System.out.println(s);
        }
        
	return str.length();        
    }

    public static void main(String args[]) {
       if (args.length == 1)
	       foo(null);
       else foo("Not null");
    }
}