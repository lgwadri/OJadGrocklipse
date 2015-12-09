package net.sf.jadclipse.ui;


import net.sf.jadclipse.CommandLine;
import net.sf.jadclipse.CommandOption;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * work in progress...
 * @author V.G.
 */
public abstract class CommandLineOptionEditor {
	private String optionName;
	private String label;
	private PreferencePage preferencePage;
	private Composite parent;
	
	protected CommandLineOptionEditor() {
	}

	public CommandLineOptionEditor(String optionName, String label, Composite parent) {
		init(optionName, label);
		createControl(parent);
	}
	
	protected void init(String optionName, String label) {
		if (optionName == null) {
			throw new IllegalArgumentException("null option name");
		}
		this.optionName = optionName;
		this.label = (label != null) ? label : optionName;
	}
	
	protected abstract void doFillIntoGrid(Composite parent, int numColumns);
	protected abstract void adjustForNumColumns(int numColumns);
	public abstract CommandOption toOption();
	public abstract int getNumberOfControls();
	
	protected void createControl(Composite parent) {
		this.parent = parent;
		GridLayout layout = new GridLayout();
		layout.numColumns = getNumberOfControls();
		//layout.marginWidth = 0;
		//layout.marginHeight = 0;
		layout.makeColumnsEqualWidth = false;
		parent.setLayout(layout);
		doFillIntoGrid(parent, layout.numColumns);
	}
	
	protected void doAccept(CommandOption option) {
	}

	/**
	 * @return
	 */
	public PreferencePage getPreferencePage() {
		return preferencePage;
	}

	/**
	 * @param page
	 */
	public void setPreferencePage(PreferencePage page) {
		this.preferencePage = page;
	}
	
	public void accept(CommandLine cmdLine) {
		doAccept(cmdLine.getOption(optionName));
	}

	/**
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return
	 */
	public String getOptionName() {
		return optionName;
	}
	
	public Composite getParent() {
		return parent; 
	}

}
