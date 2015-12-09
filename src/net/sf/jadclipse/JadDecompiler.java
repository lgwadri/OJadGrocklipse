package net.sf.jadclipse;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This implementation of <code>IDecompiler</code> uses Jad as the
 * underlying decompler.
 * 
 * @author	V.Grishchenko
 */
public class JadDecompiler implements IDecompiler
{
	public static final String OPTION_ANNOTATE     = "-a";			//format
	public static final String OPTION_ANNOTATE_FQ  = "-af";			//format 
	public static final String OPTION_BRACES       = "-b";			//format
	public static final String OPTION_CLEAR        = "-clear";		//format
	public static final String OPTION_DIR          = "-d";			//	?
	public static final String OPTION_DEAD         = "-dead";		//directives
	public static final String OPTION_DISASSEMBLER = "-dis";		//directives
	public static final String OPTION_FULLNAMES    = "-f";			//format
	public static final String OPTION_FIELDSFIRST  = "-ff";			//format
	public static final String OPTION_DEFINITS     = "-i";			//format
	public static final String OPTION_SPLITSTR_MAX = "-l";			//format
	public static final String OPTION_LNC          = "-lnc";		//debug
	public static final String OPTION_LRADIX       = "-lradix";		//format
	public static final String OPTION_SPLITSTR_NL  = "-nl";			//format
	public static final String OPTION_NOCONV       = "-noconv";		//directives
	public static final String OPTION_NOCAST       = "-nocast";		//directives
	public static final String OPTION_NOCLASS      = "-noclass";	//directives
	public static final String OPTION_NOCODE       = "-nocode";		//directives
	public static final String OPTION_NOCTOR       = "-noctor";		//directives
	public static final String OPTION_NODOS        = "-nodos";		//directives
	public static final String OPTION_NOFLDIS      = "-nofd";		//directives
	public static final String OPTION_NOINNER      = "-noinner";	//directives
	public static final String OPTION_NOLVT        = "-nolvt";		//directives
	public static final String OPTION_NONLB        = "-nonlb";		//format
	public static final String OPTION_OVERWRITE    = "-o";			// 	?
	public static final String OPTION_SENDSTDOUT   = "-p";			//	?	
	public static final String OPTION_PA           = "-pa";			//directives
	public static final String OPTION_PC           = "-pc";			//directives
	public static final String OPTION_PE           = "-pe";			//directives
	public static final String OPTION_PF           = "-pf";			//directives
	public static final String OPTION_PI           = "-pi";			//format
	public static final String OPTION_PL           = "-pl";			//directives
	public static final String OPTION_PM           = "-pm";			//directives
	public static final String OPTION_PP           = "-pp";			//directives
	public static final String OPTION_PV           = "-pv";			//format
	public static final String OPTION_RESTORE      = "-r";			//	?
	public static final String OPTION_IRADIX       = "-radix";		//format
	public static final String OPTION_EXT          = "-s";			//	?
	public static final String OPTION_SAFE         = "-safe";		//directives
	public static final String OPTION_SPACE        = "-space";		//format
	public static final String OPTION_STAT         = "-stat";		//misc
	public static final String OPTION_INDENT_SPACE = "-t";			//format
	public static final String OPTION_INDENT_TAB   = "-t";			//	?
	public static final String OPTION_VERBOSE      = "-v";			//misc
	public static final String OPTION_ANSI	       = "-8";			//misc
	public static final String OPTION_REDSTDERR    = "-&";			//	?
	
	public static final String USE_TAB = "use tab";
	
	public static final String[] TOGGLE_OPTION = 
	{
		OPTION_ANNOTATE,
		OPTION_ANNOTATE_FQ,
		OPTION_BRACES,
		OPTION_CLEAR,		
		OPTION_DEAD,
		OPTION_DISASSEMBLER,
		OPTION_FULLNAMES,
		OPTION_FIELDSFIRST,
		OPTION_DEFINITS,
		OPTION_LNC,
		OPTION_SPLITSTR_NL,
		OPTION_NOCONV,
		OPTION_NOCAST,
		OPTION_NOCLASS,
		OPTION_NOCODE,
		OPTION_NOCTOR,
		OPTION_NODOS,
		OPTION_NOFLDIS,
		OPTION_NOINNER,
		OPTION_NOLVT,
		OPTION_NONLB,
		/* OPTION_OVERWRITE, */
		/* OPTION_SENDSTDOUT, */
		/* OPTION_RESTORE, */
		OPTION_SAFE,
		OPTION_SPACE,
		OPTION_STAT,
		OPTION_INDENT_TAB,
		OPTION_VERBOSE,
		OPTION_ANSI,
		/* OPTION_REDSTDERR */
	};
	
