package columbus.refactorings.model.makearraycopy;

public class MakeArrayCopyExample {
    private Object[] array= {new Object(),new Object(),new Object()};
    private int[] arrayInt = {1,2,3};
    
    public void foo(Object[] x){    
        this.array = x;
    }
    
    public void boo(int[] y){   
        this.arrayInt = y;
    }
    
    public void print(){
       System.out.println("");
    }
}
