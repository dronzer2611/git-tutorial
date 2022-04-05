package com.bh.ecs.integration.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class CSVUtils {
	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';

	public static synchronized void writeToFile(OutputStream outputStream, List<String> csvCol,
			List<List<String>> csvData) {
		try(Writer writer =  new OutputStreamWriter(outputStream)) {
			CSVUtils.writeLine(writer, csvCol, ',', '"');
			for (List<String> row : csvData) {
				CSVUtils.writeLine(writer, row, ',', '"');
			}
		} catch (IOException e) {
			//do nothing
		}

	}
	
	public List<String[]> readCSV(InputStream inputStream) {
		List<String[]> file = new ArrayList<>();
		if (inputStream != null) {
			
			try (Scanner scanner = new Scanner(inputStream)){
				
				List<String> arr = null;
				while (scanner.hasNext()) {
					arr = null;
					arr = executeCSV(arr,scanner);
					if (arr != null && !arr.isEmpty()) {
						file.add(arr.toArray(new String[0]));
					}
				}
			} catch (Exception c) {
				//do nothing
			} 
		}
		return file;
	}
	
	public List<String> executeCSV(List<String> arr,Scanner scanner) {
		List<String> array = arr;
		try {
			array = parseLine(scanner.nextLine());
		} catch (Exception e) {
			// nothing
		}
		return array;
	}
	public static List<String> parseLine(String cvsLine) {
		return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators) {
		return parseLine(cvsLine, separators, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<>();

		// if empty, return!
		if (cvsLine == null || cvsLine.replace(',', ' ').trim().isEmpty()) {
			return result;
		}

		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder curVal = new StringBuilder();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars) {

			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {

					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}

				}
			} else {
				if (ch == customQuote) {

					inQuotes = true;

					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}

					// double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}

				} else if (ch == separators) {

					result.add(curVal.toString());

					curVal = new StringBuilder();
					startCollectChar = false;

				} else if (ch == '\r' || ch == '?') {
					// ignore LF characters
					continue;
				} else if (ch == '\n') {
					// the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}

		}

		result.add(curVal.toString());

		return result;
	}

	public static void writeLine(Writer w, List<String> values) throws IOException {
		writeLine(w, values, DEFAULT_SEPARATOR, ' ');
	}

	public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
		writeLine(w, values, separators, ' ');
	}

	// https://tools.ietf.org/html/rfc4180
	private static String followCVSformat(String value) {

		String result = value;
		if (result == null) {
			result = "";
		} else if (result.contains("\"")) {

			result = result.replace("\"", "\"\"");
		}
		return result;

	}

	public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

		boolean first = true;

		// default customQuote is empty

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (!first) {
				sb.append(separators);
			}
			if (customQuote == ' ') {
				sb.append(followCVSformat(value));
			} else {
				sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
			}

			first = false;
		}
		sb.append("\n");
		w.append(sb.toString());
	}

}