	public static final String[] VALUE_OPTION_STRING = 
	{
		/* OPTION_DIR, */
		OPTION_PA,
		OPTION_PC,
		OPTION_PE,
		OPTION_PF,	
		OPTION_PL,
		OPTION_PM,
		OPTION_PP,
		/* OPTION_EXT, */
	};
	
	public static final String[] VALUE_OPTION_INT = 
	{
		/* OPTION_INDENT_SPACE, */
		OPTION_SPLITSTR_MAX,
		OPTION_LRADIX,
		OPTION_PI,
		OPTION_PV,	
		OPTION_IRADIX,
	};
	
	private String source = "/* ERROR? */";
	private StringBuffer log;
	private List excList = new ArrayList();
	private long time, start;
	

    /**
     * Performs a <code>Runtime.exec()</code> on jad executable with 
     * selected options.
     * 
     * @see IDecompiler#decompile(String, String, String)
     */
    public void decompile(String root, String packege, String className)
    {   	
    	File workingDir = new File(root + "/" + packege);
    	StringWriter output = new StringWriter();
		IPreferenceStore settings = JadclipsePlugin.getDefault().getPreferenceStore();    	
    	Writer decor = (settings.getBoolean(JadDecompiler.OPTION_LNC) && settings.getBoolean(JadclipsePlugin.ALIGN))
    					? new DebugAlignWriter(output) : (Writer) output;
		StringWriter errors = new StringWriter();
		PrintWriter errorsP = new PrintWriter(errors);
		//errorsP.println("\n\n\n/***** DECOMPILE LOG *****\n");
		int status = 0;
		
    	try
    	{
    		start = System.currentTimeMillis();     		
//			errorsP.println("\tDECOMPILED FROM: " 
//								+ ((root.isArchive()) 
//									? ("archive " + root.getPath().toOSString())
//									: ("class file " 
//										+ classFile.getCorrespondingResource().getLocation().toOSString())) + "\n\n");
			errorsP.println("\tJad reported messages/errors:");  	
		    Process p = Runtime.getRuntime().exec(buildCmdLine(className), new String[] {}, workingDir);
			StreamRedirectThread outRedirect = 
				new StreamRedirectThread("output_reader", p.getInputStream(), decor);
			StreamRedirectThread errRedirect = 
				new StreamRedirectThread("error_reader", p.getErrorStream(), errors);
			outRedirect.start();
			errRedirect.start();
			status = p.waitFor();	//wait for jad to finish				
			outRedirect.join();		//wait until output stream content is fully copied
			errRedirect.join();		//wait until error stream content is fully copied
			if (outRedirect.getException() != null) excList.add(outRedirect.getException());
			if (errRedirect.getException() != null) excList.add(errRedirect.getException());			
    	}
    	catch (Exception e)
    	{
    		excList.add(e);
    	}
    	finally
    	{
    		try
    		{
    			decor.flush();
    			decor.close();
		    	errorsP.println("\tExit status: " + status);
		    	//errorsP.print(" *************************/");
		    	errors.flush();	    		
	    		errorsP.close();
    		}
    		catch (Exception e) 
    		{ 
    			excList.add(e); // will never get here...
    		}
			time = System.currentTimeMillis() - start;    		
    	}
    	
      	source = output.toString();
    	log = errors.getBuffer();
    	//logExceptions();
		//result = new DecompiledClassFile(classFile, source.toString());	
    }

    /**
     * Jad doesn't support decompilation from archives. This methods extracts
     * request class file from the specified archive into temp directory and
     * then calls <code>decompile</code>.
     * 
     * @see IDecompiler#decompileFromArchive(String, String, String)
     */
    public void decompileFromArchive(String archivePath, String packege, String className)
    {
    	start = System.currentTimeMillis();
   		File workingDir = new File(JadclipsePlugin.getDefault().getPreferenceStore().getString(JadclipsePlugin.TEMP_DIR) + "/" + System.currentTimeMillis());    	

    	try
    	{
    		workingDir.mkdirs();  
    		JarClassExtractor.extract(archivePath, packege, className, true, workingDir.getAbsolutePath());
    		decompile(workingDir.getAbsolutePath(), "", className);
    	}
    	catch (Exception e)
    	{
    		excList.add(e);
    		//logExceptions();
    		return;
    	}
    	finally
    	{
    		deltree(workingDir);
    	}
    }

    /**
     * @see IDecompiler#getSource()
     */
    public String getSource()
    {
        return source;
    }

    /**
     * @see IDecompiler#getLog()
     */
    public String getLog()
    {
        return log == null ? "" : log.toString();
    }
    
    public List getExceptions()
    {
    	return excList;
    }
    
    public long getDecompilationTime()
    {
    	return time;
    }
    
