package dev.nipafx.demo.modern.operations;

import dev.nipafx.demo.modern.page.GitHubPage;
import dev.nipafx.demo.modern.page.Page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.joining;

public class ResultServer {

	public static void serve(Page rootPage, Path serverDir) throws IOException {
		if (!Files.exists(serverDir))
			Files.createDirectory(serverDir);

		// TODO: parse to HTML document
		var html = """
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
				""".formatted(Pretty.pageName(rootPage), pageTreeHtml(rootPage));
		Files.writeString(serverDir.resolve("index.html"), html);
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
		// TODO: add page info
		return """
				<div class="page level-0">
					<a href=""></a>
				</div>
				""";
	}

}
