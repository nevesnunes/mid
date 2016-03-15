## Main

Contains the entrypoint. Establishes the initial request, delegating further processing to a list of crawlers.

## Requester

Functionality for request sending to a certain url, offering streams of the results. These utilities can be used by any module.

Most requests are very basic, using predefined properties. If you want to use custom properties, you can call an overloaded method that receives that list of properties as an additional argument. Otherwise, call the corresponding single argument method.

## Crawler

It is responsable for dealing with malformed or failed requests, opting to call additional Requester methods if necessary.

Subclasses define patterns to be matched in request processing. They also dictate any further extracting to be done until the relevant data is available to proceed with the image download.
