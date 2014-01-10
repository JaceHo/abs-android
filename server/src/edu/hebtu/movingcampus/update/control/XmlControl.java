package edu.hebtu.movingcampus.update.control;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlControl {

public static String getElements(String name, String filename)throws Exception {
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filename));
		Element root = doc.getDocumentElement();
		NodeList NElement = root.getElementsByTagName(name);
		String SElement = NElement.item(0).getFirstChild().getNodeValue();
		return SElement;

		// 判空,此处不需要
		// Node t = Nurl.item(0).getFirstChild();
		// if (t == null)
		// System.out.println("<url>包含的文本是空对象");

	}
	
	public static void changeElements(String name, String filename)throws Exception {

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filename));
		Element root = doc.getDocumentElement();
		NodeList NElement = root.getElementsByTagName(name);
		NElement.item(0).getFirstChild().setNodeValue("1");
		doc2XmlFile(doc, filename);
	}

	public static void doc2XmlFile(Document document, String filename)throws Exception {
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(filename));
		transformer.transform(source, result);
	}
}
