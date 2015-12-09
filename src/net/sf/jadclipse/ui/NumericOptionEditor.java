package net.sf.jadclipse.ui;


import net.sf.jadclipse.CommandOption;

import org.eclipse.swt.widgets.Composite;

/**
 * work in progress...
 * @author V.G.
 */
public class NumericOptionEditor extends CommandLineOptionEditor {

	/**
	 * @param optionName
	 * @param parent
	 */
	public NumericOptionEditor(String optionName, String label, Composite parent) {
		super(optionName, label, parent);
	}
	
	public NumericOptionEditor(String optionName, String label, Composite parent, int[] allowedValues) {
		super(optionName, label, parent);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#doAccept(net.sf.jadclipse.CommandOption)
	 */
	protected void doAccept(CommandOption option) {
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#toOption()
	 */
	public CommandOption toOption() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		// TODO Auto-generated method stub

	}

}
