package com.github.korotovskih.bsl;

import org.junit.Test;
import picocli.CommandLine;

import java.io.File;

import static org.junit.Assert.*;

public class GenericReportTest {

    @Test
    public void main() {
        String inFile = new File("src/test/resources").getAbsolutePath();
        String outFile = new File("report.xml").getAbsolutePath();
        int exitCode = new CommandLine(new GenericReport()).execute(inFile, outFile);
    }
}