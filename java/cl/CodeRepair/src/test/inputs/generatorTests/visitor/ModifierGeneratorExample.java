package sourcecodegeneratortest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

public @Generated(value={}) final 
@Deprecated class /** Probafejlec */
ModifierGeneratorExample<T extends List<String> & Cloneable> 
extends ArrayList<T> //Cloneable 
implements Cloneable, Serializable {

	private static final long serialVersionUID = 1836296845438623443L;

	private @Deprecated 
	volatile //volatile 
	T adattag;
	
	@Generated(value = { "" })
	public final /* a wild comment
	 appeared */ static String Pelda(){
		return "";
	}  
	
	/**
	 * Egy rendhagyobb pelda. 
	 */
	@Override
	public Object clone(){
		return super.clone();
	}
}
