package mid.crawlers;

import java.io.InputStream;

import mid.Requester;

public class ImageBam extends Crawler {
	public ImageBam() {
		requestPattern = "href=\"http://www.imagebam";
		imagePattern = "http://.*imagebam\\.com.*jpe*g";
		imageSplitPattern = "\"";
		imageSplitIndex = 0;
	}

	@Override
	public void continueCrawl(String hostURL) {
		InputStream postInStr = Requester.executeGet(hostURL);

		String imgURL = processImageRequestRegex(postInStr);

		InputStream imageInStr = Requester.executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}