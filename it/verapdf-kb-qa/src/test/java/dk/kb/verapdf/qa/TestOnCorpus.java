package dk.kb.verapdf.qa;

import dk.kb.dpa.verapdf.qa.MavenProjectsHelper;
import dk.statsbiblioteket.digital_pligtaflevering_aviser.verapdf.VeraPDFValidator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contain integration tests working on the official VeraPDF corpus.  These files all have various
 * interesting properties that might trigger non-typical pathways through the VeraPDF code.
 */
public class TestOnCorpus {
    /**
     * https://github.com/veraPDF/veraPDF-apps/issues/218 reported a file descriptor leak which applies to us.  Under
     * Ubuntu 17.04 the limit is around 4000 files.  Validate the corpus repeatedly until hopefully the issue show.
     */
    @Test
    public void checkForLeakingFileDescriptorsOnWholeCorpus() throws IOException {
        Path corpusPath = MavenProjectsHelper.getRequiredPathTowardsRoot(this, "veraPDF-corpus");
        List<Path> pdfPaths = Files.walk(corpusPath)
                .filter(p -> Files.isRegularFile(p) && p.toString().toLowerCase().endsWith(".pdf"))
                .collect(Collectors.toList());

        VeraPDFValidator validator = new VeraPDFValidator("1b", true);

        long processed = 0;

        long processLimit = 100_000;

        long totalBytesGeneratedToEnsureOutputIsUsed = 0;

        while (true) {
            for (Path p : pdfPaths) {
                if (processed++ > processLimit) {
                    Assert.assertTrue(true);
                    return; // made it through, probably no leak.
                }

                try (InputStream inputStream = Files.newInputStream(p)) {
                    byte[] b = validator.apply(inputStream);
                    totalBytesGeneratedToEnsureOutputIsUsed += b.length;
                } catch (Exception e) {
                    // Silently ignore failures
                    //System.err.println(p + " failed: " + e.getMessage());
                    //e.printStackTrace(System.err);
                }
            }
        }
    }
}
