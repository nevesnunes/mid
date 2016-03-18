package mid.crawlers;

import java.io.IOException;
import java.io.InputStream;

import mid.Requester;

public class ImgSpot extends Crawler {
	public ImgSpot() {
		requestPattern = "href=[\"\']http://imgspot.org";
		imagePattern = "/upload.*[\"\' ]";
	}

	@Override
	public void continueCrawl(String hostURL) throws IOException {
		InputStream postInStr = executeGet(hostURL);

		// Sometimes the source doesn't contain the host.
		// Therefore, just match part of the URL, then prepend with the host.
		String imgURL = processImageRequest(postInStr);
		imgURL = "http://imgspot.org" + imgURL;		
		
		InputStream imageInStr = executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}