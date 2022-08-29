package org.processmining.specpp.config.parameters;

import org.processmining.specpp.util.PathTools;

import java.io.File;

public class OutputPathParameters implements Parameters {

    private final String rootPath;
    private final String globalFilenamePrefix;
    private final String globalFilenamePostfix;


    public OutputPathParameters(String rootPath, String globalFilenamePrefix, String globalFilenamePostfix) {
        this.rootPath = rootPath;
        this.globalFilenamePrefix = globalFilenamePrefix;
        this.globalFilenamePostfix = globalFilenamePostfix;
    }


    public static OutputPathParameters getDefault() {
        return new OutputPathParameters("", "", "");
    }

    public static OutputPathParameters ofPrefix(String prefix) {
        return new OutputPathParameters("", prefix, "");
    }

    private String getBasePath(PathTools.OutputFileType fileType) {
        PathTools.FolderStructure relativeTo = PathTools.isRelativeTo(fileType);
        if (relativeTo == PathTools.FolderStructure.BASE_OUTPUT_FOLDER) {
            return rootPath;
        }
        throw new NoPathFoundException();
    }

    private String getBasePath(PathTools.FolderStructure folderStructure) {
        PathTools.FolderStructure relativeTo = PathTools.isRelativeTo(folderStructure);
        if (relativeTo == PathTools.FolderStructure.BASE_OUTPUT_FOLDER) {
            return rootPath;
        }
        throw new NoPathFoundException();
    }

    public String getFolderPath(PathTools.FolderStructure folderStructure) {
        String path = getBasePath(folderStructure) + PathTools.getRelativeFolderPath(folderStructure);
        ensureFolderStructureExists(path);
        return path;
    }

    private synchronized void ensureFolderStructureExists(String folderPath) {
        File f = new File(folderPath);
        if (!f.exists()) {
            boolean mkdirs = f.mkdirs();
            if (!mkdirs && !f.exists()) throw new CannotCreateFolderException();
        }
    }

    public String getFilePath(PathTools.OutputFileType fileType, String name) {
        String folderPath = getBasePath(fileType) + PathTools.getRelativeFolderPath(fileType);

        ensureFolderStructureExists(folderPath);

        return folderPath + PathTools.getRelativeFilePath(fileType, name, globalFilenamePrefix, globalFilenamePostfix);
    }

    public String getFilePath(PathTools.OutputFileType fileType, String name, String fileExtension) {
        return getFilePath(fileType, name) + fileExtension;
    }

    @Override
    public String toPrettyString() {
        return toString();
    }

    public String getRootPath() {
        return rootPath;
    }

    @Override
    public String toString() {
        return "OutputPathParameters{" + "baseOutputPath='" + rootPath + '\'' + ", globalFilenamePrefix='" + globalFilenamePrefix + '\'' + ", globalFilenamePostfix='" + globalFilenamePostfix + '\'' + '}';
    }

    public static class NoPathFoundException extends RuntimeException {
    }

    public static class CannotCreateFolderException extends RuntimeException {
    }
}
