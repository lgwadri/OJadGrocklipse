package net.sf.jadclipse.ui;


import net.sf.jadclipse.JadDecompiler;
import net.sf.jadclipse.JadclipsePlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Misc. preference page
 * 
 * @author	V.Grishchenko
 */
public class JadClipsePreferencePageMisc
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{
	public JadClipsePreferencePageMisc()
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
    protected void createFieldEditors()
    {
		addField(new BooleanFieldEditor(JadDecompiler.OPTION_STAT,
											"Show the total number of processed classes/methods/fields",
												getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(JadDecompiler.OPTION_VERBOSE,
											"Show method names while decompiling",
												getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(JadDecompiler.OPTION_ANSI,
											"Convert Unicode strings into ANSI strings",
												getFieldEditorParent()));
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench arg0)
    {
    }
}

//	String OPTION_STAT         = "-stat";		//misc
//	String OPTION_VERBOSE      = "-v";			//misc
//	String OPTION_ANSI	       = "-8";			//misc
	