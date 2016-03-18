package mid.crawlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mid.Property;
import mid.Requester;

public class Imgyt extends Crawler {
	public Imgyt() {
		requestPattern = "href=[\"\']http://img.yt";
		imagePattern = "http://img.yt.*[\"\' ]";
	}

	@Override
	public void continueCrawl(String hostURL) throws IOException {
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property("imgContinue", "Continue+to+your+image"));
		InputStream postInStr = Requester.executePostWithForm(hostURL, properties);

		String imgURL = processImageRequest(postInStr);

		InputStream imageInStr = executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}