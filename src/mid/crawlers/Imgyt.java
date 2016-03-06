package mid.crawlers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mid.Property;
import mid.Requester;

public class Imgyt extends Crawler {
	public Imgyt() {
		requestPattern = "href=\"http://img.yt";
		imagePattern = "href=\'http://img.yt";
		imageSplitPattern = "\'";
		imageSplitIndex = 1;
	}

	@Override
	public void continueCrawl(String hostURL) {
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property("imgContinue", "Continue+to+your+image"));
		InputStream postInStr = Requester.executePostWithForm(hostURL, properties);

		String imgURL = processImageRequest(postInStr);

		InputStream imageInStr = Requester.executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}