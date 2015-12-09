package net.sf.jadclipse;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditorActionContributor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * Adds JadClipse action to tool/menu bars
 * 
 * @author		V. Grishchenko
 */
public class JadclipseActionBarContributor extends ClassFileEditorActionContributor
{
	private DecompileAction dAction;
	private JadclipseClassFileEditor editor;

	private class DecompileAction extends Action
	{
		protected DecompileAction(ImageDescriptor actionIcon)
		{
			super("Decompile@Ctrl+Shift+X", actionIcon);
			//setDescription("xxx");
			setToolTipText("Decompile");
			setAccelerator(SWT.CTRL | SWT.SHIFT | 'X');
		}
		
		public void run()
		{
			if (editor != null)
				editor.doSetInput(true);
		}
	}
	
	public JadclipseActionBarContributor()
	{
		URL base = JadclipsePlugin.getDefault().getBundle().getEntry("/");
		URL url = null;
		try
		{
			url = new URL(base, "icons/jad.gif");
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		ImageDescriptor actionIcon = null;
		if (url != null)
			actionIcon = ImageDescriptor.createFromURL(url);
		dAction = new DecompileAction(actionIcon);
	}
	
	public void contributeToToolBar(IToolBarManager toolBarManager)
	{
		super.contributeToToolBar(toolBarManager);
		toolBarManager.add(dAction);
//		toolBarManager.add(new Separator(JadclipsePlugin.PID_JADCLIPSE));
//		toolBarManager.appendToGroup(JadclipsePlugin.PID_JADCLIPSE, dAction);
	}
	
	public void contributeToMenu(IMenuManager menu)
	{
		super.contributeToMenu(menu);
		MenuManager jadMenu = new MenuManager("Ja&dClipse");
		jadMenu.add(dAction);
		IMenuManager window = 
			menu.findMenuUsingPath(IWorkbenchActionConstants.M_WINDOW);
		if (window != null)
			menu.insertBefore(IWorkbenchActionConstants.M_WINDOW, jadMenu);
		else
			menu.add(jadMenu);
	}
	
	public void setActiveEditor(IEditorPart targetEditor)
	{
		if (targetEditor instanceof JadclipseClassFileEditor)
		{
			editor = (JadclipseClassFileEditor)targetEditor;
			editor.doSetInput(false);
		}
		else
			editor = null;
		super.setActiveEditor(targetEditor);
	}	
}
