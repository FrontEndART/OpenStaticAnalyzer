package coderepair.communication.base;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import java.nio.charset.StandardCharsets;

import coderepair.communication.exceptions.RepairAlgorithmException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

public abstract class RepairAlgorithm {

    private Logger logger = LoggerFactory.getLogger(RepairAlgorithm.class);

    private Map<String, Object> settings;
    private File inputFile;
    private File outputFile;
    private File errorFile;

    public RepairAlgorithm() {
        super();
    }

    public void init(String args[]) throws IOException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Incorrect number of input arguments!");
        }

        this.inputFile = new File(args[0]);
        this.outputFile = new File(args[1]);
        this.errorFile = new File(args[2]);

        if (!this.inputFile.exists() || !this.inputFile.canRead()) {
            throw new IOException("The given inputfile (" + this.inputFile + ") does not exist.");
        }
        if (this.outputFile.exists() && !this.outputFile.canWrite()) {
            throw new IOException("Cannot write the outputfile (" + this.outputFile + ") specified.");
        }

        if (this.errorFile.exists() && !this.errorFile.canWrite()) {
            throw new IOException("Cannot write the outputfile (" + this.errorFile + ") specified.");
        }
    }

    @SuppressWarnings("unchecked")
    public void readInput() throws RepairAlgorithmException {
        try {
            XStream xstream = new XStream(new DomDriver(StandardCharsets.UTF_8.name()));
            xstream.addPermission(AnyTypePermission.ANY );
            System.out.println("File: " + this.inputFile);
            String file = FileUtils.readFileToString(this.inputFile, StandardCharsets.UTF_8);
		
            try {
                this.settings = (Map<String, Object>) xstream.fromXML(file);
            } catch (XStreamException e) {
                this.logger.error("Error happened while reading file {}.", this.inputFile);
                System.out.println(e.getStackTrace());
                throw new IOException("Inputfile cannot be deserialized.", e);
            }
        } catch (IOException e) {
            throw new RepairAlgorithmException(e);
        }
    }

    public final String refactoring() throws RepairAlgorithmException {
        return doRepairs(this.settings);
    }

    protected abstract String doRepairs(final Map<String, Object> settings) throws RepairAlgorithmException;

    public void writeOutput(String diff) throws RepairAlgorithmException {
        try {
            FileUtils.write(this.outputFile, diff, StandardCharsets.UTF_8);
        } catch (IOException e) {
            this.logger.error("Error happened while writing file {}.", this.outputFile);
            throw new RepairAlgorithmException(e);
        }
    }

    public void writeError(RepairAlgorithmException e) throws IOException {
        try {
            FileUtils.write(this.errorFile, e.getMessage(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            this.logger.error("Error happened while writing file {}.", this.errorFile);
            throw ex;
        }
    }

    public void deinit() {
        this.settings.clear();
        this.settings = null;
        this.inputFile = null;
        this.outputFile = null;
        this.errorFile = null;
    }

    protected abstract String getToolName();

}
