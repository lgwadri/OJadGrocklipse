package net.sf.jadclipse.ui;


import net.sf.jadclipse.JadDecompiler;
import net.sf.jadclipse.JadclipsePlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Formatting preference page
 * 
 * @author	V.Grishchenko
 */
public class JadClipsePreferencePageFormat
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{
	public JadClipsePreferencePageFormat()
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
	}	

    /**
     * @see FieldEditorPreferencePage#createFieldEditors()
     */
    protected void createFieldEditors()
    {
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_ANNOTATE, 
						    				"Generate JVM instructions as comments", 
    											getFieldEditorParent()));
    			
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_ANNOTATE_FQ, 
    										"Output fully qualified names when annotating", 
    											getFieldEditorParent()));
    									
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_BRACES, 
    										"Generate redundant braces", 
    											getFieldEditorParent()));
    									
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_CLEAR, 
    										"Clear all prefixes", 
    											getFieldEditorParent()));
    									
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_FULLNAMES, 
    										"Generate fully qualified names", 
    											getFieldEditorParent()));
    									
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_FIELDSFIRST, 
    										"Output fields before methods", 
    											getFieldEditorParent()));
    									
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_DEFINITS, 
    										"Print default initializers for fields", 
    											getFieldEditorParent()));

    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_NONLB, 
    										"Don't insert a newline before opening brace", 
    											getFieldEditorParent()));    
    									
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_SPACE, 
    										"Output space between keyword (if, while, etc) and expression", 
    											getFieldEditorParent())); 
    									
    	addField(new BooleanFieldEditor(JadDecompiler.OPTION_SPLITSTR_NL, 
    										"Split strings on newline characters", 
    											getFieldEditorParent()));  

    	IntegerFieldEditor splitstr =
    		 new IntegerFieldEditor(JadDecompiler.OPTION_SPLITSTR_MAX, 
    										"Split strings into pieces of max chars (0=dis)", 
    											getFieldEditorParent());
//    	splitstr.addItem("disable", "-1");
//    	splitstr.addItem("10", "10");
//		splitstr.addItem("20", "20");
//		splitstr.addItem("30", "30");
//		splitstr.addItem("40", "40");
//		splitstr.addItem("50", "50");
//		splitstr.addItem("60", "60");
//		splitstr.addItem("70", "70");
//		splitstr.addItem("80", "80");
//		splitstr.addItem("90", "90");
//		splitstr.addItem("100", "100");
		addField(splitstr);
    									
    									
		StringChoiceFieldEditor iradix = 
			new StringChoiceFieldEditor(JadDecompiler.OPTION_IRADIX, 
											"Display integers using the specified radix", 
												getFieldEditorParent());
		iradix.addItem("8", "8");
		iradix.addItem("10", "10");
		iradix.addItem("16", "16");
		addField(iradix);
		
		StringChoiceFieldEditor lradix = 
			new StringChoiceFieldEditor(JadDecompiler.OPTION_LRADIX, 
											"Display long integers using the specified radix", 
												getFieldEditorParent());
		lradix.addItem("8", "8");
		lradix.addItem("10", "10");
		lradix.addItem("16", "16");
		addField(lradix);
		


    	addField(new IntegerFieldEditor(JadDecompiler.OPTION_PI, 
    										"Pack imports into one line using .* (0=dis)", 
    											getFieldEditorParent()));
    	
    	addField(new IntegerFieldEditor(JadDecompiler.OPTION_PV, 
    										"Pack fields with the same types into one line (0=dis)", 
    											getFieldEditorParent()));

		StringChoiceFieldEditor indent =
			new StringChoiceFieldEditor(JadDecompiler.OPTION_INDENT_SPACE, 
											"Number of spaces for indentation: ", 
												getFieldEditorParent());
		indent.addItem("tab", JadDecompiler.USE_TAB);
		indent.addItem("0", "0");
		indent.addItem("1", "1");
		indent.addItem("2", "2");
		indent.addItem("3", "3");
		indent.addItem("4", "4");
		indent.addItem("5", "5");
		indent.addItem("6", "6");
		indent.addItem("7", "7");
		indent.addItem("8", "8");
		indent.addItem("9", "9");
		indent.addItem("10", "10");
		indent.addItem("11", "11");
		indent.addItem("12", "12");
		indent.addItem("13", "13");
		indent.addItem("14", "14");
		indent.addItem("15", "15");
    	addField(indent);
    }

    /**
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench arg0)
    {
    }
}

//	OPTION_ANNOTATE     = "-a";			//format
//	OPTION_ANNOTATE_FQ  = "-af";		//format 
//	OPTION_BRACES       = "-b";			//format
//	OPTION_CLEAR        = "-clear";		//format
//	OPTION_FULLNAMES    = "-f";			//format
//	OPTION_FIELDSFIRST  = "-ff";		//format
//	OPTION_DEFINITS     = "-i";			//format
//	OPTION_SPLITSTR_MAX = "-l";			//format
//	OPTION_LRADIX       = "-lradix";	//format
//	OPTION_SPLITSTR_NL  = "-nl";		//format
//	OPTION_NONLB        = "-nonlb";		//format	
//	OPTION_PI           = "-pi";		//format
//	OPTION_PV           = "-pv";		//format
//	OPTION_IRADIX       = "-radix";		//format
//	OPTION_SPACE        = "-space";		//format
//	OPTION_INDENT_SPACE = "-t";			//format					
