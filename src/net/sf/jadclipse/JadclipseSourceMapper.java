package net.sf.jadclipse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.SourceMapper;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

/**
 * Yet "better" way to hook into JDT...
 * 
 * using a source mapper enables us to use the outline!
 * 
 * Sorry, still ne better news for debuging.
 * 
 * @author Jochen Klein
 */

public class JadclipseSourceMapper extends SourceMapper {

	private IDecompiler decompiler;

	private SourceMapper originalSourceMapper;

	private boolean isAttachedSource;

	public JadclipseSourceMapper() {
		// values are never used, as we have overwritten
		// findSource()

		// super(new Path("."), "");
		super(new Path("."), "", new HashMap()); // per Rene's e-mail
		decompiler = new JadDecompiler();
	}

	/*
	 * (non-Javadoc) R2.1 fix
	 * 
	 * @see
	 * org.eclipse.jdt.internal.core.SourceMapper#findSource(org.eclipse.jdt
	 * .core.IType)
	 */
	public char[] findSource(IType type) {
		if (!type.isBinary()) {
			return null;
		}
		BinaryType parent = (BinaryType) type.getDeclaringType();
		BinaryType declType = (BinaryType) type;
		while (parent != null) {
			declType = parent;
			parent = (BinaryType) declType.getDeclaringType();
		}
		IBinaryType info = null;
		try {
			info = (IBinaryType) declType.getElementInfo();
		} catch (JavaModelException e) {
			return null;
		}
		if (info == null) {
			return null;
		}
		return findSource(type, info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.internal.core.SourceMapper#findSource(org.eclipse.jdt
	 * .core.IType, org.eclipse.jdt.internal.compiler.env.IBinaryType)
	 */
	public char[] findSource(IType type, IBinaryType info) {
		IPreferenceStore prefs = JadclipsePlugin.getDefault()
				.getPreferenceStore();
		boolean always = prefs.getBoolean(JadclipsePlugin.IGNORE_EXISTING);
		boolean ignoreptcsrc = prefs.getBoolean(JadclipsePlugin.IGNORE_PTCSRC);

		// Since the source mapper for the PackageFragmentRoot is replaced (see
		// below)
		// make sure that further files are not decompiled if they have a source
		// attachment.
		// Fixes bug:
		// http://sourceforge.net/tracker/index.php?func=detail&aid=1554721&group_id=40205&atid=427342
		if (originalSourceMapper != null && !always) {
			char[] attachedSource = originalSourceMapper.findSource(type, info);
			if (attachedSource != null) {
				isAttachedSource = true;
				return attachedSource;
			}
		}

		if (info == null) {
			return null;
		}

		Collection exceptions = new LinkedList();
		IPackageFragment pkgFrag = type.getPackageFragment();
		IPackageFragmentRoot root = (IPackageFragmentRoot) pkgFrag.getParent();

		try {
			if (root instanceof PackageFragmentRoot) {
				PackageFragmentRoot pfr = (PackageFragmentRoot) root;

				// try
				SourceMapper sourceMapper = pfr.getSourceMapper();
				if (sourceMapper != null && !always
						&& !(sourceMapper instanceof JadclipseSourceMapper)) {
					char[] attachedSource = sourceMapper.findSource(type, info);
					if (attachedSource != null) {
						isAttachedSource = true;
						return attachedSource;
					}
				}

				if (originalSourceMapper == null) {
					originalSourceMapper = sourceMapper;
					pfr.setSourceMapper(this);
				}
			}
		} catch (JavaModelException e) {
			JadclipsePlugin.logError(e, "Could not set source mapper.");
		}

		isAttachedSource = false;

		String pkg = type.getPackageFragment().getElementName().replace('.',
				'/');
		String location = "\tDecompiled from: ";
		String classFile = new String(info.getFileName());
		int p = classFile.lastIndexOf('/');
		classFile = classFile.substring(p + 1);
		if (!ignoreptcsrc) {
			isAttachedSource = true;
			IDecompiler _decompiler = new PTCDecompiler();
			String archivePath = getArchivePath(root);
			String _class = classFile;
			int pos = _class.lastIndexOf('.');
			_class = _class.substring(0, pos);
			_decompiler.decompile(archivePath, type.getPackageFragment()
					.getElementName(), _class);
			StringBuffer source = new StringBuffer();
			source.append(formatSource(_decompiler.getSource()));
			if (!source.toString().trim().equals(""))
				return source.toString().toCharArray();
		}
		if (root.isArchive()) {
			String archivePath = getArchivePath(root);
			location += archivePath;
			decompiler.decompileFromArchive(archivePath, pkg, classFile);
		} else {
			try {
				location += root.getUnderlyingResource().getLocation()
						.toOSString()
						+ "/" + pkg + "/" + classFile;
				decompiler.decompile(root.getUnderlyingResource().getLocation()
						.toOSString(), pkg, classFile);
			} catch (JavaModelException e) {
				exceptions.add(e);
			}
		}

		StringBuffer source = new StringBuffer();
		source.append(formatSource(decompiler.getSource()));
		source.append("\n\n/*");
		source.append("\n\tDECOMPILATION REPORT\n\n");
		source.append(location).append("\n");
		source.append("\tTotal time: ").append(Long.toString(decompiler.getDecompilationTime())).append(" ms\n");
		source.append(decompiler.getLog());
		exceptions.addAll(decompiler.getExceptions());
		logExceptions(exceptions, source);
		source.append("\n*/");
		return source.toString().toCharArray();
	}

	private String formatSource(String source) {
		String result = null;

		IPreferenceStore prefs = JadclipsePlugin.getDefault()
				.getPreferenceStore();
		boolean useFormatter = prefs
				.getBoolean(JadclipsePlugin.USE_ECLIPSE_FORMATTER);

		if (source != null && useFormatter) {
			CodeFormatter formatter = ToolFactory.createCodeFormatter(null);
			TextEdit textEdit = formatter.format(
					CodeFormatter.K_COMPILATION_UNIT, source, 0, source
							.length(), 0, null);
			if (textEdit != null) {
				IDocument document = new Document(source);
				try {
					textEdit.apply(document);
				} catch (BadLocationException e) {
					JadclipsePlugin.logError(e,
							"Unable to apply text formatting.");
				}
				result = document.get();
			}

			if (result == null) {
				JadclipsePlugin.logError(null,
						"Could not format code, it will remain unformatted.");
				result = source;
			}
		} else {
			result = source;
		}

		return result;
	}

	private void logExceptions(Collection exceptions, StringBuffer buffer) {
		buffer.append("\tCaught exceptions:");
		if (exceptions == null || exceptions.size() == 0)
			return; // nothing to do
		buffer.append("\n");
		StringWriter stackTraces = new StringWriter();
		PrintWriter stackTracesP = new PrintWriter(stackTraces);

		Iterator i = exceptions.iterator();
		while (i.hasNext()) {
			((Exception) i.next()).printStackTrace(stackTracesP);
			stackTracesP.println("");
		}

		stackTracesP.flush();
		stackTracesP.close();
		buffer.append(stackTraces.toString());
	}

	private String getArchivePath(IPackageFragmentRoot root) {
		String archivePath = null;
		IResource resource;

		try {
			if ((resource = root.getUnderlyingResource()) != null)
				// jar in workspace
				archivePath = resource.getLocation().toOSString();
			else
				// external jar
				archivePath = root.getPath().toOSString();
		} catch (JavaModelException e) {
			throw new RuntimeException("Unexpected Java model exception: "
					+ e.toString());
		}
		return archivePath;
	}

	/**
	 * Finds the deepest <code>IJavaElement</code> in the hierarchy of <code>elt</elt>'s children (including <code>elt</code>
	 * itself) which has a source range that encloses <code>position</code>
	 * according to <code>mapper</code>.
	 * 
	 * Code mostly taken from 'org.eclipse.jdt.internal.core.ClassFile'
	 */
	protected IJavaElement findElement(IJavaElement elt, int position) {
		ISourceRange range = getSourceRange(elt);
		if (range == null || position < range.getOffset()
				|| range.getOffset() + range.getLength() - 1 < position) {
			return null;
		}
		if (elt instanceof IParent) {
			try {
				IJavaElement[] children = ((IParent) elt).getChildren();
				for (int i = 0; i < children.length; i++) {
					IJavaElement match = findElement(children[i], position);
					if (match != null) {
						return match;
					}
				}
			} catch (JavaModelException npe) {
			}
		}
		return elt;
	}

	/**
	 * @see org.eclipse.jdt.internal.core.SourceMapper#mapSource(IType, char[],
	 *      IBinaryType)
	 */
	public void mapSource(IType type, char[] contents, boolean force) {
		if (force) {
			sourceRanges.remove(type);
		}
		super.mapSource(type, contents, null);
	}

	/**
	 * @return Does the source returned by
	 *         {@link #findSource(IType, IBinaryType)} originate from a source
	 *         attachment?
	 */
	public boolean isAttachedSource() {
		return isAttachedSource;
	}

}