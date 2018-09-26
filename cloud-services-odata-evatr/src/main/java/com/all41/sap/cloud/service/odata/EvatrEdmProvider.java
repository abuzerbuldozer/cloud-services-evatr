/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.all41.sap.cloud.service.odata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

/**
 * this class is supposed to declare the metadata of the OData service
 * it is invoked by the Olingo framework e.g. when the metadata document of the service is invoked
 * e.g. http://localhost:8080/ExampleService1/ExampleService1.svc/$metadata
 */
public class EvatrEdmProvider extends CsdlAbstractEdmProvider {

  // Service Namespace
  public static final String NAMESPACE = "OData.All41";

  // EDM Container
  public static final String CONTAINER_NAME = "Container";
  public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

  // Entity Types Names
  public static final String ET_RESPONSE_NAME = "Response";
  public static final FullQualifiedName ET_RESPONSE_FQN = new FullQualifiedName(NAMESPACE, ET_RESPONSE_NAME);

  // Entity Set Names
  public static final String ES_RESPONSES_NAME = "Responses";
  
//Function
public static final String FUNCTION_EVATR_VATID_VERIFICATION_CALL = "EvatrVatIDVerificationCall";
public static final FullQualifiedName FUNCTION_EVATR_VATID_VERIFICATION_CALL_FQN = new FullQualifiedName(NAMESPACE, FUNCTION_EVATR_VATID_VERIFICATION_CALL);

//Function/Action Parameters
public static final String PARAMETER_USTID_1 = "UstId_1";
public static final String PARAMETER_USTID_2 = "UstId_2";
public static final String PARAMETER_FIRMENNAME = "Firmenname";
public static final String PARAMETER_ORT = "Ort";
public static final String PARAMETER_PLZ = "PLZ";
public static final String PARAMETER_STRASSE = "Strasse";
public static final String PARAMETER_DRUCK = "Druck";



  @Override
  public List<CsdlSchema> getSchemas() {

    // create Schema
    CsdlSchema schema = new CsdlSchema();
    schema.setNamespace(NAMESPACE);

    // add EntityTypes
    List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
    entityTypes.add(getEntityType(ET_RESPONSE_FQN));
    schema.setEntityTypes(entityTypes);

    // add EntityContainer
    schema.setEntityContainer(getEntityContainer());

	List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
	functions.addAll(getFunctions(FUNCTION_EVATR_VATID_VERIFICATION_CALL_FQN));
	schema.setFunctions(functions);
    
    // finally
    List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
    schemas.add(schema);

    return schemas;
  }


  @Override
  public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {

    // this method is called for one of the EntityTypes that are configured in the Schema
    if(entityTypeName.equals(ET_RESPONSE_FQN)){

      //create EntityType properties
      /*CsdlProperty id = new CsdlProperty().setName("UstId_1").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty  description = new CsdlProperty().setName("Description").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      */
	    CsdlProperty  datum 	 = new CsdlProperty().setName("Datum").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  uhrzeit 	 = new CsdlProperty().setName("Uhrzeit").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  errorCode  = new CsdlProperty().setName("ErrorCode").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  ustId1	 = new CsdlProperty().setName("UstId_1").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  ustId2	 = new CsdlProperty().setName("UstId_2").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  firmenname = new CsdlProperty().setName("Firmenname").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  ort 		 = new CsdlProperty().setName("Ort").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  plz 		 = new CsdlProperty().setName("PLZ").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  strasse 	 = new CsdlProperty().setName("Strasse").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  ergname 	 = new CsdlProperty().setName("Erg_Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  ergort 	 = new CsdlProperty().setName("Erg_Ort").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  ergplz 	 = new CsdlProperty().setName("Erg_PLZ").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  ergstr 	 = new CsdlProperty().setName("Erg_Str").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  gueltigAb  = new CsdlProperty().setName("Gueltig_ab").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  gueltigBis = new CsdlProperty().setName("Gueltig_bis").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	    CsdlProperty  druck 	 = new CsdlProperty().setName("Druck").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());    	

      // create CsdlPropertyRef for Key element
      CsdlPropertyRef propertyRef = new CsdlPropertyRef();
      propertyRef.setName("UstId_1");

      // configure EntityType
      CsdlEntityType entityType = new CsdlEntityType();
      entityType.setName(ET_RESPONSE_NAME);
//      entityType.setProperties(Arrays.asList(id, name , description));
	    entityType.setProperties(Arrays.asList( datum, uhrzeit, errorCode, ustId1, ustId2, firmenname, ort, plz, strasse, ergname, ergort, ergplz, ergstr, gueltigAb, gueltigBis, druck ));

      entityType.setKey(Collections.singletonList(propertyRef));

      return entityType;
    }

    return null;
  }

  @Override
  public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

    if(entityContainer.equals(CONTAINER)){
      if(entitySetName.equals(ES_RESPONSES_NAME)){
        CsdlEntitySet entitySet = new CsdlEntitySet();
        entitySet.setName(ES_RESPONSES_NAME);
        entitySet.setType(ET_RESPONSE_FQN);

        return entitySet;
      }
    }

    return null;
  }

