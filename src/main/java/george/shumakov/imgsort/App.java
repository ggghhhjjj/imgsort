package george.shumakov.imgsort;

import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

import static java.lang.System.exit;

import com.joestelmach.natty.Parser;

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
	// The cached pattern for case 5
	private static final Pattern P = Pattern.compile(Pattern.quote("date"),
			Pattern.CASE_INSENSITIVE);
	private static final Parser parser = new Parser();
	private static final Calendar cal = Calendar.getInstance();

	public static void main(String[] args) throws IOException, SAXException,
			TikaException {
		if (args.length < 1) {
			out.println("Must has at least one parameter dir name.");
			exit(-1);
		}

		Path sourcePath = Paths.get(args[0]);
		if (!Files.isDirectory(sourcePath)) {
			out.println("First argument must be dir path.");
			exit(-1);
		}

		Files.walk(sourcePath)
				.forEach(
						filePath -> {
							if (Files.isRegularFile(filePath)) {
								System.out.println(filePath);
								try (InputStream is = Files
										.newInputStream(filePath)) {
									Metadata metadata = getMetadata(is);
									final boolean noDateMetadata[] = { true };
									Arrays.stream(metadata.names())
											.forEach(
													strMetaName -> {
														if (P.matcher(
																strMetaName)
																.find()) {
															noDateMetadata[0] = false;
															parser.parse(
																	metadata.get(strMetaName))
																	.forEach(
																			group -> group
																					.getDates()
																					.forEach(
																							date -> {
																								cal.setTime(date);
																								int year = cal
																										.get(Calendar.YEAR);
																								int month = cal
																										.get(Calendar.MONTH) + 1;
																								int day = cal
																										.get(Calendar.DAY_OF_MONTH);
																								System.out
																										.println(" "
																												+ strMetaName
																												+ " : year= "
																												+ year
																												+ ", month= "
																												+ month
																												+ ","
																												+ " day= "
																												+ day);
																							}));
														}
													});
									if (noDateMetadata[0]) {
										System.out.println("no date metadata");
										cal.setTimeInMillis(Files
												.readAttributes(
														filePath,
														BasicFileAttributes.class)
												.creationTime().toMillis());
										int year = cal.get(Calendar.YEAR);
										int month = cal.get(Calendar.MONTH) + 1;
										int day = cal
												.get(Calendar.DAY_OF_MONTH);
										System.out.println("year= " + year
												+ ", month= " + month + ","
												+ " day= " + day);
									}
								} catch (IOException | SAXException
										| TikaException e) {
									e.printStackTrace();
								}
								System.out
										.println("-------------------------------------\n");
							}
						});

	}

	public static Metadata getMetadata(final InputStream stream)
			throws IOException, SAXException, TikaException {
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
