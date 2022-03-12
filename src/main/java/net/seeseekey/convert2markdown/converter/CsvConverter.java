package net.seeseekey.convert2markdown.converter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import net.seeseekey.convert2markdown.options.FileScheme;
import net.seeseekey.convert2markdown.utils.Logging;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CsvConverter implements Converter {

    private static final Logger log = Logging.getLogger();

    @Override
    public boolean canProcessed(String input) {

        return input.endsWith(".csv");
    }

    @Override
    public Set<FileScheme> getSupportedSchemes() {

        return Set.of(FileScheme.SINGLE);
    }

    @Override
    public ConverterResult convert(String input, String filterByAuthor) throws IOException {

        // Read CSV
        List<String[]> columns;

        try (CSVReader reader = new CSVReader(new FileReader(input))) {
            columns = reader.readAll();
        } catch (CsvException e) {
            log.error("Can't read CSV file.", e);
            return null;
        }

        // Export to tables
        boolean header = true;

        List<String> markdownTable = new ArrayList<>();
        Map<Integer, Integer> rowToSize = getSizeOfRows(columns);

        // Transform table to Markdown
        for (String[] rows : columns) {

            StringBuilder assembledTableLine = new StringBuilder("| ");
            StringBuilder assembledTableLineHeader = new StringBuilder("| ");

            for (int i = 0; i < rows.length; i++) {

                String text = rows[i];
                int rowLength = rowToSize.get(i);

                assembledTableLine.append(text);
                assembledTableLine.append(" ".repeat(rowLength - text.length()));
                assembledTableLine.append(" | ");

                if (header) {
                    assembledTableLineHeader.append("-".repeat(rowLength));
                    assembledTableLineHeader.append(" | ");
                }
            }

            markdownTable.add(assembledTableLine.toString().strip());

            if (header) {
                markdownTable.add(assembledTableLineHeader.toString().strip());
                header = false;
            }
        }

        // Build content
        StringBuilder content = new StringBuilder();

        for (String line : markdownTable) {
            content.append(line);
            content.append("\n");
        }

        // Return result
        List<ConverterResultEntry> entries = new ArrayList<>();
        ConverterResultEntry entry = new ConverterResultEntry(content.toString());
        entries.add(entry);

        return new ConverterResult(entries, 0, 0, 1);
    }

    private Map<Integer, Integer> getSizeOfRows(List<String[]> columns) {

        Map<Integer, Integer> rowToSize = new HashMap<>();

        // Get length of rows
        for (String[] rows : columns) {

            for (int i = 0; i < rows.length; i++) {

                String row = rows[i];

                int currentRowLength = 0;

                if (rowToSize.containsKey(i)) {
                    currentRowLength = rowToSize.get(i);
                }

                if (row.length() > currentRowLength) {
                    rowToSize.put(i, row.length());
                }
            }
        }

        return rowToSize;
    }
}