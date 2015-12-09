package net.sf.jadclipse.opengrock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utils {
	
	public static String[][] getGrockWebSites() {
		Map<String, String> list = new HashMap<String, String>();
		list.put("http://ah-opengrok.ptcnet.ptc.com/", "http://ah-opengrok.ptcnet.ptc.com/");
		list.put("http://bla-grok-01/", "http://bla-grok-01/");
		list.put("http://localhost:8090/source/", "http://localhost:8090/source/");
		return getArrayFromHash(list);
	}

	public static String[][] getGrockProjects() {
		// TODO Auto-generated method stub
		
		Map<String, String> list = new HashMap<String, String>();
		try {
			Document doc = Jsoup.connect("http://bla-grok-01/").get();
			Elements projects = doc.getElementById("project").getAllElements();
			
			for (Element project : projects) {
				if(project.tagName().equals("option"))
					list.put(project.val(), project.text());
			}

			//el.baseUri();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getArrayFromHash(list);
	}
	
	
	public static String[][] getArrayFromHash(Map<String,String> data){
        String[][] str = null;
        {
            Object[] keys = data.keySet().toArray();
            Object[] values = data.values().toArray();
            str = new String[keys.length][2];
            for(int i=0;i<keys.length;i++) {
                str[i][0] = (String)keys[i];
                str[i][1] = (String)values[i];
            }
        }
        return str;
    }

}
