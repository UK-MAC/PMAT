package uk.co.awe.pmat;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Application;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.db.xml.XMLSerialiser;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public class RunSaver {

	private static final int APP_GUESS_THRESHOLD = 3;

	private static final Logger LOG = LoggerFactory.getLogger(RunSaver.class);

	private static String[] storeName(Run run) {
		final List<String> path = new ArrayList<String>();

		final List<MetaData.Type> metaDataTypes = Arrays.asList(
				MetaData.Type.APPLICATION, MetaData.Type.MACHINE,
				MetaData.Type.COMPILER, MetaData.Type.MPI);
		for (MetaData.Type type : metaDataTypes) {
			path.add(run.getMetaData(type).displayName().trim().replace(" ",
					"_"));
		}
		path.add(run.getRunId() + ".prf");

		return path.toArray(new String[path.size()]);
	}

	/**
	 * Add the given run to the database.
	 * 
	 * @param run
	 *            the run to add.
	 */
	public static Exception saveRun(Run run) {
		Exception ex = null;

		try {
			final Path storePath = FileSystems.getDefault().getPath(
					Constants.Application.RESULT_STORE_DIRECTORY,
					storeName(run));

			final Set<PosixFilePermission> filePermisions = PosixFilePermissions
					.fromString("rwxrwxr-x");
			Files.createDirectories(storePath.getParent(), PosixFilePermissions
					.asFileAttribute(filePermisions));

			XMLSerialiser xMLSerialiser = new XMLSerialiser();
			xMLSerialiser.serialise(storePath.toFile(), Arrays
					.asList((XMLSerialisable) run));
			Files.setPosixFilePermissions(storePath, filePermisions);

			storePath.toFile().setWritable(true, false);

			run = run.update(storePath.toString());

			DatabaseManager.getConnection().save(run);
		} catch (InvalidPathException e) {
			LOG.debug("Error copying PMTM file to store: " + run.getFile(), e);
			ex = e;
		} catch (IOException e) {
			LOG.debug("Error copying PMTM file to store: " + run.getFile(), e);
			ex = e;
		} catch (DatabaseException e) {
			LOG.debug("Failed to save run", e);
			ex = e;
		}

		return ex;
	}

	static Application guessApplication(List<MetaData> applications,
			String fileName, String appName) {
		int bestSoFar = 0;
		Application foundMatch = null;

		for (MetaData app : applications) {
			int closeness = Math.max(StringUtils.closeness(app.displayName(),
					fileName), StringUtils
					.closeness(app.displayName(), appName));
			if (closeness > APP_GUESS_THRESHOLD && closeness > bestSoFar) {
				foundMatch = (Application) app;
				bestSoFar = closeness;
			}
		}

		return foundMatch;
	}

}
