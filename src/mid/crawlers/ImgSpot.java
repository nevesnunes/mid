package mid.crawlers;

import java.io.InputStream;

import mid.Requester;

public class ImgSpot extends Crawler {
	public ImgSpot() {
		requestPattern = "href=\"http://imgspot.org";
		imagePattern = "src=\'/upload";
		imageSplitPattern = "\'";
		imageSplitIndex = 1;
	}

	@Override
	public void continueCrawl(String hostURL) {
		InputStream postInStr = Requester.executeGet(hostURL);

		String imgURL = processImageRequest(postInStr);
		imgURL = "http://imgspot.org" + imgURL;		
		
		InputStream imageInStr = executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}