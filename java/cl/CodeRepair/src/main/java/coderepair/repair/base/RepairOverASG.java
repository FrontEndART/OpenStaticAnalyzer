package coderepair.repair.base;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import coderepair.generator.transformation.DiffBuilder;
import coderepair.generator.transformation.ModifiedNodes;
import coderepair.generator.transformation.scpr.SourceCodePositionReverter;

import coderepair.communication.keys.RepairingKeys;
import coderepair.communication.exceptions.RepairAlgorithmException;
import columbus.ColumbusException;
import columbus.CsiHeader;
import columbus.StrTable;
import columbus.java.asg.Factory;

/**
 * This abstract class helps to load the ASG and the SourceCodePositionReverter.
 */
public abstract class RepairOverASG implements RepairTool {
    public  static Factory loadFactoryToASG(String asgLocation) {
        // creating Factory
        StrTable strTable = new StrTable();
        Factory factory = new Factory(strTable);

        // loading ASG
        CsiHeader csiHeader = new CsiHeader();
        try {
            factory.load(asgLocation, csiHeader);
        } catch (ColumbusException ex) {
            System.out.println("ERROR: " + ex);
            System.exit(1);
        }

        return factory;
    }

    @Override
    public final void doRepairs(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, URI srcDirURI, int tabSize)
            throws RepairAlgorithmException {
        final String asgLocation = (String) settings.get(RepairingKeys.SCHEMA_LOCATION);
        URI asgURI = null;
        try {
            // load ASG
            File asgFile = new File(asgLocation);
            if (!asgFile.exists() || !asgFile.canRead() || asgFile.isDirectory()) {
                final URL resource = Thread.currentThread().getContextClassLoader().getResource(asgLocation);
                if (resource == null) {
                    throw new IOException();
                }
                asgURI = resource.toURI();
            } else {
                asgURI = asgFile.getAbsoluteFile().toURI();
            }

            if (asgLocation.endsWith(".ljsi") || asgLocation.endsWith(".jsi")) {
                Factory factory = loadFactoryToASG(asgURI.getPath());
                SourceCodePositionReverter reverter = new SourceCodePositionReverter(factory);
                modifiedNodes.setFactory(factory);
                doRepairsOverAsg(settings, diff, modifiedNodes, factory, reverter, srcDirURI, tabSize);
            }
        } catch (IOException | URISyntaxException e) {
            throw new RepairAlgorithmException("IOError: ASG file doesn't exist at the location: " + asgLocation, e);
        }
    }

    /**
     * Use this method to refactor a problem over the ASG.
     * 
     * @param settings the settings from the refactoring wizard.
     * @param diff the diff builder which can make unified diff from multiple files.
     * @param modifiedNodes this object can be used to keep track of the nodes the refactoring has modified (or added or deleted).
     * @param factory the factory of the loaded ASG.
     * @param reverter the source code position reverter which can help you find a problem's node-id by a given lineinfo.
     * @param srcDirURI the location of the directory containing the source code.
     * @param tabSize the default tab size of the document
     * @throws RepairAlgorithmException if some error is happening.
     */
     protected abstract void doRepairsOverAsg(Map<String, Object> settings, DiffBuilder diff, ModifiedNodes modifiedNodes, Factory factory,
                                             SourceCodePositionReverter reverter, URI srcDirURI, int tabSize) throws RepairAlgorithmException;

}
