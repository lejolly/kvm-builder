package kvm;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import utils.FileUtils;
import utils.IPUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NetworkXML {

    protected static void writeNetworkXML(Path path, int nodeNumber, int vmsPerNode) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.newDocument();

            // root network element
            Element rootElement = document.createElement("network");
            document.appendChild(rootElement);

            // name
            Element nameElement = document.createElement("name");
            nameElement.appendChild(document.createTextNode("private"));
            rootElement.appendChild(nameElement);

            // bridge
            Element bridgeElement = document.createElement("bridge");
            Attr bridgeName = document.createAttribute("name");
            bridgeName.setValue("virbr100");
            bridgeElement.setAttributeNode(bridgeName);
            Attr bridgeStp = document.createAttribute("stp");
            bridgeStp.setValue("on");
            bridgeElement.setAttributeNode(bridgeStp);
            Attr bridgeDelay = document.createAttribute("delay");
            bridgeDelay.setValue("0");
            bridgeElement.setAttributeNode(bridgeDelay);
            rootElement.appendChild(bridgeElement);

            // forward
            Element forwardElement = document.createElement("forward");
            Attr forwardMode = document.createAttribute("mode");
            forwardMode.setValue("route");
            forwardElement.setAttributeNode(forwardMode);
            rootElement.appendChild(forwardElement);

            // ip
            Element ipElement = document.createElement("ip");
            Attr ipAddress = document.createAttribute("address");
            ipAddress.setValue(IPUtils.getVMGateway(nodeNumber));
            ipElement.setAttributeNode(ipAddress);
            Attr ipNetmask = document.createAttribute("netmask");
            ipNetmask.setValue(IPUtils.getVMSubnetMask());
            ipElement.setAttributeNode(ipNetmask);
            rootElement.appendChild(ipElement);

            // dhcp
            Element dhcpElement = document.createElement("dhcp");
            ipElement.appendChild(dhcpElement);

            Element rangeElement = document.createElement("range");
            Attr rangeStart = document.createAttribute("start");
            rangeStart.setValue(IPUtils.getVMStartIP(nodeNumber));
            rangeElement.setAttributeNode(rangeStart);
            Attr rangeEnd = document.createAttribute("end");
            rangeEnd.setValue(IPUtils.getVMEndIP(nodeNumber, vmsPerNode));
            rangeElement.setAttributeNode(rangeEnd);
            dhcpElement.appendChild(rangeElement);

            // output
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(document);

            // file
            Path fileName = Paths.get(path.toString() + "/xml-node" + nodeNumber + "-network.xml");
            FileUtils.createFile(fileName);
            StreamResult fileResult = new StreamResult(new File(fileName.toUri()));
            transformer.transform(source, fileResult);

            // console
//            StreamResult consoleResult = new StreamResult(System.out);
//            transformer.transform(source, consoleResult);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

}
