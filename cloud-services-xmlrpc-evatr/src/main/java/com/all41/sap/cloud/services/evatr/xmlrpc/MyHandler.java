package com.all41.sap.cloud.services.evatr.xmlrpc;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MyHandler
  extends DefaultHandler
{
  private ResultData resultData = null;
  Map<String, String> urlParams = new HashMap();
  String editingFieldName;
  boolean isEditingStarted = false;
  
  public void startElement(String uri, String localName, String qName, Attributes attributes)
    throws SAXException
  {
    if (qName.equalsIgnoreCase("params"))
    {
      this.urlParams.put("Datum", "");
      this.urlParams.put("Uhrzeit", "");
      this.urlParams.put("ErrorCode", "");
      this.urlParams.put("UstId_1", "");
      this.urlParams.put("UstId_2", "");
      this.urlParams.put("Firmenname", "");
      this.urlParams.put("Ort", "");
      this.urlParams.put("PLZ", "");
      this.urlParams.put("Strasse", "");
      this.urlParams.put("Erg_Name", "");
      this.urlParams.put("Erg_Ort", "");
      this.urlParams.put("Erg_PLZ", "");
      this.urlParams.put("Erg_Str", "");
      this.urlParams.put("Gueltig_ab", "");
      this.urlParams.put("Gueltig_bis", "");
      this.urlParams.put("Druck", "");
    }
  }
  
  public void endElement(String uri, String localName, String qName)
    throws SAXException
  {
    if (qName.equalsIgnoreCase("params")) {
      try
      {
        this.resultData = new ResultData(this.urlParams);
      }
      catch (IllegalArgumentException e)
      {
        e.printStackTrace();
      }
      catch (IllegalAccessException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public void characters(char[] ch, int start, int length)
    throws SAXException
  {
    String value = new String(ch, start, length);
    if ((!this.isEditingStarted) && (this.urlParams.containsKey(value)))
    {
      this.editingFieldName = value;
      this.isEditingStarted = true;
    }
    else if ((this.isEditingStarted) && (this.urlParams.containsKey(this.editingFieldName)) && (value.trim().length() > 0))
    {
      this.urlParams.put(this.editingFieldName, value);
      this.editingFieldName = "";
      this.isEditingStarted = false;
    }
  }
  
  public ResultData getResultData()
  {
    return this.resultData;
  }
  
  public void setResultData(ResultData resultData)
  {
    this.resultData = resultData;
  }
}
