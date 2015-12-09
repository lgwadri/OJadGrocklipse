package net.sf.jadclipse;

/**
 * A command option that has a name and an optional value.
 * 
 * Work in progress...
 * 
 * @author V.G.
 */
public class CommandOption {
	private String name;
	private String value;
	
	public CommandOption(String name, String value) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("bogus option");
		}
		this.name = name;
		this.value = value;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		if (value != null) {
			buf.append(" ").append(value);
		}
		return buf.toString();
	}

}
