package net.sf.jadclipse;

/**
 * Numeric option's value is an int.
 * 
 * Work in progress...
 * 
 * @author V.G.
 */
public class NumericOption extends CommandOption {
	private int numericValue;
	
	/**
	 * @param name
	 * @param value
	 */
	public NumericOption(String name, int numericValue) {
		super(name, String.valueOf(numericValue));
		this.numericValue = numericValue;
	}
	
	/**
	 * @return
	 */
	public int getNumericValue() {
		return numericValue;
	}
	
	/** 
	 * No whitespace is inserted between option name and value.
	 * @see net.sf.jadclipse.CommandOption#toString()
	 */
	public String toString() {
		return new StringBuffer(getName()).append(getValue()).toString();
	}

}
