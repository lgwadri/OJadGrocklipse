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
import java.util.Iterator;
import java.util.List;

import net.sf.jadclipse.opengrock.Utils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jadclipse.IDecompiler#decompile(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void decompile(String root, String packege, String className) {

		IPreferenceStore prefs = JadclipsePlugin.getDefault().getPreferenceStore();
		String srcUrl = prefs.getString(JadclipsePlugin.PTC_URL);
		String gproject = prefs.getString(JadclipsePlugin.GROCK_PROJECT);

		try {
			start = System.currentTimeMillis();

			// Construct data
			String data = URLEncoder.encode(packege + "." + className, "UTF-8");
			String path = data.replaceAll("\\.", "/") + ".java";

			if (Utils.isWhereUsedSite(srcUrl)) {
				// Send data
				srcUrl = srcUrl + "/" + gproject + "/viewSource.jsp?class=" + packege + "." + className;
				data += "&" + URLEncoder.encode("ln", "UTF-8") + "=" + URLEncoder.encode("false", "UTF-8");

				System.out.println("Open: " + srcUrl);

				URL url = new URL(srcUrl);
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);

				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				boolean go = false;

				while ((line = rd.readLine()) != null) {

					if (go)
						source += replaceAll(line) + "\n";

					if (line.startsWith("<pre>"))
						go = true;
					if (line.startsWith("</pre>"))
						go = false;
				}

				wr.close();
				rd.close();
			} else {
				String line;
				String search_pattern = srcUrl + "search?q=&project=" + gproject + "&defs=&refs=&path=" + path + "&hist=";

				/*
				 * get the file path source
				 */
				Document doc = Jsoup.connect(search_pattern).get();
				Elements results = doc.getElementById("results").getAllElements();
				for (Element el : results) {
					if (el.tagName().equals("a") && el.parent().className().equals("f")) {
						String class_url = srcUrl + el.attr("href").replaceAll("xref", "opengrok_src");
						System.out.println("Open: " + class_url);
						URL url = new URL(class_url);
						URLConnection conn = url.openConnection();
						conn.setDoOutput(true);
						BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						try {
							while ((line = rd.readLine()) != null) {
								source += line + "\n";
							}
						} finally {
							rd.close();
						}
						/*
						 * Stop at first
						 */
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			time = System.currentTimeMillis() - start;
		}
	}

	public String replaceAll(String line) {
		String[] replaces = { ">", "<", "&", "" };

		String[] paterns = { "&gt;", "&lt;", "&amp;", "<[\\/\\!]*?[^<>]*?>" };

		for (int i = 0; i < paterns.length; i++) {
			line = line.replaceAll(paterns[i], replaces[i]);
		}

		return line;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jadclipse.IDecompiler#decompileFromArchive(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void decompileFromArchive(String archivePath, String packege, String className) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see IDecompiler#getSource()
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @see IDecompiler#getLog()
	 */
	public String getLog() {
		return log == null ? "" : log.toString();
	}

	public List getExceptions() {
		return excList;
	}

	public long getDecompilationTime() {
		return time;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String _package = ""; // "com.infoengine.connector";
		String className = "Element";
		String srcVersion = "";

		String project = "x-26";
		String file = _package.replace('.', '/') + "/" + className;
		String grock_url = "http://ah-opengrok.ptcnet.ptc.com/";
		String search_pattern = grock_url + "search?q=&project=" + project + "&defs=&refs=&path=" + file + ".java&hist=";

		Document doc = Jsoup.connect(search_pattern).get();
		Elements results = doc.getElementById("results").getAllElements();

		// System.out.println(results.toString());

		for (Element el : results) {
			if (el.tagName().equals("a") && el.parent().className().equals("f")) {
				System.out.println(el.attr("href"));

				String class_url = grock_url + el.attr("href").replaceAll("xref", "opengrok_src");

				URL url = new URL(class_url);
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				String line;
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = rd.readLine()) != null) {
					System.out.println(line);
				}
				rd.close();

				// Document doc_class = Jsoup.connect(class_url).get();
				// System.out.println(doc_class.text());
			}
		}

	}

}
