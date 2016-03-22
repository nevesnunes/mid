mid
===

mid (Multiple Image Downloader) extracts the source of a given URL and downloads images from links.

## Supported Hosts

* chronos.to
* imagebam.com
* imgmaid.net
* imgsen.se
* imgspot.org
* img.yt
* myimg.club

## Compiling & Running

```
javac -cp "jars/*" src/mid/*.java src/mid/crawlers/*.java -d bin/
java -cp "jars/*:bin/" mid.Main
```
