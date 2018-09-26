package com.all41.sap.cloud.services.evatr.xmlrpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.TimeZone;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.xml.sax.SAXException;

public class EvatrConfig
{
  private static String SERVER_URL = "https://evatr.bff-online.de/";
  private static String METHOD_NAME = "evatrRPC";
  
  public static void main(String[] args)
  {
    try
    {
      evatrExecute("DE199226238", "FR82542065479", "PSA AUTOMOBILES SA", "POISSY", "78307", "", "nein");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static ResultData evatrExecute(String UstId_1, String UstId_2, String Firmenname, String Ort, String PLZ, String Strasse, String Druck)
    throws Exception
  {
    XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
    clientConfig.setServerURL(new URL(SERVER_URL));
    XmlRpcClient client = new XmlRpcClient();
    client.setConfig(clientConfig);
    Object[] params = { UstId_1, UstId_2, Firmenname, Ort, PLZ, Strasse, Druck };
    
    Object result = client.execute(METHOD_NAME, params);
    InputStream iStream = new ByteArrayInputStream(result.toString().getBytes(Charset.defaultCharset()));
    
    ResultData xmlResponse = parseResponseXml(iStream);
    System.out.println("Datum : " + xmlResponse.getDatum() + ", /nUstId_1 : " + xmlResponse.getUstId_1() + ", /nUstId_2 : " + xmlResponse.getUstId_2() + ",/nDruck : " + xmlResponse.getDruck() + ",/nErg_Name : " + xmlResponse.getErg_Name() + ",/nErg_Ort : " + xmlResponse.getErg_Ort() + ",/nErg_PLZ : " + xmlResponse.getErg_PLZ() + ",/nErg_Str : " + xmlResponse.getErg_Str() + ",/nErrorcode : " + xmlResponse.getErrorCode());
    return xmlResponse;
  }
  
  private static ResultData parseResponseXml(InputStream iStream)
    throws SAXException, IOException, ParserConfigurationException
  {
    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    SAXParser saxParser = saxParserFactory.newSAXParser();
    MyHandler handler = new MyHandler();
    saxParser.parse(iStream, handler);
    
    ResultData rd = handler.getResultData();
    
    return rd;
  }
  
  public TimeZone getTimeZone()
  {
    return null;
  }
  
  public boolean isEnabledForExtensions()
  {
    return false;
  }
}
