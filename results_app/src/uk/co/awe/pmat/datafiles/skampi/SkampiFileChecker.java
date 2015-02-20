package uk.co.awe.pmat.datafiles.skampi;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import uk.co.awe.pmat.datafiles.FileChecker;

/**
 * A {@code FileChecker} which is used to check the validity of SkaMPI files
 * before they are loaded.
 * 
 * @author AWE Plc copyright 2013
 */
public class SkampiFileChecker extends FileChecker {

	private String commentTag;
	private String skampiTag;

	/**
	 * Create a new {@code SkampiFileChecker}.
	 * 
	 * @param commentTag
	 *            the text used to mark comments in SkaMPI files.
	 * @param skampiTag
	 *            the text used to show this is a valid SkaMPI file.
	 */
	public SkampiFileChecker(String commentTag, String skampiTag) {
		this.commentTag = commentTag;
		this.skampiTag = skampiTag;
	}

	@Override
	public boolean checkHeader(File file) throws IOException {

		Scanner scanner = new Scanner(file);

		boolean isValid = false;

		try {
			if (!scanner.hasNextLine()) {
				throw new IOException("Empty SkaMPI file.");
			}
			while (scanner.hasNextLine()) {
				if (scanner.hasNext() && scanner.next().equals(this.commentTag)) {
					if (scanner.hasNext() && scanner.next().equals(skampiTag)) {
						isValid = true;
					}
				} else {
					scanner.nextLine();
				}
			}
		} catch (IllegalStateException ex) {
			throw new IOException("Scanner closed on checkHeader.", ex);
		}

		return isValid;
	}

}
