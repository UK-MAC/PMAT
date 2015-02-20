package uk.co.awe.pmat.utils;

import java.io.FileWriter;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.co.awe.pmat.MockFile;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class FilesTest {

    private static final Map<String, File> FILES = new HashMap<>();

    private static final FileUtils.FileFactory FILE_FACTORY = new FileUtils.FileFactory() {
        @Override
        public File createFile(String filename) {
            File file = FILES.get(filename);
            assertNotNull(file);
            return file;
        }
    };
    
    public FilesTest() {
        MockFile root = MockFile.createMockDir("/");
        MockFile dir = MockFile.createMockDir("dir");
        root.addFileMock(dir);
        dir.addFileMock(MockFile.createMockFile("dir", "file1"));
        dir.addFileMock(MockFile.createMockFile("dir", "file2"));
        dir.addFileMock(MockFile.createMockFile("dir", "file3"));
        MockFile subdir1 = MockFile.createMockDir("dir", "subdir1");
        subdir1.addFileMock(MockFile.createMockFile("dir", "subdir1", "file4"));
        subdir1.addFileMock(MockFile.createMockFile("dir", "subdir1", "file5"));
        subdir1.addFileMock(MockFile.createMockFile("dir", "subdir1", "file6"));
        MockFile subdir2 = MockFile.createMockDir("dir", "subdir2");
        subdir2.addFileMock(MockFile.createMockFile("dir", "subdir2", "file7"));
        subdir2.addFileMock(MockFile.createMockFile("dir", "subdir2", "file8"));
        subdir2.addFileMock(MockFile.createMockFile("dir", "subdir2", "file9"));
        dir.addFileMock(subdir1);
        dir.addFileMock(subdir2);

        FILES.put("/", root);
        FILES.put("/dir", dir);
        FILES.put("/dir/subdir1", subdir1);
        FILES.put("/dir/subdir2", subdir2);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void getSaveAsFile_should_add_extension_to_filename_if_and_only_if_the_filename_doesnt_end_with_the_extension() {

        String filename = "testFile";
        String extension = ".foo";
        MockFile file = MockFile.createMockFile(filename);

        String expFilename = filename + extension;

        assertThat(FileUtils.getSaveAsFile(file, "", extension).getName(), equalTo(expFilename));

        filename = "testFile.foo";
        file = MockFile.createMockFile(filename);

        assertThat(FileUtils.getSaveAsFile(file, "", extension).getName(), equalTo(expFilename));
    }

    @Test
    public void getSaveAsFile_should_return_a_file_with_the_default_filename_if_the_given_file_is_a_directory() {

        String defaultFilename = "testFile";
        String extension = ".foo";
        MockFile dir = MockFile.createMockDir("/", "tmp");

        assertTrue(dir.isDirectory());

        String expPath = "/tmp/" + defaultFilename + extension;

        assertThat(FileUtils.getSaveAsFile(dir, defaultFilename, extension).getAbsolutePath(), equalTo(expPath));
    }

    @Test
    public void globToRegex_should_convert_a_valid_glob_to_the_equivalent_regex() {

        String glob = "a*b";
        String regex = "a.*b";

        assertThat(FileUtils.globToRegex(glob), equalTo(regex));

        glob = "a?b";
        regex = "a.b";

        assertThat(FileUtils.globToRegex(glob), equalTo(regex));

        glob = "ab.c";
        regex = "ab\\.c";

        assertThat(FileUtils.globToRegex(glob), equalTo(regex));

        glob = "ab{c,d}";
        regex = "ab(c|d)";

        assertThat(FileUtils.globToRegex(glob), equalTo(regex));
    }

    @Test
    public void regexToGlob_should_convert_a_globable_regex_to_the_equivalent_glob() {

        String regex = "a.*b";
        String glob = "a*b";

        assertThat(FileUtils.regexToGlob(regex), equalTo(glob));

        regex = "a.b";
        glob = "a?b";

        assertThat(FileUtils.regexToGlob(regex), equalTo(glob));

        regex = "ab\\.c";
        glob = "ab.c";

        assertThat(FileUtils.regexToGlob(regex), equalTo(glob));

        regex = "ab(c|d)";
        glob = "ab{c,d}";

        assertThat(FileUtils.regexToGlob(regex), equalTo(glob));
    }

    @Test
    public void globMatch_applied_to_a_directory_should_just_return_that_directory() {
        List<File> files = FileUtils.globMatch("/dir", FILE_FACTORY);

        assertThat(files.size(), equalTo(1));
        assertThat(files.get(0).getAbsolutePath(), equalTo("dir"));
    }

    @Test
    public void globMatch_can_find_a_file_using_a_wildcard_character() {
        List<File> files = FileUtils.globMatch("/dir/f*2", FILE_FACTORY);

        assertThat(files.size(), equalTo(1));
        assertThat(files.get(0).getName(), equalTo("file2"));
    }

    @Test
    public void globMatch_given_a_directory_and_a_star_should_return_all_the_in_the_directory() {
        List<File> expFiles = java.util.Arrays.asList(FILES.get("/dir").listFiles());

        List<File> files = FileUtils.globMatch("/dir/*", FILE_FACTORY);
        assertThat(files.size(), equalTo(expFiles.size()));

        Collections.sort(files);
        assertThat(files, equalTo(expFiles));
    }

    @Test
    public void globMatch_given_a_wildcard_directory_should_return_all_one_level_down() {
        List<File> expFiles = new ArrayList<>();
        expFiles.addAll(java.util.Arrays.asList(FILES.get("/dir/subdir1").listFiles()));
        expFiles.addAll(java.util.Arrays.asList(FILES.get("/dir/subdir2").listFiles()));
        Collections.sort(expFiles);

        List<File> files = FileUtils.globMatch("/dir/*/*", FILE_FACTORY);
        assertThat(files.size(), equalTo(expFiles.size()));

        Collections.sort(files);
        assertThat(files, equalTo(expFiles));
    }
    
    @Test
    public void getMD5sum_should_return_the_md5_sum_of_the_given_file() throws IOException, InterruptedException {
        File file = Files.createTempFile("md5-test", ".tmp").toFile();
        file.deleteOnExit();
        
        try (FileWriter out = new FileWriter(file)) {
            out.write("MD5 Test File");
        }
        
        String md5 = FileUtils.getMD5sum(file);
        
        Process proc = new ProcessBuilder("md5sum", file.getAbsolutePath()).start();
        proc.waitFor();
        byte[] buffer = new byte[8096];
        int read = proc.getInputStream().read(buffer);
        String expMd5 = new String(buffer, 0, read).split(" ")[0].toUpperCase();
        
        assertThat(md5, equalTo(expMd5));
    }

}