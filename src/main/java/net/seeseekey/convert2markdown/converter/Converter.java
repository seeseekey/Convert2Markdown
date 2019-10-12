package net.seeseekey.convert2markdown.converter;

import com.rometools.rome.io.FeedException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public interface Converter {

    boolean canProcessed(String input);

    ConverterResult convert(String input, String filterByAuthor) throws IOException, FeedException, XMLStreamException;
}
