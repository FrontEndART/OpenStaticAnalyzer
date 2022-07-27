package coderepair.repair.algs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.nio.charset.StandardCharsets;

import coderepair.repair.RepairToolSwitcher;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import difflib.DiffUtils;
import difflib.Patch;

public class IntegrationTest {

    private static final String SRC_TEST_EXAMPLES = "src/test/inputs/repairTests/examples";
    private static final String SRC_TEST_LINKEDEXAMPLES = "src/test/inputs/repairTests/linkedexamples";

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    private static final SimpleDateFormat LOGFORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS");
    private static final String TEMPDIR = System.getProperty("java.io.tmpdir");

    @Before
    public void before() {
    }

    @After
    public void after() {
    }


    @Test
    public void EI_EXPOSE_REP2_Test() throws Exception {
        runTool("EI_EXPOSE_REP2", SRC_TEST_EXAMPLES, TEMPDIR);
    }

    @Test
    public void EI_EXPOSE_REP2_DATEOBJECT_Test() throws Exception {
        runTool("EI_EXPOSE_REP2_DATEOBJECT", SRC_TEST_EXAMPLES, TEMPDIR);
    }

    @Test
    public void MS_EXPOSE_REP_Test() throws Exception {
        runTool("MS_EXPOSE_REP", SRC_TEST_EXAMPLES, TEMPDIR);
    }

    @Test
    public void EI_EXPOSE_REP2_ARRAY_Test() throws Exception {
        runTool("EI_EXPOSE_REP2_array", SRC_TEST_LINKEDEXAMPLES, TEMPDIR);
    }

    @Test
    public void MS_SHOULD_BE_FINAL_Test() throws Exception {
        runTool("MS_SHOULD_BE_FINAL", SRC_TEST_EXAMPLES, TEMPDIR);
    }

    @Test
    public void MS_PKGPROTECT_Test() throws Exception {
        runTool("MS_PKGPROTECT", SRC_TEST_LINKEDEXAMPLES, TEMPDIR);
    }

    @Test
    public void FI_PUBLIC_SHOULD_BE_PROTECTED_Test() throws Exception {
        runTool("FI_PUBLIC_SHOULD_BE_PROTECTED", SRC_TEST_EXAMPLES, TEMPDIR);
    }

    @Test
    public void MS_MUTABLE_COLLECTION_Test() throws Exception {
        runTool("MS_MUTABLE_COLLECTION", SRC_TEST_LINKEDEXAMPLES, TEMPDIR);
    }

    @Test
    public void NP_NULL_ON_SOME_PATH_Test() throws Exception {
        runTool("NP_NULL_ON_SOME_PATH", SRC_TEST_EXAMPLES, TEMPDIR);
    }

    private void runTool(final String baseName, final String baseInputDir, final String baseWorkDir) throws Exception {
        final Date now = new Date();
        final String inputXml = baseName + "-input.xml";
        final String referenceDiff = baseName + "-reference.diff";
        final String outputDiff = baseName + "-output_" + LOGFORMAT.format(now) + ".diff";
        final String errorLog = baseName + "-error_" + LOGFORMAT.format(now) + ".log";
        File inputXmlFile = new File(baseInputDir, inputXml);
        File refrenceDiffFile = new File(baseInputDir, referenceDiff);
        File outputDiffFile = new File(baseWorkDir, outputDiff);
        File errorLogFile = new File(baseWorkDir, errorLog);

        Assert.assertTrue("Input xml file cannot be found.", inputXmlFile.exists());
        Assert.assertTrue("Reference diff file cannot be found.", refrenceDiffFile.exists());

        final String[] args = new String[] { inputXmlFile.getAbsolutePath(), outputDiffFile.getAbsolutePath(), errorLogFile.getAbsolutePath() };
        RepairToolSwitcher.main(args);

        if (!outputDiffFile.exists()) {
            logger.warn("======================== TEST FAILED: {} ========================", baseName);
            if (errorLogFile.exists()) {
                logger.error(FileUtils.readFileToString(errorLogFile, StandardCharsets.UTF_8));
                Assert.fail("Running tool (" + baseName + ") failed. No output diff found.");
            } else {
                Assert.fail("Running tool (" + baseName + ") failed. No output diff found. No error log found.");
            }
        }

        boolean contentEqualsIgnoreEOL = FileUtils.contentEqualsIgnoreEOL(refrenceDiffFile, outputDiffFile,
                StandardCharsets.UTF_8.name());
        if (!contentEqualsIgnoreEOL) {
            logger.warn("======================== TEST FAILED: {} ========================", baseName);
            List<String> outputLines = FileUtils.readLines(outputDiffFile, StandardCharsets.UTF_8);
            List<String> referenceLines = FileUtils.readLines(refrenceDiffFile, StandardCharsets.UTF_8);
            Patch diff = DiffUtils.diff(referenceLines, outputLines);
            logger.warn("Output diff ({}) not equals reference diff ({}).", outputDiffFile, refrenceDiffFile);
            List<String> generateUnifiedDiff = DiffUtils.generateUnifiedDiff(refrenceDiffFile.getAbsolutePath(), outputDiffFile.getAbsolutePath(),
                    referenceLines, diff, 0);
            for (String line : generateUnifiedDiff) {
                logger.debug("{}", line);
            }
            logger.warn("=================================================================", baseName);
        }
        Assert.assertTrue("Output does not match with golden reference.", contentEqualsIgnoreEOL);
    }
}
