package mid.crawlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mid.Requester;

public abstract class Crawler {
	public abstract void continueCrawl(String hostURL) throws IOException;
	
	public void crawl(List<String> hostURLs) {
		for (String hostURL : hostURLs) {
			// Dirty hacky delays
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			try {
				continueCrawl(hostURL);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Skipping broken URL: " + hostURL);
			}
		}
	}
	
	public String getRequestPattern() {
		return requestPattern;
	}
	public String getImagePattern() {
		return imagePattern;
	}

	protected InputStream executeGet(String hostURL) {
		InputStream inStr = null;
		
		try {
			inStr = Requester.executeGet(hostURL);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// The host may be temporarily unresponsive.
		if (inStr == null) {
			System.out.println("Host unresponsive. Retrying...");
			try {
			    URI uri = new URI(hostURL);
			    String domain = uri.getHost();
				if (InetAddress.getByName(domain).isReachable(10000))
					inStr = Requester.executeGet(hostURL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		return inStr;
	}

	protected List<String> processGet(InputStream inStr) throws IOException {
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(inStr));
		Pattern p = Pattern.compile(requestPattern);
		List<String> urls = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			Matcher m = p.matcher(Pattern.quote(line));
			while (m.find()) {
				String hostURL = line.substring(
						m.start(), line.length()).split("[\"\']")[1];
				System.out.println("Crawler: " + hostURL);
				urls.add(hostURL);
			}
		}

		return urls;
	}
	
	protected String processImageRequest(InputStream inStr) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStr));
        String line = null;
        Pattern p = Pattern.compile(imagePattern);
		while ((line = br.readLine()) != null) {
			Matcher m = p.matcher(Pattern.quote(line));
			if (m.find()) {
				// Split URL from remaining attributes
				return m.group(0).split("[\"\' ]")[0];
			}
		}
		
		return "";
	}
	
	protected String requestPattern;
	protected String imagePattern;
}