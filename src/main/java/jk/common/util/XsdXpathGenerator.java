package jk.common.util;

/**
 * Created by jaskaransingh on 12/7/18.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by jaskaransingh on 11/7/18.
 */
public class XsdXpathGenerator {


  /**
   *
   * @param file Path of XSD file
   * @return Map of xpath as key and type as value
   */
  private static Map<String,String> getAllXpaths(File file) {
    Map<String,String> result = new HashMap<String, String>();
    try {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      Document document = docBuilder.parse (file);
      NodeList elementList = document.getElementsByTagName("xs:element");
      NodeList attributeList = document.getElementsByTagName("xs:attribute");
      for (int i = 0 ; i < attributeList.getLength(); i++) {
        Element element = (Element)attributeList.item(i);
        if(element.hasAttributes() && element.getAttribute("type") != "") {
          result.put(element.getAttribute("name"),extractType(element.getAttribute("type")));
        }
      }
      for(int i = 0 ; i < elementList.getLength(); i++) {
        Element element = (Element)elementList.item(i);
        if(element.hasAttributes() && element.getAttribute("type") != "") {
          result.put(xpathString(element,new StringBuilder(element.getAttribute("name"))).toString() ,extractType(element.getAttribute("type")));
        }
      }
    }
    catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    catch (SAXException e) {
      e.printStackTrace();
    }
    catch (IOException ed) {
      ed.printStackTrace();
    }
    return result;
  }


  /**
   *
   * @param args Input file path of xsd
   */
  public static void main(String args[]) {
    Map<String,String>  xpathWithType = getAllXpaths(new File(args[0]));
    for (Map.Entry<String,String> entry : xpathWithType.entrySet()) {
      System.out.println(entry.getKey() +" -->  " + entry.getValue());
    }
  }

  /**
   *
   * @param element Element whose parent node is to be found
   * @param xpath Current xpath
   * @return Complete xpath
   */
  private static StringBuilder xpathString(Element element,StringBuilder xpath) {
    Element parent = getParentElement(element);
    if(parent != null){
      if(parent.hasAttributes() && parent.getAttribute("name") != ""){
        xpath = new StringBuilder(parent.getAttribute("name")).append("/").append(xpath);
        return xpathString(parent,xpath);
      }else {
        return xpath;
      }
    }else {
      return xpath;
    }
  }

  /**
   *
   * @param element Element of type xs:element
   * @return Parent element
   */
  private static Element getParentElement(Element element) {
    Node parentNode = element.getParentNode();
    if (parentNode != null && parentNode instanceof Element) {
      String nodeName = parentNode.getNodeName();
      if (nodeName.equalsIgnoreCase("xs:element")) {
        return (Element) element.getParentNode();
      } else {
        return getParentElement((Element) parentNode);
      }
    } else {
      return null;
    }
  }

  /**
   *
   * @param type String type in xsd
   * @return scrapped type
   */
  private static String extractType(String type) {
    if(type.startsWith("xs:")){
      return type.replace("xs:","");
    }else {
      return type;
    }
  }



}

