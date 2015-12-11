package net.sf.jadclipse.ui;

import net.sf.jadclipse.JadDecompiler;
import net.sf.jadclipse.JadclipsePlugin;
import net.sf.jadclipse.opengrock.Utils;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Main preference page
 * 
 * @author V.Grishchenko
 */
public class JadClipsePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected StringChoiceFieldEditor choice;

	public JadClipsePreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(JadclipsePlugin.getDefault().getPreferenceStore());
	}

	/**
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
	}

	/**
	 * @see FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {
		// command line
		StringFieldEditor cmd = new StringFieldEditor(JadclipsePlugin.CMD, "Path to decompiler:", getFieldEditorParent());
		cmd.setEmptyStringAllowed(false);
		addField(cmd);

		// working dir
		StringFieldEditor tempd = new StringFieldEditor(JadclipsePlugin.TEMP_DIR, "Directory for temporary files:", getFieldEditorParent());
		tempd.setEmptyStringAllowed(false);
		addField(tempd);
				
			
		
		StringChoiceFieldEditor ptcsrcUrl = new StringChoiceFieldEditor(JadclipsePlugin.PTC_URL, "Grock Url ", getFieldEditorParent());
		ptcsrcUrl.addItem("http://ah-opengrok.ptcnet.ptc.com/", "http://ah-opengrok.ptcnet.ptc.com/");
		ptcsrcUrl.addItem("http://bla-grok-01/", "http://bla-grok-01/");
		ptcsrcUrl.addItem("http://ah-wused.ptcnet.ptc.com", "http://ah-wused.ptcnet.ptc.com");
		addField(ptcsrcUrl);
		ptcsrcUrl.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
            	choice.removeItems();
            	Combo obj = ((Combo)e.getSource());
            	String[][] fKeys = Utils.getGrockProjects(obj.getItem(obj.getSelectionIndex()));
            	for (int i = 0; i < fKeys.length; i++)
            		choice.addItem(fKeys[i][0], fKeys[i][1]);
            }

        });
		
		
		String[][] projects = Utils.getGrockProjects(null);
		//ComboFieldEditor ptcsrcUrlList = new ComboFieldEditor(JadclipsePlugin.GROCK_PROJECT, "Grock Projects", projects, getFieldEditorParent());
		//addField(ptcsrcUrlList);		
		choice = new StringChoiceFieldEditor(JadclipsePlugin.GROCK_PROJECT, "Grock Projects", getFieldEditorParent());
    	for (int i = 0; i < projects.length; i++)
    		choice.addItem(projects[i][0], projects[i][1]);
		addField(choice);
		
		
		BooleanFieldEditor ignoreptcsrc = new BooleanFieldEditor(JadclipsePlugin.IGNORE_PTCSRC, "Ignore PTC web sources if existing", getFieldEditorParent());
		addField(ignoreptcsrc);
		BooleanFieldEditor reusebuf = new BooleanFieldEditor(JadclipsePlugin.REUSE_BUFFER, "Reuse code buffer", getFieldEditorParent());
		addField(reusebuf);

		BooleanFieldEditor alwaysUse = new BooleanFieldEditor(JadclipsePlugin.IGNORE_EXISTING, "Ignore existing source", getFieldEditorParent());
		addField(alwaysUse);

		BooleanFieldEditor eclipseFormatter = new BooleanFieldEditor(JadclipsePlugin.USE_ECLIPSE_FORMATTER, "Use Eclipse code formatter (overrides Jad formatting instructions).",
				getFieldEditorParent());
		addField(eclipseFormatter);
	}

	/**
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench arg0) {
	}

}