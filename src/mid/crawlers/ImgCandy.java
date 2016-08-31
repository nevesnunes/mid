package mid.crawlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mid.Property;
import mid.Requester;

public class ImgCandy extends Crawler {
	public ImgCandy() {
		requestPattern = "href=[\"\']http://imgcandy.net";
		imagePattern = "http://imgcandy.net.*[\"\' ]";
	}

	@Override
	public void continueCrawl(String hostURL) throws IOException {
		List<Property> properties = new ArrayList<Property>();
		properties.add(new Property("imgContinue", "Continue+to+image..."));
		InputStream postInStr = Requester.executePostWithForm(hostURL, properties);

		String imgURL = processImageRequest(postInStr);

		String[] URLparts = imgURL.split("/");
		imgURL = "http://" + URLparts[2] +
				"/" + URLparts[3] + "/" + URLparts[4] + "/" + URLparts[5] +
				"/" + URLparts[6] + "/" + URLparts[7] + "/" + URLparts[8];

		InputStream imageInStr = executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}