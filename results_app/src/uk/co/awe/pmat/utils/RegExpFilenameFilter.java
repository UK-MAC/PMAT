package uk.co.awe.pmat.utils;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

/**
 * This is a filename filter that can be used within a <code>JFileChooser</code>
 * and as a <code>File.listFiles</code> filter. It matches the given regular
 * expression to the files and any directory.
 * 
 * @author AWE Plc copyright 2013
 */
public final class RegExpFilenameFilter extends FileFilter implements
		FilenameFilter {

	private String filenameRegExp; // the regular expression to match.
	private String description; // description of the file.

	/**
	 * Create the filter.
	 * 
	 * @param filenameRegExp
	 *            The regular expression to use, the description will be this
	 *            expression.
	 */
	public RegExpFilenameFilter(String filenameRegExp) {
		this.filenameRegExp = filenameRegExp;
		this.description = filenameRegExp;
	}

	/**
	 * Create this filter.
	 * 
	 * @param description
	 *            Description of the files this filter selects
	 * @param filenameRegExp
	 *            The regular expression to select the files
	 */
	public RegExpFilenameFilter(String description, String filenameRegExp) {
		this.filenameRegExp = filenameRegExp;
		this.description = description;
	}

	@Override
	public boolean accept(File dir, String name) {
		if (name == null) {
			return false;
		}
		if (name.isEmpty()) {
			return false;
		}
		return name.matches(filenameRegExp);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		return accept(f.getParentFile(), f.getName());
	}

	@Override
	public String toString() {
		return description;
	}
}
