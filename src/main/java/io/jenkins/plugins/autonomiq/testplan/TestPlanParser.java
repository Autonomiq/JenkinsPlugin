package io.jenkins.plugins.autonomiq.testplan;

import io.jenkins.plugins.autonomiq.PluginException;
import io.jenkins.plugins.autonomiq.util.TimeStampedLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class TestPlanParser {


    public class Variable {
        private String name;
        private String value;

        public Variable(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Variable{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


    private Document doc;
    private TimeStampedLogger log;

    public TestPlanParser(InputStream is, TimeStampedLogger log) throws PluginException {

        this.log = log;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
            	factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
            	factory.setFeature("http://xml.org/sax/features/external-general-entities",false);
            	factory.setFeature("http://xml.org/sax/features/external-parameter-entities",false);
            } catch(ParserConfigurationException exp){
                exp.printStackTrace();	
            }
            DocumentBuilder dBuilder = factory.newDocumentBuilder();

            doc = dBuilder.parse(new InputSource(is));
        } catch (Exception e) {

            throw new PluginException("Exception parsing xml file", e);
        }

    }

    public TestPlan parseTestSequence() throws PluginException {

        TestPlan ts;

        try {

            doc.getDocumentElement().normalize();

            //log.println("Root element: " + doc.getDocumentElement().getNodeName());

            NodeList initList = doc.getElementsByTagName("InitializeVariables");

            //log.println("len " + initList.getLength());

            List<Variable> initialVars = new LinkedList<>();

            if (initList.getLength() > 0) {
                Node nNode = initList.item(0);
                NodeList initialSetters = nNode.getChildNodes();

                getInitialVars(initialSetters, initialVars);
            }


            NodeList testList = doc.getElementsByTagName("TestSequenceItem");
            List<TestItem> testItems = new LinkedList<>();

            getTestItems(testList, testItems);

            ts = new TestPlan(initialVars, testItems);


        } catch (Exception e) {
            throw new PluginException("Exception parsing test sequence", e);
        }

        return ts;
    }

    public void dumpTest(TestPlan ts) {

        log.println("Initial vars:");
        for (Variable v : ts.getInitialVars()) {
            log.println("  " + v.toString());
        }

        log.println("Test cases:");
        for (TestItem i : ts.getSeq()) {
            log.println("Case: " + i.getCaseName());
            log.println("  Case initial vars:");
            for (Variable v : i.getSetVars()) {
                log.println("    " + v.toString());
            }
            log.println("  Case show vars:");
            for (Variable v : i.getShowVars()) {
                log.println("    " + v.toString());
            }
            log.println("  Case validate vars:");
            for (Variable v : i.getValidateVars()) {
                log.println("    " + v.toString());
            }
        }
    }

    private void getTestItems(NodeList testList, List<TestItem> testItems) {

        for (int i = 0; i < testList.getLength(); i++) {

            Node testNode = testList.item(i);

            if (testNode.getNodeType() == Node.ELEMENT_NODE) {

                NodeList elemList = testNode.getChildNodes();

                List<Variable> setVars = new LinkedList<>();
                String caseName = null;
                List<Variable> showVars = new LinkedList<>();
                List<Variable> validateVars = new LinkedList<>();

                for (int e = 0; e < elemList.getLength(); e++) {
                    Node elemNode = elemList.item(e);

                    if (elemNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element elem = (Element) elemNode;

                        switch (elemNode.getNodeName()) {
                            case "SetVariable":
                                setVars.add(getVar(elem));
                                break;
                            case "RunTestCase":
                                caseName = elem.getTextContent();
                                break;
                            case "ShowVariable":
                                showVars.add(getShowVar(elem));
                                break;
                            case "ValidateVariable":
                                validateVars.add(getVar(elem));
                                break;
                        }

                        //log.println("node " + elemNode.getNodeName());
                    }
                }

                if (caseName != null) {

                    TestItem item = new TestItem(setVars, caseName, showVars, validateVars);
                    testItems.add(item);
                }
            }
        }
    }

    private void getInitialVars(NodeList initialSetters, List<Variable> vars) {

        for (int i = 0; i < initialSetters.getLength(); i++) {

            Node setterNode = initialSetters.item(i);
            if (setterNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) setterNode;
                vars.add(getVar(elem));
            }
        }

    }

    private Variable getShowVar(Element elem) {
        String name = elem.getAttribute("name");
        return new Variable(name, null);
    }

    private Variable getVar(Element elem) {
        String name = elem.getAttribute("name");
        String value = elem.getTextContent();
        return new Variable(name, value);
    }


}
