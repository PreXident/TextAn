package cz.cuni.mff.ufal.textan.police;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import static cz.cuni.mff.ufal.textan.police.Policer.ARGUMENT_ERROR;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Common ancestor for commands processing list of files.
 */
public abstract class FilesCommand extends Command {

    /** Directory containing the files to be processed. */
    @Parameter(
            description = "directory with reports to process, ignored when list of files is provided",
            names = { "-d", "/D", "--directory" })
    public String directory = null;

    /** Indicator whether filenames should be printed. */
    @Parameter(
            description = "prints name of file before processing it to err",
            names = { "-v", "/V", "--verbose" })
    public boolean verbose = false;

    /** List of reports to process. */
    @Parameter(
            description = "[reports to process]")
    public List<String> files = new ArrayList<>();

    /** Iterates through input files. */
    protected ThrowingConsumer<ThrowingConsumer<Path>> reportsIterator;

    @Override
    public int checkParameters() {
        return files.isEmpty() && directory == null ? ARGUMENT_ERROR : 0;
    }

    @Override
    protected void prepare(final Options options) {
        if (files.isEmpty() && directory == null) {
            throw new ParameterException("Nothing to process!");
        }
        if (files.isEmpty() && directory != null) {
            final Path path = Paths.get(directory);
            if (!Files.isDirectory(path)) {
                throw new ParameterException(String.format("No directory found at \"%s\"", directory));
            }
            reportsIterator = consumer -> {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path p : stream) {
                        if (verbose) {
                            System.err.println(p.toAbsolutePath().toString());
                        }
                        consumer.accept(p);
                    }
                }
            };
        } else {
            reportsIterator = consumer -> {
                for (String file : files) {
                    final Path path = Paths.get(file);
                    if (verbose) {
                        System.err.println(path.toAbsolutePath().toString());
                    }
                    consumer.accept(path);
                }
            };
        }
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T> {

        /**
         * Performs this operation on the given argument.
         *
         * @param t the input argument
         * @throws Exception if any error occurs
         */
        void accept(T t) throws Exception;
    }
}
