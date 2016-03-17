package mid;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mid.crawlers.*;

public class Main {
	public static void main(String[] args) {
		System.out.println("Enter URL or file path...");
		
		// Read URL
		BufferedReader br = null;
		String target = "";
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			target = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (target == null || target.isEmpty()) {
			System.out.println("No target passed. Exiting...");
			return;
		}
		
		try {
			processInitialRequest(streamFromURL(target));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static InputStream streamFromURL(String url) throws IOException {
		return Requester.executeGet(url);
	}

	private static InputStream streamFromFile(String url) throws FileNotFoundException {
		return new FileInputStream(url);
	}
	
	private static void processInitialRequest(InputStream inStr) {
		// Create folder to save images
		long unixTimestamp = Instant.now().getEpochSecond();
		String saveFolderName = Long.toString(unixTimestamp);
		File saveFolder = new File(saveFolderName);
		if (saveFolder != null && !saveFolder.exists())
			saveFolder.mkdirs();
		Requester.setSaveFolderName(saveFolderName);

		// Search for URLs in the page source
		BufferedReader br = new BufferedReader(new InputStreamReader(inStr));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				for (Crawler c : crawlers) {
					// A line can have multiple urls to crawl
					List<String> hostURLs = new ArrayList<String>();
					Pattern p = Pattern.compile(c.getRequestPattern());
					Matcher m = p.matcher(Pattern.quote(line));
					while (m.find()) {
						String hostURL = line.substring(
								m.start(), line.length()).split("[\" ]")[1];
						System.out.println("Will crawl: " + hostURL);
						hostURLs.add(hostURL);
					}
					c.crawl(hostURLs);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();	
		// Delete empty folder
		} finally {
			saveFolder.delete();
		}
		
		System.out.println("All images downloaded. Have fun~!");
	}

	private static final List<Crawler> crawlers;
	static {
		crawlers = new ArrayList<Crawler>(Arrays.asList(
		        new Chronos(),
		        new ImageBam(),
		        new ImgSense(),
		        new ImgSpot(),
		        new Imgyt(),
		        new MyIMG()));
	}
}