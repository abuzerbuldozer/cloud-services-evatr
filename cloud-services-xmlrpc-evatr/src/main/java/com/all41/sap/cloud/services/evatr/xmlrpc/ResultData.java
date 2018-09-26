package com.all41.sap.cloud.services.evatr.xmlrpc;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResultData
{
  private String Datum;
  private String Uhrzeit;
  private String ErrorCode;
  private String UstId_1;
  private String UstId_2;
  private String Firmenname;
  private String Ort;
  private String PLZ;
  private String Strasse;
  private String Erg_Name;
  private String Erg_Ort;
  private String Erg_PLZ;
  private String Erg_Str;
  private String Gueltig_ab;
  private String Gueltig_bis;
  private String Druck;
  
  public String getDatum()
  {
    return this.Datum;
  }
  
  public void setDatum(String datum)
  {
    this.Datum = datum;
  }
  
  public String getUhrzeit()
  {
    return this.Uhrzeit;
  }
  
  public void setUhrzeit(String uhrzeit)
  {
    this.Uhrzeit = uhrzeit;
  }
  
  public String getErrorCode()
  {
    return this.ErrorCode;
  }
  
  public void setErrorCode(String errorCode)
  {
    this.ErrorCode = errorCode;
  }
  
  public String getUstId_1()
  {
    return this.UstId_1;
  }
  
  public void setUstId_1(String ustId_1)
  {
    this.UstId_1 = ustId_1;
  }
  
  public String getUstId_2()
  {
    return this.UstId_2;
  }
  
  public void setUstId_2(String ustId_2)
  {
    this.UstId_2 = ustId_2;
  }
  
  public String getFirmenname()
  {
    return this.Firmenname;
  }
  
  public void setFirmenname(String firmenname)
  {
    this.Firmenname = firmenname;
  }
  
  public String getOrt()
  {
    return this.Ort;
  }
  
  public void setOrt(String ort)
  {
    this.Ort = ort;
  }
  
  public String getPLZ()
  {
    return this.PLZ;
  }
  
  public void setPLZ(String pLZ)
  {
    this.PLZ = pLZ;
  }
  
  public String getStrasse()
  {
    return this.Strasse;
  }
  
  public void setStrasse(String strasse)
  {
    this.Strasse = strasse;
  }
  
  public String getErg_Name()
  {
    return this.Erg_Name;
  }
  
  public void setErg_Name(String erg_Name)
  {
    this.Erg_Name = erg_Name;
  }
  
  public String getErg_Ort()
  {
    return this.Erg_Ort;
  }
  
  public void setErg_Ort(String erg_Ort)
  {
    this.Erg_Ort = erg_Ort;
  }
  
  public String getErg_PLZ()
  {
    return this.Erg_PLZ;
  }
  
  public void setErg_PLZ(String erg_PLZ)
  {
    this.Erg_PLZ = erg_PLZ;
  }
  
  public String getErg_Str()
  {
    return this.Erg_Str;
  }
  
  public void setErg_Str(String erg_Str)
  {
    this.Erg_Str = erg_Str;
  }
  
  public String getGueltig_ab()
  {
    return this.Gueltig_ab;
  }
  
  public void setGueltig_ab(String gueltig_ab)
  {
    this.Gueltig_ab = gueltig_ab;
  }
  
  public String getGueltig_bis()
  {
    return this.Gueltig_bis;
  }
  
  public void setGueltig_bis(String gueltig_bis)
  {
    this.Gueltig_bis = gueltig_bis;
  }
  
  public String getDruck()
  {
    return this.Druck;
  }
  
  public void setDruck(String druck)
  {
    this.Druck = druck;
  }
  
  public ResultData(Map<String, String> responseXml)
    throws IllegalArgumentException, IllegalAccessException
  {
    Field[] declaredFields = getClass().getDeclaredFields();
    for (int i = 0; i < declaredFields.length; i++)
    {
      Field declaredField = declaredFields[i];
      String declaredFieldName = declaredField.getName();
      
      Object xmlValue = responseXml.get(declaredFieldName);
      if (xmlValue != null)
      {
        if (!declaredField.isAccessible()) {
          declaredField.setAccessible(true);
        }
        ConcurrentHashMap<String, String> concHashMap = new ConcurrentHashMap();
        concHashMap.put(declaredFieldName, xmlValue.toString());
        declaredField.set(this, xmlValue.toString());
        
        declaredField.setAccessible(false);
      }
    }
  }
}
