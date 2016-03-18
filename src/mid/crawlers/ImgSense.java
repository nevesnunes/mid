package mid.crawlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mid.Property;
import mid.Requester;

public class ImgSense extends Crawler {
	public ImgSense() {
		requestPattern = "href=[\"\']http://imgsen.se";
		imagePattern = "http://imgsen.se.*[\"\' ]";
	}

	@Override
	public void continueCrawl(String hostURL) throws IOException {
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property("imgContinue", "Continue+to+image..."));
		InputStream postInStr = Requester.executePostWithForm(hostURL, properties);

		String imgURL = processImageRequest(postInStr);

		InputStream imageInStr = executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}