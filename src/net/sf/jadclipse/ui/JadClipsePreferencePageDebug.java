package net.sf.jadclipse.ui;


import net.sf.jadclipse.JadDecompiler;
import net.sf.jadclipse.JadclipsePlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Debug preference page
 * 
 * @author	V.Grishchenko
 */
public class JadClipsePreferencePageDebug
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

	private BooleanFieldEditor optionLncEditor;
	private BooleanFieldEditor alignEditor;
	
	public JadClipsePreferencePageDebug()
	{
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(JadclipsePlugin.getDefault().getPreferenceStore());
	}
	
	/**
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) 
	{
		super.createControl(parent);
		//WorkbenchHelp.setHelp(getControl(), new DialogPageContextComputer(this, IJavaHelpContextIds.JAVA_EDITOR_PREFERENCE_PAGE));
	}	

    /**
	 * @see FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {
		optionLncEditor = new BooleanFieldEditor(JadDecompiler.OPTION_LNC,
				"Output original line numbers as comments",
				getFieldEditorParent());
		addField(optionLncEditor);

		alignEditor = new BooleanFieldEditor(JadclipsePlugin.ALIGN,
				"Align code for debugging", getFieldEditorParent());
		addField(alignEditor);
	}

    /**
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
    public void init(IWorkbench arg0)
    {
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#initialize()
	 */
	protected void initialize() {
		super.initialize();
		boolean enabled = getPreferenceStore().getBoolean(JadDecompiler.OPTION_LNC);
		alignEditor.setEnabled(enabled, getFieldEditorParent());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == optionLncEditor) {
            boolean enabled = event.getNewValue().equals(Boolean.TRUE);
            alignEditor.setEnabled(enabled, getFieldEditorParent());
        }
		super.propertyChange(event);
	}

}
