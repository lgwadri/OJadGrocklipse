package net.sf.jadclipse.ui;


import net.sf.jadclipse.CommandOption;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * work in progress...
 * @author V.G.
 */
public class StringOptionEditor extends CommandLineOptionEditor {
	public static int ALLOW_EMPTY = 1; 
	public static int ALLOW_WS    = 2;
	  
	private Text value;
	private int allowMask;

	/**
	 * @param optionName
	 * @param parent
	 */
	public StringOptionEditor(String optionName, String label, int allowMask, Composite parent) {
		this.allowMask = allowMask;
		init(optionName, label);
		createControl(parent);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#doAccept(net.sf.jadclipse.CommandOption)
	 */
	protected void doAccept(CommandOption option) {
		if (option == null) {
			value.setText("");
		}
		else {
			String val = option.getValue();
			if (val != null && (val = val.trim()).length() > 0) {
				value.setText(val);
				if ((allowMask & ALLOW_WS) == 0 
						&& (val.indexOf(' ') > 0 || val.indexOf('\t') > 0)
						&& getPreferencePage() != null) {
					 getPreferencePage().setErrorMessage("Option " + getOptionName() + " does not support whitespace");
				}
			}
			else {
				value.setText("");
				if ((allowMask & ALLOW_EMPTY) == 0 && getPreferencePage() != null) {
					getPreferencePage().setErrorMessage("Option " + getOptionName() + " must have a value");
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(getLabel());
		value = new Text(parent, SWT.BORDER);
		value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		return 2;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#toOption()
	 */
	public CommandOption toOption() {
		String val = value.getText();
		if (val != null && (val = val.trim()).length() > 0) {
			if ((val.indexOf(' ') < 0 && val.indexOf('\t') < 0)
					|| (allowMask & ALLOW_WS) != 0) {
				return new CommandOption(getOptionName(), val);
			}
		}
		if ((allowMask & ALLOW_EMPTY) != 0) {
			return new CommandOption(getOptionName(), null);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.ui.CommandLineOptionEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		GridData gd = (GridData)value.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;// (gd.horizontalSpan == 1);
	}


}
