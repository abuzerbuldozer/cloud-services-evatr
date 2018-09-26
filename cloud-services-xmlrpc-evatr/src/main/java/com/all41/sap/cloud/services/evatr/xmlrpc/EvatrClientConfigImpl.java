package com.all41.sap.cloud.services.evatr.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class EvatrClientConfigImpl
  extends XmlRpcClientConfigImpl
{
  private static String SERVER_URL = "http://evatr.bff-online.de/evatrRPC?Firmenname=PSA AUTOMOBILES SA&Ort=POISSY&PLZ=78307&UstId_1=DE199226238&UstId_2=FR82542065479";
  private static String METHOD_NAME = "evatrRPC";
  
  public EvatrClientConfigImpl(String urlParams)
    throws MalformedURLException
  {
    URL pURL = new URL(SERVER_URL);
    setServerURL(pURL);
  }
  
  public static void main(String[] args) {}
}
