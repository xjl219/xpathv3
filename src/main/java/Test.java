import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.tidy5.Tidy;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

public class Test {

	public static void main(String[] args) {
		  try {
	           XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
	           InputSource source= new InputSource();
	           try {
				xmlReader.parse(new InputSource(new FileReader("/Users/xujl-mac/IdeaProjects/tt/src/main/resources/simple.xml")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  } catch (ParserConfigurationException err) {
				err.printStackTrace();
	        } catch (SAXException err) {
	        	err.printStackTrace();
	        }
		System.out.println(System.getProperty("javax.xml.parsers.SAXParserFactory"));
		System.out.println(System.getProperty("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"));
	}

}
