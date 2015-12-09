package net.sf.jadclipse;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.internal.core.BufferManager;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorInput;

/**
 * A "better" way to hook into JDT...
 * 
 * @author  V. Grishchenko
 * @author  Jochen Klein
 * @author  Johann Gyger
 */
public class JadclipseClassFileEditor extends ClassFileEditor
{
	public static final String ID = "net.sf.jadclipse.JadclipseClassFileEditor";
	public static final String MARK = "/*jadclipse*/";
	private static final char[] MARK_ARRAY = MARK.toCharArray();

	private JadclipseSourceMapper sourceMapper;
	
	protected JadclipseBufferManager getBufferManager()
	{
		JadclipseBufferManager manager;
		BufferManager defManager = BufferManager.getDefaultBufferManager();
		if (defManager instanceof JadclipseBufferManager)
			manager = (JadclipseBufferManager) defManager;
		else
			manager = new JadclipseBufferManager(defManager);
		return manager;
	}

	/**
	 * Sets edditor input only if buffer was actually opened.
	 * 
	 * @param force if <code>true</code> initialize no matter what
	 */
	public void doSetInput(boolean force)
	{
		IEditorInput input = getEditorInput();
		if (doOpenBuffer(input, force))
		{
			try
			{
				doSetInput(input);
			}
			catch (Exception e)
			{
				JadclipsePlugin.logError(e, "");
			}
		}
	}
	
	/**
	 * @return <code>true</code> if this editor displays decompiled source,
	 * <code>false</code> otherwise
	 */
	public boolean containsDecompiled()
	{
		return (sourceMapper != null);
	}

	private boolean doOpenBuffer(IEditorInput input, boolean force)
	{
		if (input instanceof IClassFileEditorInput)
		{
			try
			{
				boolean opened = false;
				IClassFile cf = ((IClassFileEditorInput) input).getClassFile();
				IPreferenceStore prefs = JadclipsePlugin.getDefault().getPreferenceStore();
				boolean reuseBuf = prefs.getBoolean(JadclipsePlugin.REUSE_BUFFER);
				boolean always = prefs.getBoolean(JadclipsePlugin.IGNORE_EXISTING);
				String origSrc = cf.getSource();
				//have to check our mark since all line comments are stripped in debug align mode
				if (origSrc == null
						|| always && !origSrc.startsWith(MARK)
						|| (origSrc.startsWith(MARK) && (!reuseBuf || force)))
				{
					if (sourceMapper == null)
						sourceMapper = new JadclipseSourceMapper();

					char[] src = sourceMapper.findSource(cf.getType());
					if (src == null) {
						src = new char[] {'\n', '/', '/', 'E', 'r', 'r', 'o', 'r', '!'};
					}
					char[] markedSrc = null;
					if (sourceMapper.isAttachedSource()) {
						markedSrc = src;
					} else {
						markedSrc = new char[MARK_ARRAY.length + src.length];
						//next time we know this is decompiled source
						System.arraycopy(MARK_ARRAY, 0, markedSrc, 0, MARK_ARRAY.length);
						System.arraycopy(src, 0, markedSrc, MARK_ARRAY.length, src.length);
					}

					IBuffer buffer = getBufferManager().createBuffer(cf);
					buffer.setContents(markedSrc);
					getBufferManager().addBuffer(buffer);
					//buffer.addBufferChangedListener((IBufferChangedListener)cf);
					sourceMapper.mapSource(cf.getType(), markedSrc, true);
					opened = true;
				}
				return opened;
			}
			catch (Exception e)
			{
				JadclipsePlugin.logError(e, "");
			}
		}
		return false;
	}

}