package net.sf.jadclipse.ui;


import net.sf.jadclipse.CommandOption;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * work in progress...
 * @author V.G.
 */
public class ToggleOptionEditor extends CommandLineOptionEditor {
	Button cbox;
	
	/**
	 * @param optionName
	 * @param parent
	 */
	public ToggleOptionEditor(String optionName, String label, Composite parent) {
		super(optionName, label, parent);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#doAccept(net.sf.jadclipse.CommandOption)
	 */
	protected void doAccept(CommandOption option) {
		boolean select = false;
		if (option != null) {
			select = true;
			if (option.getValue() != null && getPreferencePage() != null) {
				getPreferencePage().setErrorMessage("Option " + getOptionName() + " does not support values");
			}
		}
		cbox.setSelection(select);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		cbox = new Button(parent, SWT.CHECK);
		cbox.setText(getLabel());
		cbox.setLayoutData(new GridData());
	}

	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#toOption()
	 */
	public CommandOption toOption() {
		return cbox.getSelection() ? new CommandOption(getOptionName(), null) : null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		((GridData)cbox.getLayoutData()).horizontalSpan = numColumns;
	}

}
