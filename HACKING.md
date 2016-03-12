Main
The entrypoint. Establishes the initial request, delegating further processing to all available crawlers.

Requester
Contains all functionality for sending requests to a certain url, offering streams of the results. These utilities can be used by any module.

Crawler
Processes requests. Subclasses dictate any further extracting to be done until the relevant data can be downloaded.
