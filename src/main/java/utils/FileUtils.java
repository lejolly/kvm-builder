package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static void writeLine(BufferedWriter bufferedWriter, String line) throws IOException {
        bufferedWriter.write(line);
        bufferedWriter.newLine();
    }

    public static void printAndWriteLine(BufferedWriter bufferedWriter, String line) throws IOException {
        System.out.println(line);
        writeLine(bufferedWriter, line);
    }

    public static BufferedWriter createAndWriteToFile(Path path) {
        try {
            Files.createFile(path);
            return Files.newBufferedWriter(path, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createFile(Path path) {
        try {
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkAndCreateThenClearDirectory(Path path) {
        try {
            if (!Files.isDirectory(path)) {
                // if a file of the same name as the folder exists, delete it
                if (Files.exists(path)) {
                    Files.delete(path);
                }
                // create directory
                Files.createDirectories(path);
            }
            // clear directory
            clearDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearDirectory(Path path) throws IOException {
        Files.newDirectoryStream(path).forEach(fileOrFolder -> {
            try {
                if (Files.isDirectory(fileOrFolder) && Files.list(fileOrFolder).findAny().isPresent()) {
                    clearDirectory(fileOrFolder);
                }
                Files.delete(fileOrFolder);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

}
