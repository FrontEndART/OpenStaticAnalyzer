package coderepair.generator;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import coderepair.generator.support.ModifiedNodesPrinter;
import coderepair.generator.support.ModifiedNodesPrinter.SwitchThis;

import coderepair.generator.fileeditor.FileEditor;

import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.communication.exceptions.RepairAlgIllegalArgumentException;
import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import com.google.gdata.util.common.base.CharEscapers;

/**
 * Class for change the files by inserting and deleting strings.
 */
public class SourceCodeGenerator {

    public static final int DEFAULT_TABSIZE = 4;

    private String tabStr;
    private int tabSize;
    private URI srcDirURI;

    /**
     * Constructor.
     * 
     * @param srcDirURI The source directory's URI.
     * @param inputsTabSize The input tab size.
     * @param outputTabStr The output tab string.
     */
    public SourceCodeGenerator(URI srcDirURI, int inputsTabSize, String outputTabStr) {
        this(outputTabStr, inputsTabSize);
        this.srcDirURI = srcDirURI;
    }

    /**
     * Constructor.
     * 
     * @param tabString The input tab size.
     * @param tabSize The input tab size.
     */
    public SourceCodeGenerator(String tabString, int tabSize) {
        super();
        this.tabStr = tabString;
        this.tabSize = tabSize;
    }

    /**
     * Generates a diff string from modified nodes.
     * 
     * @param modifiedNodes The ModifiedNodes object.
     * @param diff The DiffBuilder to build diff.
     * @return The generated diff.
     * @throws RepairAlgorithmException If the ModifiedNodes or the Diffbuilder is null, or the ModifiedNodes' factory is empty.
     */
    public String generateDiff(ModifiedNodes modifiedNodes, DiffBuilder diff) throws RepairAlgorithmException {
        if (modifiedNodes == null) {
            throw new RepairAlgIllegalArgumentException("ModifiedNodes cannot be null!");
        } else if (!modifiedNodes.isEmpty() && modifiedNodes.getFactory() == null) {
            throw new RepairAlgIllegalArgumentException("Factory attribute of ModifiedNodes has to be set when ModifiedNodes is not empty!");
        } else if (diff == null) {
            throw new RepairAlgIllegalArgumentException("DiffBuilder cannot be null!");
        }
        if (!modifiedNodes.isEmpty()) {
            Map<String, FileEditor> feditorMap = new HashMap<String, FileEditor>();
            ModifiedNodesPrinter printer = new ModifiedNodesPrinter(modifiedNodes, this.srcDirURI, this.tabSize, this.tabStr);
            List<SwitchThis> swchlist = printer.calculate();

            for (Iterator<SwitchThis> iter = swchlist.iterator(); iter.hasNext();) {
                SwitchThis swch = iter.next();
                String filePath = swch.getFilePath();

                FileEditor editor = null;
                if (!feditorMap.containsKey(filePath)) {
                    try {
                        editor = new FileEditor(this.srcDirURI.resolve(CharEscapers.uriPathEscaper().escape(filePath.replace("\\", "/"))/*Utils.resolvePath(this.srcDirURI, filePath.replace("\\", "/")*/), this.tabSize);

                        feditorMap.put(filePath, editor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    editor = feditorMap.get(filePath);
                }

                editor.write(swch.getPart(), swch.getSwitchTo());
            }

            for (Iterator<Entry<String, FileEditor>> iter = feditorMap.entrySet().iterator(); iter.hasNext();) {
                Entry<String, FileEditor> entry = iter.next();
                List<String> oldFile = null;
                List<String> newFile = entry.getValue().toList();
                try {
                    oldFile = FileEditor.fileIntoLines(this.srcDirURI.resolve(CharEscapers.uriPathEscaper().escape(entry.getKey().replace("\\", "/"))));/*Utils.resolvePath(this.srcDirURI, entry.getKey().replace("\\", "/")));*/
                } catch (IOException e) {
                    e.printStackTrace();
                }

                diff.addDiff(oldFile, newFile, entry.getKey());
            }
        }

        return diff.build();
    }
}
