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
public abstract class ReportsCommand extends Command {

    /** Directory containing the reports to be processed. */
    @Parameter(
            description = "directory with reports to process, ignored when list of reports is provided",
            names = { "-d", "/D", "--directory" })
    public String directory = null;

    /** List of reports to process. */
    @Parameter(
            description = "[reports to process]")
    public List<String> files = new ArrayList<>();

    /** Iterates through input files. */
    protected ThrowingConsumer<ThrowingConsumer<InputStream>> reportsIterator;

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
                        consumer.accept(Files.newInputStream(p));
                    }
                }
            };
        } else {
            reportsIterator = consumer -> {
                for (String file : files) {
                    consumer.accept(Files.newInputStream(Paths.get(file)));
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
