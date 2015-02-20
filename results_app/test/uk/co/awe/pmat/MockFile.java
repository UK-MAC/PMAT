package uk.co.awe.pmat;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.co.awe.pmat.utils.StringUtils;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class MockFile extends File {

    private final String[] names;
    private final List<MockFile> subFiles;
    private final boolean isDir;

    private MockFile(boolean isDir, String... names) {
        super(StringUtils.joinStrings(Arrays.asList(names), File.separator));
        this.names = names;
        this.isDir = isDir;
        subFiles = new ArrayList<>();
    }

    public static MockFile createMockFile(String... names) {
        return new MockFile(false, names);
    }

    public static MockFile createMockDir(String... names) {
        return new MockFile(true, names);
    }

    public void addFileMock(MockFile fileMock) {
        subFiles.add(fileMock);
    }

    @Override
    public boolean canExecute() { return false; }

    @Override
    public boolean canRead() { return false; }

    @Override
    public boolean canWrite() { return false; }

    @Override
    public int compareTo(File other) {
        return getName().compareTo(other.getName());
    }

    @Override
    public boolean createNewFile() throws IOException { return false; }

    @Override
    public boolean delete() { return false; }

    @Override
    public void deleteOnExit() {}

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) { return super.equals(obj); }

    @Override
    public boolean exists() { return true; }

    @Override
    public File getAbsoluteFile() { return null; }

    @Override
    public String getAbsolutePath() {
        return StringUtils.joinStrings(Arrays.asList(names), File.separator);
    }

    @Override
    public File getCanonicalFile() throws IOException { return null; }

    @Override
    public String getCanonicalPath() throws IOException { return null; }

    @Override
    public long getFreeSpace() { return 0; }

    @Override
    public String getName() { return names[names.length - 1]; }

    @Override
    public String getParent() { return null; }

    @Override
    public File getParentFile() { return null; }

    @Override
    public String getPath() { return null; }

    @Override
    public long getTotalSpace() { return 0; }

    @Override
    public long getUsableSpace() { return 0; }

    @Override
    public int hashCode() { return 0; }

    @Override
    public boolean isAbsolute() { return false; }

    @Override
    public boolean isDirectory() { return isDir; }

    @Override
    public boolean isFile() { return !isDir; }

    @Override
    public boolean isHidden() { return false; }

    @Override
    public long lastModified() { return 0; }

    @Override
    public long length() { return 0; }

    @Override
    public String[] list() { return null; }

    @Override
    public String[] list(FilenameFilter filter) { return null; }

    @Override
    public File[] listFiles() {
        return subFiles.toArray(new File[subFiles.size()]);
    }

    @Override
    public File[] listFiles(FilenameFilter filter) {
        List<File> files = new ArrayList<>();
        for (File subFile : subFiles) {
	    if ((filter == null) || filter.accept(this, subFile.getName())) {
                files.add(subFile);
	    }
        }
        return files.toArray(new File[files.size()]);
    }

    @Override
    public File[] listFiles(FileFilter filter) { return null; }

    @Override
    public boolean mkdir() { return false; }

    @Override
    public boolean mkdirs() { return false; }

    @Override
    public boolean renameTo(File dest) { return false; }

    @Override
    public boolean setExecutable(boolean executable, boolean ownerOnly) { return false; }

    @Override
    public boolean setExecutable(boolean executable) { return false; }

    @Override
    public boolean setLastModified(long time) { return false; }

    @Override
    public boolean setReadOnly() { return false; }

    @Override
    public boolean setReadable(boolean readable, boolean ownerOnly) { return false; }

    @Override
    public boolean setReadable(boolean readable) { return false; }

    @Override
    public boolean setWritable(boolean writable, boolean ownerOnly) { return false; }

    @Override
    public boolean setWritable(boolean writable) { return false; }

    @Override
    public String toString() { return Arrays.toString(names); }

    @Override
    public URI toURI() { return null; }

    @Override
    @Deprecated
    public URL toURL() throws MalformedURLException { return null; }
    
}
