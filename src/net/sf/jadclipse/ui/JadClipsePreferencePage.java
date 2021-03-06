package net.sf.jadclipse.ui;

import net.sf.jadclipse.JadDecompiler;
import net.sf.jadclipse.JadclipsePlugin;
import net.sf.jadclipse.opengrock.Utils;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
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
 * @author V.Grishchenko, Kamal Essajidi kaessajidi@outlook.fr
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

		
		String[] grogrockURLs = Platform.getResourceBundle(JadclipsePlugin.getDefault().getBundle()).getString(JadclipsePlugin.GROCK_URLS).split(",");
		String[] whereuserdURLs = Platform.getResourceBundle(JadclipsePlugin.getDefault().getBundle()).getString(JadclipsePlugin.WUSED_URLS).split(",");
		
		StringChoiceFieldEditor ptcsrcUrl = new StringChoiceFieldEditor(JadclipsePlugin.PTC_URL, "Sources Url ", grogrockURLs, getFieldEditorParent());
		ptcsrcUrl.addItems(whereuserdURLs);
		addField(ptcsrcUrl);
		ptcsrcUrl.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				choice.removeItems();
				Combo obj = ((Combo) e.getSource());
				String[][] fKeys = Utils.getGrockProjects(obj.getItem(obj.getSelectionIndex()));
				choice.addItems(fKeys);
			}

		});

		choice = new StringChoiceFieldEditor(JadclipsePlugin.GROCK_PROJECT, "Sources Project", Utils.getGrockProjects(null), getFieldEditorParent());
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