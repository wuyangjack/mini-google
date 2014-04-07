# MiniGoogle

## Authors
* 	Chenyang Yu 	(yu12@seas.upenn.edu)
* 	Xuan Zheng 		(zxuan@seas.upenn.edu)
* 	Sitong Zhou 	(sitong@seas.upenn.edu)
* 	Yang Wu 		(yangwu6@seas.upenn.edu)

## Milestones
*	MS1 (Apr 7, Monday): 

	Crawler crawls some webpages for testing. 

	Initial design for other modules (Indexer, Lexicon, PageRank).

* 	MS2 (Apr 10, Thursday):

	Hadoop understood.

## Modules
#### Crawler (Chenyang)
* 	Support chunk encoding.
* 	Database: url(key), timestamp, HTML.
*	WebCrawler.java /database 50 100(optional)
	Hardcode starting address in WebCrawler.java : initUrls()

#### Inverted index / Forward index (Chenyang / Sitong)
*	Inverted index: input word, output docs and positions

#### IDF / IF (Xuan)
* 	Dependent on inverted index / forward index (discuss with Chenyang)

#### PageRank (Yang)