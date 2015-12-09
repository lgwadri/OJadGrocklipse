package net.sf.jadclipse;

import java.io.File;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author	V.Grishchenko
 */
public class JadclipsePlugin extends AbstractUIPlugin implements IPropertyChangeListener
{
	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.jadclipse";

	public static final String TEMP_DIR = "net.sf.jadclipse.tempd";
	public static final String IGNORE_PTCSRC = "net.sf.jadclipse.ptc.ignoresrc"; //since 2.04
	public static final String PTC_URL = "net.sf.jadclipse.ptc.ptcsrcUrl";
	public static final String PTC_VERSION = "net.sf.jadclipse.ptc.ptcSrcVersion";
	public static final String CMD = "net.sf.jadclipse.cmd";
	public static final String ALIGN = "net.sf.jadclipse.align";
	public static final String REUSE_BUFFER = "net.sf.jadclipse.reusebuff";
	public static final String IGNORE_EXISTING = "net.sf.jadclipse.alwaysuse"; //since 2.04
	public static final String USE_ECLIPSE_FORMATTER = "net.sf.jadclipse.use_eclipse_formatter";

	//The shared instance.
	private static JadclipsePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public JadclipsePlugin()
	{
	    plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		resourceBundle = Platform.getResourceBundle(getBundle());
		getPreferenceStore().addPropertyChangeListener(this);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		getPreferenceStore().removePropertyChangeListener(this);
		resourceBundle = null;
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static JadclipsePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key)
	{
		ResourceBundle bundle = JadclipsePlugin.getDefault().getResourceBundle();
		try
		{
			return bundle.getString(key);
		}
		catch (MissingResourceException e)
		{
			return key;
		}
	}
	
	public static void logError(Throwable t, String message)
	{
		JadclipsePlugin.getDefault().getLog().log(
			new Status(Status.ERROR, PLUGIN_ID, 0, message, t));
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle()
	{
		return resourceBundle;
	}

	protected void initializeDefaultPreferences(IPreferenceStore store)
	{
		String command = "jad";
		if (resourceBundle != null)
		{
			try
			{
				command = resourceBundle.getString(CMD);
			}
			catch (MissingResourceException e)
			{
			}
		}
		store.setDefault(CMD, command);
		store.setDefault(
			TEMP_DIR,
			System.getProperty("user.home") + File.separator + ".net.sf.jadclipse");
		store.setDefault(REUSE_BUFFER, true); //since 2.02
		store.setDefault(IGNORE_EXISTING, false);
		store.setDefault(USE_ECLIPSE_FORMATTER, false);
		store.setDefault(JadDecompiler.OPTION_INDENT_SPACE, 4); //default ident
		store.setDefault(JadDecompiler.OPTION_IRADIX, 10);
		store.setDefault(JadDecompiler.OPTION_LRADIX, 10);
		store.setDefault(JadDecompiler.OPTION_SPLITSTR_MAX, 0); //disable
		store.setDefault(JadDecompiler.OPTION_PI, 0); //disable
		store.setDefault(JadDecompiler.OPTION_PV, 0); //disable
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getProperty().equals(IGNORE_EXISTING) && event.getNewValue().equals(Boolean.FALSE))
			JadclipseBufferManager.closeJadclipseBuffers(false);
	}

}