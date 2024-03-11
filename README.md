# Modern Java in Action

A repository for my live-coding talk [Modern Java in Action](https://nipafx.dev/talk-java-action).

## Next

Simple server:
* `ResultServer::launchWebServer`:
	```java
	System.out.println("Visit localhost:8080");
	SimpleFileServer
			.createFileServer(
					new InetSocketAddress(8080),
					serverDir.toAbsolutePath(),
					OutputLevel.INFO)
			.start();
	```
