package net.seeseekey.convert2markdown.converter;

import com.rometools.rome.io.FeedException;
import net.seeseekey.convert2markdown.options.FileScheme;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Set;

public interface Converter {

    boolean canBeProcessed(String input);

    Set<FileScheme> getSupportedSchemes();

    ConverterResult convert(String input, String filterByAuthor) throws IOException, FeedException, XMLStreamException;
}
