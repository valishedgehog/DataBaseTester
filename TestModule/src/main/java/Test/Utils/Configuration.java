package Test.Utils;

import Test.Exceptions.BadConfigException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Configuration {
    @JsonProperty
    private String testsFolder;

    @JsonProperty
    private String serverExecutable;

    @JsonProperty
    private int port;

    private boolean notNull() {
        return testsFolder != null && serverExecutable != null;
    }

    private boolean checkTestsFolder() {
        Path path = Paths.get(testsFolder);
        return Files.exists(path) && Files.isDirectory(path);
    }

    private boolean checkServerJar() {
        Path path = Paths.get(serverExecutable);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    public String getTestsFolder() {
        return testsFolder;
    }

    public String getServerExecutable() {
        return serverExecutable;
    }

    public int getPort() {
        return port;
    }

    public boolean check() {
        return notNull() && checkServerJar() && checkTestsFolder();
    }

    public Path getTestPath(String testName) {
        Path testFolderPath = Paths.get(testsFolder, testName);

        if (!(Files.exists(testFolderPath))) {
            return null;
        }

        return testFolderPath;
    }

    public static Configuration load() {
        Path config = Paths.get(System.getProperty("user.home"), ".dbms_testing_config");
        ObjectMapper mapper = new ObjectMapper();
        Configuration configuration = null;

        try {
            configuration = mapper.readValue(config.toFile(), Configuration.class);
            if (!configuration.check()) throw new BadConfigException();
        } catch (IOException | BadConfigException e) {
            Printer.printCriticalErrorAndExit(e);
        }

        return configuration;
    }
}