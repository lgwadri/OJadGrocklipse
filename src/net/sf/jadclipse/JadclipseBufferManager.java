package net.sf.jadclipse;

import java.util.Enumeration;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.BufferManager;

/**
 * This class is a hack that replaces JDT <code>BufferManager</code> in order
 * to make <code>addBuffer()</code> and <code>removeBuffer()</code>
 * accessible.
 * 
 * @author		V.Grishchenko
 */
public class JadclipseBufferManager extends BufferManager
{
	/**
	 * Constructor for JadclipseBufferManager.
	 */
	public JadclipseBufferManager(BufferManager manager)
	{
		super();
		synchronized (BufferManager.class)
		{
			Enumeration enumeration = manager.getOpenBuffers();
			while (enumeration.hasMoreElements())
			{
				IBuffer buffer = (IBuffer) enumeration.nextElement();
				addBuffer(buffer);
			}
			BufferManager.DEFAULT_BUFFER_MANAGER = this;
		}
	}
	
	/**
	 * Closes buffers open by jadclipse
	 * 
	 * @param all close all buffers including those that have no real source
	 */
	public static void closeJadclipseBuffers(boolean all)
	{
		BufferManager manager = BufferManager.getDefaultBufferManager();
		if (manager instanceof JadclipseBufferManager)
		{
			Enumeration enumeration = manager.getOpenBuffers();
			while (enumeration.hasMoreElements())
			{
				IBuffer buffer = (IBuffer) enumeration.nextElement();
				IOpenable owner = buffer.getOwner();
				if (owner instanceof IClassFile
						&& buffer.getContents().startsWith(JadclipseClassFileEditor.MARK))
				{
					JadclipseBufferManager jManager = (JadclipseBufferManager)manager;
					jManager.removeBuffer(buffer);
					if (!all) //restore buffers for files without source
					{
						IClassFile cf = (IClassFile)owner;
						String realSource = null;
						try
						{
							realSource = cf.getSource();
						}
						catch (JavaModelException e)
						{
							IStatus err = new Status(IStatus.ERROR, JadclipsePlugin.PLUGIN_ID, 
												0, "failed to get source while flushing buffers", e);
							JadclipsePlugin.getDefault().getLog().log(err);
						}
						if (realSource == null)
							jManager.addBuffer(buffer);
					}
				}	
			}
		}
	}

	/**
	 * @see BufferManager#addBuffer(IBuffer)
	 */
	public void addBuffer(IBuffer buffer)
	{
		super.addBuffer(buffer);
	}

	/**
	 * @see BufferManager#removeBuffer(IBuffer)
	 */
	public void removeBuffer(IBuffer buffer)
	{
		super.removeBuffer(buffer);
	}
}
