package com.github.korotovskih.bsl;

import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLParserRuleContext;
import com.github._1c_syntax.bsl.parser.Tokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Files;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "make", mixinStandardHelpOptions = true, version = "1.0",
        description = "make coverage report")
class GenericReport implements Callable<Integer> {

    @Parameters(index = "0", description = "*.bsl")
    private File infile;

    @Parameters(index = "1", description = "*.xml")
    private File outFile;

    public static void main(String... args) {
        int exitCode = new CommandLine(new GenericReport()).execute(args);
        System.exit(exitCode);
    }

    private static boolean mustCovered(Tree node) {
        // the same as in BSL LS
        return node instanceof BSLParser.StatementContext
                || node instanceof BSLParser.GlobalMethodCallContext
                || node instanceof BSLParser.Var_nameContext;
    }

    public void bslFormFolder(File folder, List<File> lst)
    {
        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries)
        {
            if (entry.isDirectory())
            {
                bslFormFolder(entry, lst);
                continue;
            }
            lst.add(entry);
        }
    }

    public int[] linesToCover(File bslFile) throws IOException {
        URI uri = bslFile.toURI();
        Path path = Path.of(uri);
        String content = Files.readString(path);
        Tokenizer tokenizer = new Tokenizer(content);
        int[] linesToCover = Trees.getDescendants(tokenizer.getAst()).stream()
                .filter(node -> !(node instanceof TerminalNodeImpl))
                .filter(GenericReport::mustCovered)
                .mapToInt(node -> ((BSLParserRuleContext) node).getStart().getLine())
                .distinct().toArray();
        return linesToCover;
    }

    @Override
    public Integer call() throws Exception {

        List<File> lst = new ArrayList<>();
        bslFormFolder(infile, lst);

        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder = icFactory.newDocumentBuilder();
        Document doc = icBuilder.newDocument();

        Element root = doc.createElement("coverage");
        root.setAttribute("version", "1");
        doc.appendChild(root);

        for (File entry : lst)
        {
            int[] linesToCover = linesToCover(entry);
            Element fileElement = doc.createElement("file");
            fileElement.setAttribute("path", entry.getCanonicalPath());
            for (int line : linesToCover)
            {
                Element lineElement = doc.createElement("lineToCover");
                lineElement.setAttribute("covered", "false");
                lineElement.setAttribute("lineNumber", String.valueOf(line));
                fileElement.appendChild(lineElement);
            }
            root.appendChild(fileElement);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult stream = new StreamResult(new FileOutputStream(outFile));

        transformer.transform(source, stream);
        return 0;
    }



}
