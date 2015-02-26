package george.shumakov.imgsort;

import java.io.FileInputStream;
import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;
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
            out.println("Must has at least one parameter file name.");
            exit(-1);
        }
        try (InputStream is = new FileInputStream(args[0])) {
            out.println(parseExample(is));
        }

    }

    public static String parseExample(final InputStream stream) throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try {
            parser.parse(stream, handler, metadata);
            return handler.toString();
        } finally {
            stream.close();
        }
    }
}
