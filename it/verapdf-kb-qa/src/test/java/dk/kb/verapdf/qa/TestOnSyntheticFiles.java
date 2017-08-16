package dk.kb.verapdf.qa;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import dk.statsbiblioteket.digital_pligtaflevering_aviser.verapdf.VeraPDFValidator;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class contain integration tests working on boring plain PDF files generated on the fly. It is intended that
 * these should generate as little interesting work by VeraPDF as possible.
 */
public class TestOnSyntheticFiles {

    public byte[] createSimplePdf(String paragraph) {
        try {
            // http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-1
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Document document = new Document();
            //PdfAWriter.getInstance(document, baos, PdfAConformanceLevel.PDF_A_1A);
            PdfWriter.getInstance(document, baos);
            document.open();
            document.add(new Paragraph(paragraph));
            document.close();

            try (OutputStream os = new FileOutputStream("/tmp/x.pdf")) {
                baos.writeTo(os);
            }
            return baos.toByteArray();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void checkForLeakingFileDescriptorsOnSyntheticFiles() throws Exception {
        Assert.assertTrue(createSimplePdf("Hello world").length > 0);

        long processed = 0;

        long processLimit = 100_000;

        VeraPDFValidator validator = new VeraPDFValidator("1b", true);

        while (true) {
            if (processed++ > processLimit) {
                Assert.assertTrue(true);
                return; // ok.
            }

            byte[] simplePdf = createSimplePdf("Hello #" + processed);
            try {
                final InputStream inputStream = new ByteArrayInputStream(simplePdf);
                byte[] b = validator.apply(inputStream);
                inputStream.close();
            } catch (Exception e) {
                System.err.println(processed + " failed: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }
}

