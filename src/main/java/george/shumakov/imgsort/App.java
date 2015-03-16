package george.shumakov.imgsort;

import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.lang.System.exit;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, SAXException, TikaException {
        if (args.length < 1) {
            out.println("Must has at least one parameter dir name.");
            exit(-1);
        }

        Path sourcePath = Paths.get(args[0]);
        if (!Files.isDirectory(sourcePath)) {
            out.println("First argument must be dir path.");
            exit(-1);
        }

        Files.walk(sourcePath).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                System.out.println(filePath);
                try (InputStream is = Files.newInputStream(filePath)) {
                    Metadata metadata = getMetadata(is);
                    Arrays.stream(metadata.names()).forEach(metaName -> System.out.println(" " + metaName + " :" + metadata.get(metaName)));
                } catch (IOException | SAXException | TikaException e) {
                    e.printStackTrace();
                }
                System.out.println("-------------------------------------\n");
            }
        });

    }

    public static Metadata getMetadata (final InputStream stream) throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try {
            parser.parse(stream, handler, metadata);
            return metadata;
        } finally {
            stream.close();
        }
    }
}
