package net.sf.jadclipse.opengrock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jadclipse.JadclipsePlugin;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utils {

	public static String[][] getGrockProjects(String baseUrl) {
		IPreferenceStore prefs = JadclipsePlugin.getDefault().getPreferenceStore();
		Map<String, String> list = new TreeMap<String, String>();
		try {
			
			String srcUrl = baseUrl != null ? baseUrl : prefs.getString(JadclipsePlugin.PTC_URL);
			if (isWhereUsedSite(srcUrl)) {
				Document doc = jsconnect(srcUrl + "/wus_x-20/wusSelectApp.jsp").get();
				Elements projects = doc.getElementsByTag("li");
				for (Element project : projects) {
					list.put(project.children().attr("href").split("/")[1], project.children().text());
				}
			} else {
				Document doc = jsconnect(srcUrl).get();
				Elements projects = doc.getElementById("project").getAllElements();
				for (Element project : projects) {
					if (project.tagName().equals("option"))
						list.put(project.val(), project.text());
				}
			}

		} catch (Exception e) {
			list.put(prefs.getString(JadclipsePlugin.GROCK_PROJECT), prefs.getString(JadclipsePlugin.GROCK_PROJECT) + " (Disconnected)");
			JadclipsePlugin.logError(e, e.getLocalizedMessage());
		}

		return getArrayFromHash(list);
	}
	
	public static Connection jsconnect(String url) throws IOException {
		
		Connection con = Jsoup.connect(url.trim());
		con.timeout(50000);
		return con;
	}

	public static boolean isWhereUsedSite(String srcUrl) {
		String whereuserdURLs = Platform.getResourceBundle(JadclipsePlugin.getDefault().getBundle()).getString(JadclipsePlugin.WUSED_URLS);
		if (whereuserdURLs.indexOf(srcUrl) == -1)
			return false;
		else
			return true;
	}

	public static String[][] getArrayFromHash(Map<String, String> data) {
		String[][] str = null;
		{
			Object[] keys = data.keySet().toArray();
			Object[] values = data.values().toArray();
			str = new String[keys.length][2];
			for (int i = 0; i < keys.length; i++) {
				str[i][0] = (String) keys[i];
				str[i][1] = (String) values[i];
			}
		}
		return str;
	}

}
