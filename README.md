mid
===

mid (Multiple Image Downloader) extracts the source of a given URL and downloads images from links.

## Supported Hosts

* Chronos.to
* ImageBam.com
* ImgSpot.org
* Img.yt

## Compiling & Running

```
javac -cp "jars/*" src/mid/*.java src/mid/crawlers/*.java -d bin/
java -cp "jars/*:bin/" mid.Main
```
