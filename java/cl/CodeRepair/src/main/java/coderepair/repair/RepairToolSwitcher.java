package coderepair.repair;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import coderepair.generator.SourceCodeGenerator;
import coderepair.repair.rules.RepairRule_EI_EXPOSE_REP2;
import coderepair.repair.rules.RepairRule_EI_EXPOSE_REP2_Date_object;
import coderepair.repair.rules.RepairRule_EI_EXPOSE_REP2_array;
import coderepair.repair.rules.RepairRule_MS_SHOULD_BE_FINAL;
import coderepair.repair.rules.RepairRule_NP_NULL;
import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.communication.base.RepairAlgorithm;
import coderepair.communication.base.RepairAlgorithmRunner;
import coderepair.communication.keys.*;
import coderepair.communication.exceptions.RepairAlgorithmException;
import coderepair.communication.exceptions.RepairAlgorithmRuntimeException;
import coderepair.repair.base.RepairTool;

public class RepairToolSwitcher extends RepairAlgorithm {

    private static final int DEFAULT_INPUT_TAB_SIZE = 1;
    private static final String DEFAULT_OUTPUT_TAB_STR = "    ";

    public RepairToolSwitcher() {
        super();
    }

    @Override
    protected String doRepairs(Map<String, Object> settings) throws RepairAlgorithmException {
        if (!settings.containsKey(RepairingKeys.SRC_LOCATION)) {
            throw new RepairAlgorithmRuntimeException("Source code location is not set.");
        }

        URI srcDirURI = null;
        final String srcLocation = (String) settings.get(RepairingKeys.SRC_LOCATION);
        try {
            File srcDir = new File(srcLocation);
            if (!srcDir.exists() || !srcDir.canRead() || !srcDir.isDirectory()) {
                URL resource = Thread.currentThread().getContextClassLoader().getResource(srcLocation);
                if (resource == null) {
                    throw new IOException();
                }
                srcDirURI = resource.toURI();
            } else {
                srcDirURI = srcDir.getAbsoluteFile().toURI();
            }
        } catch (IOException e) {
            throw new RepairAlgorithmException("IOError: Src dir doesn't exist at the location: " + srcLocation, e);
        } catch (URISyntaxException e) {
            throw new RepairAlgorithmException("URISyntaxException in " + srcLocation, e);
        }

        int tabSize = DEFAULT_INPUT_TAB_SIZE;
        if (settings.containsKey(RepairingKeys.INPUT_TAB_SIZE)) {
            tabSize = Integer.parseInt((String) settings.get(RepairingKeys.INPUT_TAB_SIZE));
        }

        String tabStr = DEFAULT_OUTPUT_TAB_STR;
        if (settings.containsKey(RepairingKeys.OUTPUT_TAB_STR)) {
            tabStr = (String) settings.get(RepairingKeys.OUTPUT_TAB_STR);
        }

        final DiffBuilder diff = new DiffBuilder();
        final ModifiedNodes modifiedNodes = new ModifiedNodes();
        final SourceCodeGenerator sourceCodeGenerator = new SourceCodeGenerator(srcDirURI, tabSize, tabStr);

        final String problemType = (String) settings.get(RepairingKeys.PROBLEM_TYPE);
        RepairTool repair;
        switch(problemType) {
            case CommonSecurityRuleKeys.EI_EXPOSE_REP2_PROBLEM_TYPE:
                repair = new RepairRule_EI_EXPOSE_REP2();
                break;
            case CommonSecurityRuleKeys.EI_EXPOSE_REP2_ARRAY_PROBLEM_TYPE:
                repair = new RepairRule_EI_EXPOSE_REP2_array();
                break;
            case CommonSecurityRuleKeys.EI_EXPOSE_REP2_DATEOBJECT_PROBLEM_TYPE:
                repair = new RepairRule_EI_EXPOSE_REP2_Date_object();
                break;
            case CommonSecurityRuleKeys.MS_SHOULD_BE_FINAL_PROBLEM_TYPE:
                repair = new RepairRule_MS_SHOULD_BE_FINAL();
                break;
            case CommonSecurityRuleKeys.NP_NULL_ON_SOME_PATH:
            case CommonSecurityRuleKeys.NP_NULL_ON_SOME_PATH_EXCEPTION:
                repair = new RepairRule_NP_NULL();
                break;
            default:
                throw new RepairAlgorithmException("No repair algorithm found for the problemType: " + problemType);
        }

        try {
            repair.doRepairs(settings, diff, modifiedNodes, srcDirURI, tabSize);
        } catch (IllegalArgumentException e) {
            throw new RepairAlgorithmException(e);
        }

        return sourceCodeGenerator.generateDiff(modifiedNodes, diff);
    }

    @Override
    protected String getToolName() {
        return "RepairToolSwitcher";
    }

    public static void main(String[] args) throws IOException {
        RepairAlgorithmRunner rar = new RepairAlgorithmRunner();
        rar.run(args, new RepairToolSwitcher());
    }

}
