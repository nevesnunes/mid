package mid.crawlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mid.Requester;

public abstract class Crawler {
	public abstract void continueCrawl(String hostURL);
	
	public void crawl(List<String> hostURLs) {
		for (String hostURL : hostURLs) {
			// Dirty hacky delays
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			continueCrawl(hostURL);
		}
	}

	protected InputStream executeGet(String hostURL) {
		InputStream inStr = Requester.executeGet(hostURL);

		// The host may be temporarily unresponsive.
		if (inStr == null) {
			System.out.println("Host unresponsive. Retrying...");
			try {
			    URI uri = new URI(hostURL);
			    String domain = uri.getHost();
				if (InetAddress.getByName(domain).isReachable(5000))
					inStr = Requester.executeGet(hostURL);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		return inStr;
	}

	public List<String> processGet(InputStream inStr) {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStr));
		String line = null;
		Pattern p = Pattern.compile(requestPattern);
		List<String> urls = new ArrayList<String>();
		try {
			while ((line = br.readLine()) != null) {
				Matcher m = p.matcher(Pattern.quote(line));
				while (m.find()) {
					String hostURL = line.substring(
							m.start(), line.length()).split("\"")[1];
					System.out.println("Crawler: " + hostURL);
					urls.add(hostURL);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return urls;
	}
	
	public String processImageRequestRegex(InputStream inStr) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inStr));
	        String line = null;
	        Pattern p = Pattern.compile(imagePattern);
			while ((line = br.readLine()) != null) {
				Matcher m = p.matcher(Pattern.quote(line));
				if (m.find()) {
					return m.group(0);
				}
			}
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    }
		
		return "";
	}

	public String processImageRequest(InputStream inStr) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inStr));
	        String line = null;
	        int index = -1;
	        while ((line = br.readLine()) != null) {
	    		//System.out.println("Line: " + line);
	        	index = line.indexOf(imagePattern);
	            if (index != -1) {
	            	String imgURL = line.substring(index, line.length())
	            			.split(imageSplitPattern)[imageSplitIndex];
	            	return imgURL;
	            }
	        }
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    }
		
		return "";
	}
	
	public String getRequestPattern() {
		return requestPattern;
	}
	public String getImagePattern() {
		return imagePattern;
	}
	
	protected String requestPattern;
	protected String imagePattern;
	protected String imageSplitPattern;
	protected int imageSplitIndex;
}