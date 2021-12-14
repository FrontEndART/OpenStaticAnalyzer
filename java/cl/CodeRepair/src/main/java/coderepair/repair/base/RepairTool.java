package coderepair.repair.base;

import java.net.URI;
import java.util.Map;

import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;

public interface RepairTool {

    /**
     * Use this method to refactor a problem.
     * 
     * @param settings the settings from the refactoring wizard.
     * @param diff the diff builder which can make unified diff from multiple files.
     * @param modifiedNodes this object can be used to keep track of the nodes the refactoring has modified (or added or deleted).
     * @param srcDirURI the location of the directory containing the source code.
     * @param tabSize the default tab size of the document
     */
    void doRepairs(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, URI srcDirURI, int tabSize)
            throws RepairAlgorithmException;

    String getToolName();

}