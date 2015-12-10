package net.sf.jadclipse.opengrock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jadclipse.JadclipsePlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utils {

	public static String[][] getGrockWebSites() {
		Map<String, String> list = new HashMap<String, String>();
		list.put("http://ah-opengrok.ptcnet.ptc.com/", "http://ah-opengrok.ptcnet.ptc.com/");
		list.put("http://bla-grok-01/", "http://bla-grok-01/");
		list.put("http://ah-wused.ptcnet.ptc.com", "http://ah-wused.ptcnet.ptc.com");
		list.put("http://localhost:8090/source/", "http://localhost:8090/source/");
		return getArrayFromHash(list);
	}

	public static String[][] getGrockProjects() {
		// TODO Auto-generated method stub

		Map<String, String> list = new TreeMap<String, String>();
		try {
			IPreferenceStore prefs = JadclipsePlugin.getDefault().getPreferenceStore();
			String srcUrl = prefs.getString(JadclipsePlugin.PTC_URL);
			if (isWhereUsedSite(srcUrl)) {
				Document doc = Jsoup.connect(prefs.getString(JadclipsePlugin.PTC_URL) + "/wus_x-20/wusSelectApp.jsp").get();
				Elements projects = doc.getElementsByTag("li");
				for (Element project : projects) {
					list.put(project.children().text(), project.children().attr("href").split("/")[1]);
				}
			} else {
				Document doc = Jsoup.connect(prefs.getString(JadclipsePlugin.PTC_URL)).get();
				Elements projects = doc.getElementById("project").getAllElements();
				for (Element project : projects) {
					if (project.tagName().equals("option"))
						list.put(project.val(), project.text());
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getArrayFromHash(list);
	}

	public static boolean isWhereUsedSite(String srcUrl) {
		if (srcUrl.indexOf("ah-wused") == -1)
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
