package coderepair.communication.base;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coderepair.communication.exceptions.RepairAlgorithmException;

public class RepairAlgorithmRunner {

    private static final Logger logger = LoggerFactory.getLogger(RepairAlgorithmRunner.class);

    public void run(String[] args, RepairAlgorithm alg) throws IOException {
        logger.info("{} tool started with these parameters:", alg.getToolName());
        logger.info(Arrays.toString(args));
        alg.init(args);
        try {
            alg.readInput();
            final String diff = alg.refactoring();
            alg.writeOutput(diff);
        } catch (RepairAlgorithmException e) {
            logger.warn("Error during tool execution.", e);
            alg.writeError(e);
        } finally {
            alg.deinit();
        }
        logger.info("{} tool finished.", alg.getToolName());
    }

}
