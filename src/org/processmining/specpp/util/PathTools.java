package org.processmining.specpp.util;

import org.apache.commons.text.StringEscapeUtils;

public class PathTools {

    public static final String PATH_FOLDER_SEPARATOR = "/";

    public enum FolderStructure {
        BASE_OUTPUT_FOLDER("output"),
        LOG_FOLDER(BASE_OUTPUT_FOLDER, "logs"),
        EXPORT_FOLDER(BASE_OUTPUT_FOLDER, "exports"),
        VIS_FOLDER(BASE_OUTPUT_FOLDER, "vis"),
        BASE_INPUT_FOLDER("input"),
        EVENT_LOG_FOLDER(BASE_INPUT_FOLDER, "event_logs");

        private final FolderStructure parent;
        private final String name;

        FolderStructure(String name) {
            this(null, name);
        }

        FolderStructure(FolderStructure parent, String name) {
            this.parent = parent;
            this.name = name;
        }
    }

    public enum OutputFileType {
        MAIN_LOG(FolderStructure.LOG_FOLDER, ".log"),
        SUB_LOG(FolderStructure.LOG_FOLDER, ".log"),
        CHART(FolderStructure.VIS_FOLDER, "chart_", "", ".png"),
        GRAPH(FolderStructure.VIS_FOLDER, "graph_", "", ".png"),
        MISC_EXPORT(FolderStructure.EXPORT_FOLDER),
        CSV_EXPORT(FolderStructure.EXPORT_FOLDER, ".csv");

        private final FolderStructure folder;
        private final String prefix, postfix, extension;

        OutputFileType(FolderStructure folder) {
            this(folder, "", "", "");
        }

        OutputFileType(FolderStructure folder, String extension) {
            this(folder, "", "", extension);
        }

        OutputFileType(FolderStructure folder, String prefix, String postfix, String extension) {
            this.folder = folder;
            this.prefix = prefix;
            this.postfix = postfix;
            this.extension = extension;
        }
    }

    public static String weakSanitize(String fileName) {
        return StringEscapeUtils.escapeJava(fileName.replace('\\', ' '));
    }

    public static String getRelativeFilePath(OutputFileType outputFileType, String name, String prefix, String postfix) {
        return prefix + outputFileType.prefix + weakSanitize(name) + outputFileType.postfix + postfix + outputFileType.extension;

    }

    public static String getRelativeFilePath(OutputFileType outputFileType, String name) {
        return outputFileType.prefix + weakSanitize(name) + outputFileType.postfix + outputFileType.extension;
    }

    public static FolderStructure isRelativeTo(OutputFileType fileType) {
        return isRelativeTo(fileType.folder);
    }


    public static FolderStructure isRelativeTo(FolderStructure folder) {
        if (folder.parent == null) return folder;
        else return isRelativeTo(folder.parent);
    }

    public static String getRelativeFolderPath(OutputFileType fileType) {
        return getRelativeFolderPath(fileType.folder);
    }

    public static String getRelativeFolderPath(FolderStructure folder) {
        if (folder.parent == null) return folder.name + PATH_FOLDER_SEPARATOR;
        else return getRelativeFolderPath(folder.parent) + folder.name + PATH_FOLDER_SEPARATOR;
    }


}
