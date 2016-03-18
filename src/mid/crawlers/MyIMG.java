package mid.crawlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mid.Property;
import mid.Requester;

public class MyIMG extends Crawler {
	public MyIMG() {
		requestPattern = "href=[\"\']http://myimg.club";
		imagePattern = "http://myimg.club.*[\"\' ]";
	}

	@Override
	public void continueCrawl(String hostURL) throws IOException {
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property("op", "view"));
		properties.add(new Property("id", hostURL.split("/")[3]));
		properties.add(new Property("pre", "1"));
		properties.add(new Property("next", "Continue+to+image..."));
		InputStream postInStr = Requester.executePostWithForm(hostURL, properties);

		String imgURL = processImageRequest(postInStr);

		InputStream imageInStr = executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}