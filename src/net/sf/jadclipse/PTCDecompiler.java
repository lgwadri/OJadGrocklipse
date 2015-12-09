/**
 * 
 */
package net.sf.jadclipse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author kamal
 *
 */
public class PTCDecompiler implements IDecompiler {
	
	private String source = "";
	private StringBuffer log;
	private List excList = new ArrayList();
	private long time, start;

	/**
	 * 
	 */
	public PTCDecompiler() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.sf.jadclipse.IDecompiler#decompile(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void decompile(String root, String packege, String className) {
		
		IPreferenceStore prefs = JadclipsePlugin.getDefault().getPreferenceStore();    	
		String srcUrl = prefs.getString(JadclipsePlugin.PTC_URL);
		String srcVersion = prefs.getString(JadclipsePlugin.PTC_VERSION);
		String repositoryType = null;
		try {
			start = System.currentTimeMillis();   
	        // Construct data
	        String data = URLEncoder.encode(packege + "." + className, "UTF-8");
	        String path = data.replaceAll("\\.", "/") + ".java";
	        data += "&" + URLEncoder.encode("ln", "UTF-8") + "=" + URLEncoder.encode("false", "UTF-8");
	        
	        if(srcUrl.indexOf("ah-grok") != -1)
	        	repositoryType = "opengroc";
	        else
	        	repositoryType = "whereUserd";
	        		
	        // Send data
	        srcUrl = srcUrl.replaceAll("@class@", data).replaceAll("@path@", path);
	        	
	        URL url = new URL( srcUrl );
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	       
	        if(repositoryType.equals("whereUserd"))
        	{
		        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		        wr.write(data);
		        wr.flush();
		    
		        // Get the response
		        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        String line;
		        boolean go = false;
		        
		        while ((line = rd.readLine()) != null) {
		        	
		        		if(go)
		        			source += replaceAll(line) + "\n";       		
		        		
		        		if( line.startsWith("<pre>") )
		        			go = true;
		        		if( line.startsWith("</pre>") )
		        			go = false;	        	
		        		  
		        }
		        
		        wr.close();
		        rd.close();	        
        	} else
        	{
		        String line;
		        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        while ((line = rd.readLine()) != null) {
        			source += line + "\n";       		
		        }
		        rd.close();
        	}
	        
	        
	    } catch (Exception e) {
	    	
	    	e.printStackTrace();
	    	
	    }finally
    	{
			time = System.currentTimeMillis() - start;    		
    	}

	}

	public String replaceAll ( String line){
		String[] replaces  = { 
		 ">",
		 "<",
		 "&",
		 ""
		};
		
		String[] paterns = {
			"&gt;",
			"&lt;",
			"&amp;",			
			"<[\\/\\!]*?[^<>]*?>"			
		};
		
		for (int i = 0; i < paterns.length; i++) {
			line = line.replaceAll(paterns[i], replaces[i]);
		}
		
		return line;
	}
	/* (non-Javadoc)
	 * @see net.sf.jadclipse.IDecompiler#decompileFromArchive(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void decompileFromArchive(String archivePath, String packege,
			String className) {
		// TODO Auto-generated method stub

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
    
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
			String packege = "wt.part";
			String className = "WTPart";
			String srcVersion = "";
			
		 	String data = URLEncoder.encode("class", "UTF-8") + "=" + URLEncoder.encode(packege + "." + className, "UTF-8");
	        data += "&" + URLEncoder.encode("version", "UTF-8") + "=" + URLEncoder.encode(srcVersion, "UTF-8");
	        String msource = "";
	        // Send data
	        URL url = new URL( "http://ah-wused.ptcnet.ptc.com/wus_x-05_M030/wuscs.jsp" + "?" + data );
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	        wr.write(data);
	        wr.flush();
	    
	        // Get the response
	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line;
	        while ((line = rd.readLine()) != null) {
	        	//no html
	        	line = line.replaceAll("<[\\/\\!]*?[^<>]*?>", "");
	        	if( line.startsWith("  ") && line.indexOf('|') != -1 )
	        		msource += line.substring(line.indexOf("| ") + 2) + "\n";
	        }
	        wr.close();
	        rd.close();

	}

}
