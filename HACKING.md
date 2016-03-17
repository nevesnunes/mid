## Main

Contains the entrypoint. Establishes the initial request, delegating further processing to a list of **Crawlers**.

## Requester

Utility class with different types of requests to be sent, offering streams of the results.

Most requests are very basic, using predefined properties. If you want to use custom properties, you can call an overloaded method that receives that list of properties as an additional argument. Otherwise, call the corresponding single argument method.

Exception handling is entirely managed by the invokers of these utilities.

## Crawler

Deals with malformed or failed requests, opting to call additional **Requester** methods if necessary.

Subclasses define patterns to be matched in request processing. They also dictate any further extracting to be done until the image URL is available to download.
