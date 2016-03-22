package mid.crawlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import mid.Property;
import mid.Requester;

public class IMGMaid extends Crawler {
	public IMGMaid() {
		requestPattern = "href=[\"\']http://imgmaid.net";
		imagePattern = "http://imgmaid.net/i/.*[\"\' ]";
	}

	@Override
	public void continueCrawl(String hostURL) throws ClientProtocolException, IOException {
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property("op", "view"));
		properties.add(new Property("id", hostURL.split("/")[3]));
		properties.add(new Property("pre", "30"));
		properties.add(new Property("next", "Continue+to+image..."));
		InputStream postInStr = Requester.executePostWithForm(hostURL, properties);

		String imgURL = processImageRequest(postInStr);
		String[] URLparts = imgURL.split("/");
		imgURL = "http://i" + URLparts[4] + "." + URLparts[2] +
				"/i/" + URLparts[5] + "/" + URLparts[6] + ".jpg";

		InputStream imageInStr = executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}