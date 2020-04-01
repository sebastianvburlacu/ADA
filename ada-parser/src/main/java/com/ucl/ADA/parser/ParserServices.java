package com.ucl.ADA.parser;

import com.google.common.collect.SetMultimap;
import com.ucl.ADA.parser.ada_model.ADAClass;
import com.ucl.ADA.parser.parser.ADAParser;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;


@Service
public class ParserServices {


    public ParserServices() {
    }

    /**
     * Parse the given code repository and generates a map of ADAClass
     *
     * @param repositoryPath root path of the source code repository
     * @param filePaths      set of file path(relative) that needs to be parsed
     * @return a map of parsed files in from of path->ADAClass
     */
    public SetMultimap<String, ADAClass> parseRepository(String repositoryPath, Set<String> filePaths) {
        return new ADAParser().getParsedSourceFile(repositoryPath, filePaths);
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        String repositoryPath = "temp/alexandar1000/ADA/master/2020-03-06-18-00-20";
        Set<String> filePaths = new HashSet<>();
        filePaths.add("ada-parser/src/main/java/com/ucl/ADA/parser/parser/ADAParser.java");
        SetMultimap<String, ADAClass> s = new ADAParser().getParsedSourceFile(repositoryPath, filePaths);
    }

}
