package mid.crawlers;

import java.io.IOException;
import java.io.InputStream;
import mid.Requester;

public class ImageBam extends Crawler {
	public ImageBam() {
		requestPattern = "href=[\"\']http://www.imagebam";
		imagePattern = "http://.*imagebam\\.com.*[\"\' ]";
	}

	@Override
	public void continueCrawl(String hostURL) throws IOException {
		InputStream postInStr = executeGet(hostURL);

		String imgURL = processImageRequest(postInStr);

		InputStream imageInStr = executeGet(imgURL);
		Requester.downloadImage(imgURL, imageInStr);
	}
}