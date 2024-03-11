package dev.nipafx.demo.modern.operations;

import com.sun.net.httpserver.SimpleFileServer;
import com.sun.net.httpserver.SimpleFileServer.OutputLevel;
import dev.nipafx.demo.modern.page.GitHubPage;
import dev.nipafx.demo.modern.page.Page;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.joining;

public class ResultServer {

	public static void serve(Page rootPage, Path serverDir) throws IOException {
		if (!Files.exists(serverDir))
			Files.createDirectory(serverDir);

		var html = Jsoup.parse("""
				<!DOCTYPE html>
				<html lang="en">
					<head>
						<meta charset="utf-8">
						<title>%s</title>
						<link rel="stylesheet" href="style.css">
					</head>
					<body>
						<div class="container">
							%s
						</div>
					</body>
				</html>
				""".formatted(Pretty.pageName(rootPage), pageTreeHtml(rootPage)));
		Files.writeString(serverDir.resolve("index.html"), html.html());

		launchWebServer(serverDir);
	}

	private static void launchWebServer(Path serverDir) {
		System.out.println("Visit localhost:8080");
		new Thread(() ->
				SimpleFileServer
						.createFileServer(
								new InetSocketAddress(8080),
								serverDir.toAbsolutePath(),
								OutputLevel.INFO)
						.start())
				.start();
	}

	private static String pageTreeHtml(Page rootPage) {
		var printedPages = new HashSet<Page>();
		return appendPageTreeHtml(printedPages, rootPage, 0);
	}

	private static String appendPageTreeHtml(Set<Page> printedPages, Page page, int level) {
		var pageHtml = pageHtml(page, printedPages.contains(page), level);
		if (printedPages.contains(page)) {
			printedPages.add(page);
			return pageHtml;
		} else {
			printedPages.add(page);
			var descendantsHtml = page instanceof GitHubPage ghPage
					? ghPage
							.links().stream()
							.map(linkedPage -> appendPageTreeHtml(printedPages, linkedPage, level + 1))
							.collect(joining("\n"))
					: "";
			return """
					%s
					%s
					""".formatted(pageHtml, descendantsHtml);
		}
	}

	private static String pageHtml(Page page, boolean reference, int level) {
		return """
				<div class="page level-%d">
					<a href="%s">%s</a>
					%s
				</div>
				""".formatted(
						level,
						page.url().toString(),
						Pretty.pageName(page),
						reference ? "<span class=\"ref\"></span>" : "");
	}

}
