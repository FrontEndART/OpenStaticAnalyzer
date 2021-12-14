package coderepair.generator.transformation;

import java.util.List;

import coderepair.communication.exceptions.RepairAlgorithmRuntimeException;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * This class creates a diff from multiple files.
 */
public class DiffBuilder {

    private static final String NEWLINE = System.getProperty("line.separator");

    final private StringBuilder diff;
    private String workspaceDir;

    public DiffBuilder() {
        this.diff = new StringBuilder();
    }

    /**
     * 
     * @param prefix the prefix of the path which should be removed
     */
    public DiffBuilder(String prefix) {
        this();
        if (prefix == null) {
            throw new RepairAlgorithmRuntimeException("WorkspaceDir cannot be null!");
        }
        this.workspaceDir = prefix.replace("\\", "/");
    }

    /**
     * Adds a delta to the diff.
     * 
     * @param originalLines the original lines of code.
     * @param revisedLines the modified lines of code.
     * @param filePath the position of the original file.
     * @return this object.
     */
    public DiffBuilder addDiff(List<String> originalLines, List<String> revisedLines, String filePath) {
        String relativePath = convert(filePath);
        final List<String> patch = createPatchOfFile(originalLines, revisedLines, relativePath);
        // add index to patches
        this.diff.append("Index: ");
        // TODO MAYBE: keep the diffs for the same file under the same "index" section.
        this.diff.append(relativePath);
        this.diff.append(NEWLINE);
        this.diff.append("===================================================================");
        this.diff.append(NEWLINE);
        // add the patch to the diff
        for (String line : patch) {
            this.diff.append(line);
            if (!(line.endsWith("\n") || line.endsWith("\r"))) {
                this.diff.append(NEWLINE);
            }
        }
        return this;
    }

    private String convert(String filePath) {
        // convert to java diff (on windows)
        filePath = filePath.replace("\\", "/");
        // convert absolute path to relative path
        if (!filePath.isEmpty() && this.workspaceDir != null) {
            // remove path prefix
            filePath = filePath.replace(this.workspaceDir, "");
        }
        // remove starting slash if exist
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        return filePath;
    }

    private List<String> createPatchOfFile(List<String> originalLines, List<String> revisedLines, String filePath) {
        final Patch patch = DiffUtils.diff(originalLines, revisedLines);
        return DiffUtils.generateUnifiedDiff(filePath + "\t(original)", filePath + "\t(refactored)", originalLines, patch, 3);
    }

    /**
     * Builds the unified diff according to the given deltas.
     * 
     * @return the diff in unified diff format as a string.
     */
    public String build() {
        return this.diff.toString();
    }

    @Override
    public String toString() {
        return this.diff.toString();
    }

}