    private String[] buildCmdLine(String classFileName)
    {
    	ArrayList cmdLine = new ArrayList();
    	IPreferenceStore settings = JadclipsePlugin.getDefault().getPreferenceStore();
    	
    	//command and special options
    	cmdLine.add(settings.getString(JadclipsePlugin.CMD));
    	cmdLine.add(OPTION_SENDSTDOUT);
    	
    	String indent = settings.getString(OPTION_INDENT_SPACE);
    	if (indent.equals(USE_TAB))
    		cmdLine.add(OPTION_INDENT_TAB);
    	else
    	{
    		try
    		{
    			Integer.parseInt(indent);
    			cmdLine.add(OPTION_INDENT_SPACE + indent);
    		}
    		catch (Exception e)
    		{
    		}
    	}
    	
    	//toggles
    	for (int i = 0; i < TOGGLE_OPTION.length; i++)
    	{
    		if (settings.getBoolean(TOGGLE_OPTION[i]))
    			cmdLine.add(TOGGLE_OPTION[i]);
    	}
    	
    	//integers, 0 means disabled
    	int iValue;
    	for (int i = 0; i < VALUE_OPTION_INT.length; i++)
    	{
    		iValue = settings.getInt(VALUE_OPTION_INT[i]);
    		if (iValue > 0)
    			cmdLine.add(VALUE_OPTION_INT[i] + iValue);
    	}
    	
    	//strings, "" means disabled
    	String sValue;
    	for (int i = 0; i < VALUE_OPTION_STRING.length; i++)
    	{
    		sValue = settings.getString(VALUE_OPTION_STRING[i]);
    		if (sValue != null && sValue.length() > 0)
    			cmdLine.add(VALUE_OPTION_STRING[i] + " " + sValue);
    		
    	}
    	
    	cmdLine.add(classFileName);
    	//debugCmdLine(cmdLine);
    	return (String[]) cmdLine.toArray(new String[cmdLine.size()]);
    	
    } 
    
    void debugCmdLine(List segments) 
    {
    	StringBuffer cmdline = new StringBuffer();
    	for (int i = 0; i < segments.size(); i++)
    		cmdline.append(segments.get(i)).append(" ");
    	System.err.println("-> " + cmdline.toString());    	
    }
    
    
//    private void logExceptions()
//    {
//    	if (log == null) log = new StringBuffer();
//    	log.append("\n\tCAUGHT EXCEPTIONS:\n");
//    	if (excList.size() == 0) return;
//    	StringWriter stackTraces = new StringWriter();
//    	PrintWriter stackTracesP = new PrintWriter(stackTraces);
//    	
//    	for (int i = 0; i < excList.size(); i++)
//    	{
//    		((Exception) excList.get(i)).printStackTrace(stackTracesP);
//    		stackTracesP.println("");
//    	}
//    	
//		stackTracesP.flush();
//		stackTracesP.close();
//    	log.append(stackTraces.toString());
//    }
    
    void deltree(File root)
    {
    	if (root.isFile())
    	{
    		root.delete();
    		return;
    	}
    	
    	File[] children = root.listFiles();
    	for (int i = 0; i < children.length; i++)
    	{
    		deltree(children[i]);
    	}
    	
    	root.delete();
    }      

}

//		OPTION_ANNOTATE,
//		OPTION_ANNOTATE_FQ,
//		OPTION_BRACES,
//		OPTION_CLEAR,
//		OPTION_DIR,
//		OPTION_DEAD,
//		OPTION_DISASSEMBLER,
//		OPTION_FULLNAMES,
//		OPTION_FIELDSFIRST,
//		OPTION_DEFINITS,
//		OPTION_SPLITSTR_MAX,
//		OPTION_LNC,
//		OPTION_LRADIX,
//		OPTION_SPLITSTR_NL,
//		OPTION_NOCONV,
//		OPTION_NOCAST,
//		OPTION_NOCLASS,
//		OPTION_NOCODE,
//		OPTION_NOCTOR,
//		OPTION_NODOS,
//		OPTION_NOFLDIS,
//		OPTION_NOINNER,
//		OPTION_NOLVT,
//		OPTION_NOLB,
//		OPTION_OVERWRITE,
//		OPTION_SENDSTDOUT,
//		OPTION_PA,
//		OPTION_PC,
//		OPTION_PE,
//		OPTION_PF,
//		OPTION_PI,
//		OPTION_PL,
//		OPTION_PM,
//		OPTION_PP,
//		OPTION_PV,
//		OPTION_RESTORE,
//		OPTION_IRADIX,
//		OPTION_EXT,
//		OPTION_SAFE,
//		OPTION_SPACE,
//		OPTION_STAT,
//		OPTION_INDENT_SPACE,
//		OPTION_INDENT_TAB,
//		OPTION_VERBOSE,
//		OPTION_ANSI,
//		OPTION_REDSTDERR