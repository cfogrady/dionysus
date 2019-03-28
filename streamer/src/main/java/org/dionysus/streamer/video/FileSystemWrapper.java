package org.dionysus.streamer.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import javax.inject.Named;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Named
public class FileSystemWrapper {
    private static Logger logger = LoggerFactory.getLogger(FileSystemWrapper.class);

    public Stream<Path> listDirectoryContents(Path path) {
        try {
            return Files.list(path);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public Path getPath(String path) {
        return Paths.get(path);
    }

    public FileSystemResource buildFileSystemResource(String path) {
        return new FileSystemResource(path);
    }
}