  @Override
  public CsdlEntityContainer getEntityContainer() {

    // create EntitySets
    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
    entitySets.add(getEntitySet(CONTAINER, ES_RESPONSES_NAME));

    // create EntityContainer
    CsdlEntityContainer entityContainer = new CsdlEntityContainer();
    entityContainer.setName(CONTAINER_NAME);
    entityContainer.setEntitySets(entitySets);

  //Create function imports
    List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();
    functionImports.add(getFunctionImport(CONTAINER, FUNCTION_EVATR_VATID_VERIFICATION_CALL));

    entityContainer.setFunctionImports(functionImports);
    return entityContainer;
  }

  @Override
  public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {

    // This method is invoked when displaying the service document at e.g. http://localhost:8080/DemoService/DemoService.svc
    if(entityContainerName == null || entityContainerName.equals(CONTAINER)){
      CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
      entityContainerInfo.setContainerName(CONTAINER);
      return entityContainerInfo;
    }

    return null;
  }
  
  @Override
  public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) {
    if (functionName.equals(FUNCTION_EVATR_VATID_VERIFICATION_CALL_FQN)) {
      // It is allowed to overload functions, so we have to provide a list of functions for each function name
      final List<CsdlFunction> functions = new ArrayList<CsdlFunction>();

      // Create the parameter for the function
      final CsdlParameter parameterUstid1 = new CsdlParameter();
      parameterUstid1.setName(PARAMETER_USTID_1);
      parameterUstid1.setNullable(false);
      parameterUstid1.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

      final CsdlParameter parameterUstid2 = new CsdlParameter();
      parameterUstid2.setName(PARAMETER_USTID_2);
      parameterUstid2.setNullable(false);
      parameterUstid2.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());    
      
      final CsdlParameter parameterFirmenname = new CsdlParameter();
      parameterFirmenname.setName(PARAMETER_FIRMENNAME);
      parameterFirmenname.setNullable(true);
      parameterFirmenname.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()); 
      
      final CsdlParameter parameterOrt = new CsdlParameter();
      parameterOrt.setName(PARAMETER_ORT);
      parameterOrt.setNullable(true);
      parameterOrt.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()); 
      
      final CsdlParameter parameterPlz = new CsdlParameter();
      parameterPlz.setName(PARAMETER_PLZ);
      parameterPlz.setNullable(true);
      parameterPlz.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()); 
      
      final CsdlParameter parameterStrasse = new CsdlParameter();
      parameterStrasse.setName(PARAMETER_STRASSE);
      parameterStrasse.setNullable(true);
      parameterStrasse.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());     
      
      final CsdlParameter parameterDruck = new CsdlParameter();
      parameterDruck.setName(PARAMETER_DRUCK);
      parameterDruck.setNullable(true);
      parameterDruck.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()); 
      
      // Create the return type of the function
      final CsdlReturnType returnType = new CsdlReturnType();
      returnType.setCollection(false);
      returnType.setType(ET_RESPONSE_FQN);

      // Create the function
      final CsdlFunction function = new CsdlFunction();
      function.setName(FUNCTION_EVATR_VATID_VERIFICATION_CALL_FQN.getName())
          .setParameters(Arrays.asList( parameterUstid1, parameterUstid2, parameterFirmenname, parameterOrt, parameterPlz, parameterStrasse, parameterDruck ))
          .setReturnType(returnType);
      functions.add(function);

      return functions;
    }

    return null;
  }

  @Override
  public CsdlFunctionImport getFunctionImport(FullQualifiedName entityContainer, String functionImportName) {
    if(entityContainer.equals(CONTAINER)) {
      if(functionImportName.equals(FUNCTION_EVATR_VATID_VERIFICATION_CALL_FQN.getName())) {
        return new CsdlFunctionImport()
                  .setName(functionImportName)
                  .setFunction(FUNCTION_EVATR_VATID_VERIFICATION_CALL_FQN)
                  .setEntitySet(ES_RESPONSES_NAME)
                  .setIncludeInServiceDocument(true);
      }
    }

    return null;
  } 
  
  
  
  
}
