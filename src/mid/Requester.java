package mid;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Requester {
	public static InputStream executeGet(String targetURL) {
		return executeGet(targetURL, defaultProperties);
	}

	public static InputStream executeGet(String targetURL, List<Property> properties) {
		InputStream inStr = null;
		try {
			HttpURLConnection conn = generateConnection(targetURL);
	        for (Property p : properties)
	        	conn.setRequestProperty(p.key, p.value);
	        
			// Stream wrapper is based on encoding type
			String encoding = conn.getContentEncoding();
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				inStr = new GZIPInputStream(conn.getInputStream());
			} else {
				inStr = conn.getInputStream();
			}
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return inStr;
	}
	
	private static HttpURLConnection generateConnection(String targetURL) {
		// Sanitize URL
    	int index = targetURL.indexOf("http://");
        if (index == -1) {
        	targetURL = "http://" + targetURL;
        }
        
	    URL url = null;
		try {
		    url = new URL(targetURL);
		} catch (MalformedURLException mue) {
	        mue.printStackTrace();
	    }
		
	    HttpURLConnection conn = null;
	    try {
	    	conn = (HttpURLConnection) url.openConnection();
	    } catch (IOException ioe) {
	        ioe.printStackTrace();
	    }
        HttpURLConnection.setFollowRedirects(true);
		
		return conn;
	}

	public static InputStream executePostWithForm(String targetURL, List<Property> properties) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(targetURL);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Property p : properties)
        	builder.addTextBody(p.key, p.value, ContentType.TEXT_PLAIN);

		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);

		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(uploadFile);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HttpEntity responseEntity = response.getEntity();
		InputStream inStr = null;
	    try {
	        inStr = responseEntity.getContent();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    }
	    
	    return inStr;
	}
	
	public static void downloadImage(String imgURL) {
		try {
			URL url = new URL(imgURL);
			downloadImage(imgURL, url.openStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void downloadImage(String imgURL, InputStream postInStr) {
		BufferedImage image = null;
		try {
		    image = ImageIO.read(postInStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long unixTimestamp = Instant.now().getEpochSecond();
		File outputFile = new File(saveFolderName + "/" + Long.toString(unixTimestamp) + ".png");

		JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpegParams.setCompressionQuality(1f);
		try {
			final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			writer.setOutput(new FileImageOutputStream(outputFile));
			writer.write(null, new IIOImage(image, null, null), jpegParams);

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Got " + outputFile + " from " + imgURL);
	}
	
	public static void setSaveFolderName(String s) {
		saveFolderName = s;
	}
	
	private static String saveFolderName = ".";
	
	private static final List<Property> defaultProperties;
	static {
		defaultProperties = new ArrayList<Property>();
		defaultProperties.add(new Property("User-Agent", "Mozilla/5.0 Gecko/20100101 Firefox/44.0"));
		defaultProperties.add(new Property("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		defaultProperties.add(new Property("Accept-Language", "en-US,en;q=0.5"));
		defaultProperties.add(new Property("Accept-Encoding", "gzip"));
	}
}