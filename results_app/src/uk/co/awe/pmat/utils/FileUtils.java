package uk.co.awe.pmat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class to hold utilities methods used on files.
 * 
 * @author AWE Plc copyright 2013
 */
public final class FileUtils {

	/**
	 * This class cannot be instantiated.
	 */
	private FileUtils() {
	}

	public static String getMD5sum(File file) throws IOException {
        
        final byte[] buffer = new byte[8096];
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException("Cannot load MD5 algorithm", ex);
        }
        
        int read = 0;
        
        try (InputStream in = new FileInputStream(file)) {
            while ((read = in.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        
        return new BigInteger(1, digest.digest()).toString(16).toUpperCase();
    }

	/**
	 * Utility method which takes a file or directory and returns a file with
	 * the correct extension. If the baseFile is a directory the file returned
	 * has the defaultFilename suffixed by the extension.
	 * 
	 * @param baseFile
	 *            the starting file or directory.
	 * @param defaultFilename
	 *            the file name to use if baseFile is a directory,
	 * @param extension
	 *            the extension to append if the file does not already end with
	 *            it/
	 * @return a file with the extension suffix.
	 */
	public static File getSaveAsFile(final File baseFile,
			final String defaultFilename, final String extension) {

		final File file;
		if (baseFile.isDirectory()) {
			file = new File(baseFile, defaultFilename + extension);
		} else {
			String fileName = baseFile.getAbsolutePath();
			if (!fileName.endsWith(extension)) {
				fileName += extension;
			}
			file = new File(fileName);
		}

		return file;
	}

	/**
	 * Factory interface to allow injection of mock files in testing.
	 */
	static interface FileFactory {
		/**
		 * Create a {@code File} with the given file name.
		 * 
		 * @param filename
		 *            the name of the file.
		 * @return a {@code File}.
		 */
		File createFile(String filename);
	}

	/**
	 * The default implementation of the {@code FileFactory} interface, which
	 * returns standard files.
	 */
	private static class DefaultFileFactory implements FileFactory {
		@Override
		public File createFile(String filename) {
			return new File(filename);
		}
	}

	/**
	 * Perform the "glob" style match, using the given {@code FileFactory} to
	 * request for the files as necessary.
	 * 
	 * @param glob
	 *            the file path, possibly containing glob wild cards.
	 * @param fileFactory
	 *            the {@code FileFactory} used to return new file objects as
	 *            required.
	 * @return the list of all files that match the glob expanded file path.
	 */
	static List<File> globMatch(final String glob, final FileFactory fileFactory) {
        List<File> files = new ArrayList<>();

        String fileSeperator = File.separatorChar == '\\' ? "\\\\" : File.separator;
        List<String> dirs = Arrays.asList(glob.split(fileSeperator));

        int startIdx = 0;
        if (dirs.get(0).isEmpty()) {
            List<File> newFiles = findMatchingFiles(fileFactory
                    .createFile(File.separator), dirs.get(1));
            files.addAll(newFiles);
            startIdx = 2;
        } else {
            List<File> newFiles = findMatchingFiles(fileFactory
                    .createFile(System.getProperty("user.dir")), dirs.get(0));
            files.addAll(newFiles);
            startIdx = 1;
        }

        for (String dir : dirs.subList(startIdx, dirs.size())) {
            List<File> newFiles = new ArrayList<>();
            for (File file : files) {
                newFiles.addAll(findMatchingFiles(file, dir));
            }
            files = newFiles;
        }

        return files;
    }

	/**
	 * Scan a directory for all files that match a given "glob" search
	 * expression.
	 * 
	 * @param dir
	 *            the directory to scan.
	 * @param glob
	 *            the glob used to filter the files.
	 * @return a list of matching files, may be empty.
	 */
	private static List<File> findMatchingFiles(final File dir,
			final String glob) {

		final File[] files = dir.listFiles(new RegExpFilenameFilter(
				globToRegex(glob)));
		final List<File> emptyList = Collections.emptyList();
		return (files == null) ? emptyList : Arrays.asList(files);
	}

	/**
	 * Utility method to expand a given file path using glob style pattern
	 * matching and create a list of all files and directories that match these
	 * expanded paths.
	 * 
	 * @param glob
	 *            The file path, possibly containing glob wild cards
	 * @return The list of all files that match the glob expanded file path
	 */
	public static List<File> globMatch(final String glob) {
		return globMatch(glob, new DefaultFileFactory());
	}

	/**
	 * Utility method which converts a (simple) regex file pattern such as
	 * ".*\\.txt" into a glob style file pattern such as "*.txt".
	 * 
	 * @param regex
	 *            The string containing the regex pattern to convert
	 * @return A string containing the converted glob pattern
	 */
	public static String regexToGlob(final String regex) {
		return regex.replaceAll("(?<!\\\\)\\.(?!\\*)", "?").replaceAll(
				"\\.\\*", "\\*").replaceAll("\\\\\\.", ".").replaceAll("\\(",
				"{").replaceAll("\\)", "}").replaceAll("\\|", ",");
	}

	/**
	 * Utility method which converts a glob style file pattern such as "*.txt"
	 * into a regex file pattern such as ".*\\.txt".
	 * 
	 * @param glob
	 *            The string containing the glob pattern to convert
	 * @return A string containing the converted regex pattern
	 */
	public static String globToRegex(final String glob) {
		return glob.replaceAll("\\.", "\\\\\\.").replaceAll("\\*", "\\.\\*")
				.replaceAll("\\?", "\\.").replaceAll("\\{", "\\(").replaceAll(
						"\\}", "\\)").replaceAll(",", "\\|");
	}
}
