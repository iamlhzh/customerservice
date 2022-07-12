package com.lhzh.utils;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author 卢宏政
 * @date 2021/10/9 10:06
 */
public class XmlUtils {

    //获取xml
    public static String getElement(String xml, String key) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String encrypt = "";
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(xml);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);
            Element root = document.getDocumentElement();
            NodeList nodelist1 = root.getElementsByTagName(key);
            if (nodelist1 != null && nodelist1.getLength() > 0) {
                encrypt = nodelist1.item(0).getTextContent();
            }
        } catch (DOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            //  TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encrypt;
    }
}
