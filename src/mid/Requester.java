package mid;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class Requester {
	public static InputStream executeGet(String targetURL) 
			throws IOException {
		return executeGet(targetURL, defaultProperties);
	}

	public static InputStream executeGet(String targetURL, List<Property> properties) 
			throws IOException {
		URLConnection conn = makeConnection(targetURL, properties);
		
		int status = ((HttpURLConnection) conn).getResponseCode();
		if ((status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER)) {
			System.out.println("Got status " + status);

			// Get redirect url from "location" header field
			String newURL = conn.getHeaderField("Location");
			System.out.println("Redirect to URL: " + newURL);

			// Get the cookie if needed, for login
			String cookies = conn.getHeaderField("Set-Cookie");

			// Open the new connnection again
			conn = makeConnection(newURL, properties);
			conn.setRequestProperty("Cookie", cookies);
		}

		// Stream wrapper is based on encoding type
		String encoding = conn.getContentEncoding();
		if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
			return new GZIPInputStream(conn.getInputStream());
		} else {
			return conn.getInputStream();
		}
	}

	private static URLConnection makeConnection(String targetURL, List<Property> properties) 
			throws IOException {
		// Assume http if no protocol given		
		int httpIndex = targetURL.indexOf("http://");
		int httpsIndex = targetURL.indexOf("https://");
		if ((httpIndex == -1) && (httpsIndex == -1)) {
			targetURL = "http://" + targetURL;
		}
		
		URL url = new URL(targetURL);
		URLConnection conn;
		HttpURLConnection.setFollowRedirects(true);
		if (httpIndex != -1) {
			conn = (HttpURLConnection) url.openConnection();
		} else {		
			conn = (HttpsURLConnection) url.openConnection();
		}

		conn.setReadTimeout(5000);
		for (Property p : properties)
			conn.setRequestProperty(p.key, p.value);

		return conn;
	}

	public static InputStream executePostWithForm(String targetURL, List<Property> properties)
			throws ClientProtocolException, IOException {
		// Create SSL Client
		SSLContext sslcontext = SSLContexts.createSystemDefault();
		SSLConnectionSocketFactory sslConnectionSocketFactory = 
			new SSLConnectionSocketFactory(
					sslcontext,
					new String[] { "TLSv1", "SSLv3" },
					null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());		
		Registry<ConnectionSocketFactory> socketFactoryRegistry = 
			RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", sslConnectionSocketFactory)
				.build();
		PoolingHttpClientConnectionManager cm = 
			new PoolingHttpClientConnectionManager(socketFactoryRegistry);		
		CloseableHttpClient httpClient = HttpClients.custom()
			.setSSLSocketFactory(sslConnectionSocketFactory)
			.setConnectionManager(cm)
			.build();

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (Property p : properties)
			builder.addTextBody(p.key, p.value, ContentType.TEXT_PLAIN);
		HttpPost uploadFile = new HttpPost(targetURL);
		uploadFile.setEntity(builder.build());

		// Send POST
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		
		int status = response.getStatusLine().getStatusCode();
		if ((status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER)) {
			System.out.println("Got status " + status);

			// Get redirect url from "location" header field
			String newURL = response.getFirstHeader("Location").getValue();
			System.out.println("Redirect to URL: " + newURL);

			// Send the new POST
			uploadFile = new HttpPost(newURL);
			uploadFile.setEntity(builder.build());
			response = httpClient.execute(uploadFile);
		}

		// Get response
		return response.getEntity().getContent();
	}

	public static void downloadImage(String imgURL) 
			throws IOException {
		downloadImage(imgURL, (new URL(imgURL)).openStream());
	}

	public static void downloadImage(String imgURL, InputStream postInStr) 
			throws IOException {
		BufferedImage image = ImageIO.read(postInStr);

		String[] URLparts = imgURL.split("/");
		File outputFile = new File(saveFolderName + "/" + URLparts[URLparts.length - 1]);

		JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpegParams.setCompressionQuality(1f);

		final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
		writer.setOutput(new FileImageOutputStream(outputFile));
		writer.write(null, new IIOImage(image, null, null), jpegParams);

		System.out.println("Got " + outputFile + " from " + imgURL);
	}

	public static void setSaveFolderName(String s) {
		saveFolderName = s;
	}

	private static String saveFolderName = ".";

	private static final List<Property> defaultProperties;
	static {
		defaultProperties = new ArrayList<Property>(Arrays.asList(
				new Property("User-Agent", "Mozilla/5.0 Gecko/20100101 Firefox/44.0"),
				new Property("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
				new Property("Accept-Language", "en-US,en;q=0.5"),
				new Property("Accept-Encoding", "gzip")));
	}
}