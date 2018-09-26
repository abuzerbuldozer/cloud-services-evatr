# DEVELOPING ODATA v4 SERVICE WITH OLINGO JAVA LIBRARY

## Overview
This document guides you through the steps required to write an OData Service based on the Olingo OData 4.0 Library for Java (based on current Olingo 4.0.0 which can be get via the Download-Page).

The requirement is to have an ODATA service in SCP as wrapper for the online „tax id number check“-service (aka „UstID“) based on XML-RPC

We will create a Web Application and deploy it on a local Tomcat server. Afterwards, the OData service can be invoked from a browser and it will provide the data according to the OData V4 specification. This tutorial is kept as simple as possible, in order to fully concentrate on the implementation of the service. Only  READ entity and collection scenarios are covered, READ scenarios also covers a function call which returns an entity. So the function call also part of READ entity scenario.

Our scenario is writing an odata function call which redirects the call to the XML-RPC interface using cloud-services-xmlrpc-evatr.jar  and  converts the response that is coming back from XML-RPC interface to odata response. 



## Build Environment
You need to familiar with Maven and Git and have installed at least the following min versions:

- Maven Version 3.2.0
- JDK Version 1.6

We will create a Web Application and deploy it on a local Tomcat server. Afterwards, the OData service can be invoked from a browser and it will provide the data according to the OData V4 specification.

The service will display a list of response objects and all properties of a response object. 

We have __ResultData.java__ class  in  `com.all41.sap.cloud.services.evatr.xmlrpc` package of `cloud-services-xmlrpc-evatr.jar` which we developed before and added in our odata project as dependency. We will store result of our webservice call in that class and convert it to related odata edm object type. ( edmEntity, edmEntitySet ). 
 
 
 
 
 
### Create Project using the maven archetype “webapp”
Within Eclipse, open the Maven Project wizard via `File -> New -> Other -> Maven -> Maven Project`

On the second wizard page, choose the archetype: `maven-archetype-webapp`

 ![Archetype Selection](docs/pic1.png?raw=true "Archetype Selection")
 
On the next page, enter the following information:

* __Groupd Id  :__ `com.all41.cloud.service`
* __Artifact Id:__ `DemoService`
* __Version    :__ `4.0.0`
* __Package    :__ `com.all41.sap.cloud.service`

__Note:__ _If you’re using this wizard for the first time, it might take some time, as maven needs to download the archetype itself to your local maven-repo._

After finishing the wizard, the next step is to edit the pom.xml file.

### Edit pom file

In our project, we will be using several libraries, e.g. the Olingo libraries. In the pom.xml file, we specify the dependencies and Maven will download them to our local maven repository. Furthermore, the pom.xml file tells Maven which output we want to have as result of our build. In our case, this is a war file.

In our example, the pom.xml file looks as follows:

 ![Pom.xml](docs/pic2.png?raw=true "Pom.xml - 1st Part")

As above, in the first part we are defining required build plugins and properties that we will used in the other parts of our pom.xml.

Second part contains information about dependencies.As you can see, we added [cloud-services-xmlrpc-evatr](/tree/master/cloud-services-xmlrpc-evatr) project as dependency. Other dependencies is required for olingo.

 ![Pom.xml](docs/pic3.png?raw=true "Pom.xml - 2nd Part")

### Check Java build path

In order to check the Build path settings, open the context menu on the project and choose _Build Path -> Configure Build Path_ …

 ![Java Build Path](docs/pic4.png?raw=true "Java Build Path")

Select the Source tab. You might see that the source folder src/main/java is configured, but displays an error marker.

 ![Java Build Path](docs/pic5.png?raw=true "Java Build Path")

The reason is that it is missing on file system. So the solution is to create the required folder in Eclipse.

 ![Java Build Path](docs/pic6.png?raw=true "Java Build Path")

Afterwards, open the Build Path dialog again. The second error might be about missing test source folder. Since we don’t need it for our tutorial, we remove it from the build path.

 ![Java Build Path](docs/pic7.png?raw=true "Java Build Path")

### Build the project
Although the project doesn’t contain any source files yet, let’s perform our first Maven build, in order to check for any problems.
From the context menu on the project node, chose _Run As -> maven build_ If you have never executed the build before, Maven asks you to specify at least one goal. Enter the usual goals `clean install` and press `Run`

 ![Maven Build](docs/pic8.png?raw=true "Maven Build")

The log output is provided in the Eclipse Console view. You should check it for the output `Build Success`

__Note:__ If maven provides an error marker right from the beginning,it would help to update your Project: From context menu on project node, choose Maven -> update Project ->
### Implementation
The implementation of an OData service based on Olingo server library can be grouped in the following steps:

- Declaring the metadata of the service
- Handle service requests

Since our example service has to run on a web server, we have to create some code which calls our service in a web application:

* Web application implementation

The following section will guide you through every step in detail.

# Declare the metadata

## Background

According to the OData specification, an OData service has to declare its structure in the so-called [Metadata Document](http://docs.oasis-open.org/odata/odata/v4.01/cs01/part1-protocol/odata-v4.01-cs01-part1-protocol.html#sec_MetadataDocumentRequest). 
This document defines the contract, such that the user of the service knows which requests can be executed, the structure of the result and how the service can be navigated.
The Metadata Document can be invoked via the following URI:

```
<serviceroot>/$metadata´
```

Furthermore, OData specifies the usage of the so-called Service Document Here, the user can see which Entity Collections are offered by an OData service.

The service document can be invoked via the following URI:

```
<serviceroot>/
```

The information that is given by these 2 URIs, has to be implemented in the service code. Olingo provides an API for it and we will use it in the implementation of our CsdlEdmProvider.

## Create class

Create package `com.all41.sap.cloud.service.odata` . Create class `EvatrEdmProvider` and specify the superclass `org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider`.

__Note:__ _edm_ is the abbreviation for Entity Data Model. Accordingly, we understand that the `CsdlEdmProvider` is supposed to provide static descriptive information.
The Entity Model of the service can be defined in the EDM Provider. The EDM model basically defines the available EntityTypes and the relation between the entities. An EntityType consists of primitive, complex or navigation properties. The model can be invoked with the Metadata Document request.

As we can see, the [Olingo server API](https://olingo.apache.org/javadoc/odata4/index.html) provides one package that contains interfaces for the description of the metadata:

 ![Olingo v4 Javadoc](docs/pic9.png?raw=true "Olingo v4 Javadoc")


Some of these interfaces are going to be used in the following sections. Note: Take a look into the [Javadoc](https://olingo.apache.org/javadoc/odata4/index.html)

## Implement the required methods

The base class `CsdlAbstractEdmProvider` provides methods for declaring the metadata of all OData elements.

For example: The entries that are displayed in the Service Document are provided by the method getEntityContainerInfo() The structure of EntityTypes is declared in the method getEntityType()

In our case, we implement following methods :

* __getEntityType()__ Here we declare the EntityType “Response” and its properties
* __getEntitySet()__ Here we state that the list of response can be called via the EntitySet “Responses”
* __getEntityContainer()__ Here we provide a Container element that is necessary to host the EntitySet.
* __getSchemas()__ The Schema is the root element to carry the elements.
* __getEntityContainerInfo()__ Information about the EntityContainer to be displayed in the Service Document
* __getFunctions()__ Provide the definition of the Function Edm elements (parameters ). 
* __getFunctionImport()__ To express that function can be called statically.

In Eclipse, in order to select the methods to override, right click into the Java editor and from the context menu choose _Source -> Override/Implement Methods_ . Select the mentioned methods and press OK.

 ![Methods To Implement](docs/pic10.png?raw=true "Methods To Implement In Edm Provider")

Let’s have a closer look at our methods in detail.
First, we need to declare some constants, to be used in the code below:

```
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
```
 
__getEntityType()__

In our service, we want to provide a list of responses to users who call the OData service. The user of our service, for example an app-developer, may ask: What does such a "response" entry look like? How is it structured? Which information about a response is provided? For example, the name of it and which data types can be expected from these properties? Such information is provided by a `CsdlEdmProvider` (and for convenience we extend the `CsdlAbstractEdmProvider`).

In our service, for modelling the `CsdlEntityType`, we have to provide the following metadata:

The name of the EntityType: "_Response_" The properties: _name_ and _type_ and additional info, e.g. “_ID_” of type `EdmPrimitiveTypeKind.String` Which of the properties is the “_key_” property: a reference to the “_ID_” property.

```
public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
// this method is called for one of the EntityTypes that are configured in the Schema
    if(entityTypeName.equals(ET_RESPONSE_FQN)){
      //create EntityType properties
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
      entityType.setProperties(Arrays.asList( datum, uhrzeit, errorCode, ustId1, ustId2, firmenname, ort, plz, strasse, ergname, ergort, ergplz, ergstr, gueltigAb, gueltigBis, druck ));
      entityType.setKey(Collections.singletonList(propertyRef));
      return entityType;
    }
    return null; 
}
```

__getEntitySet()__

The procedure for declaring the Entity Sets is similar. An EntitySet is a crucial resource, when an OData service is used to request data. In our example, we will invoke the following URL, which we expect to provide us a list of responses:
```
http://localhost:8080/DemoService/EvatrServlet.svc/Responses
```

When declaring an EntitySet, we need to define the type of entries which are contained in the list, such as an `CsdlEntityType`. In our service, we set our previously created `CsdlEntityType`, which is referred by a FullQualifiedName.
```
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
```

__getFunction()__
First, we check which function we have to return. Then, a list of parameters and the return type are created. At the end all parts are fit together and get returned as new CsdlFunction Object.
 ```
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
```  
  
__getFunctionImport()__

We have created the function itself. To express that function can be called statically we have to override the method getFunctionImport().
```
public CsdlFunctionImport getFunctionImport(FullQualifiedName entityContainer,String functionImportName) {
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
getEntityContainer()
In order to provide data, our OData service needs an EntityContainer that carries the EntitySets. In our service, we have only one EntitySet and one function import, so we create one EntityContainer and add our EntitySet and function import inside of it.
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
```
 
__getSchemas()__

Up to this point, we have declared the type of our data (CsdlEntityType) and our list (CsdlEntitySet), and we have put it into a container (CsdlEntityContainer). Now we are required to put all these elements into a CsdlSchema. While the model of an OData service can have several schemas, in most cases there will probably be only one schema. So, in our service, we create a list of schemas, where we add one new CsdlSchema object. The schema is configured with a Namespace, which serves to uniquely identify all elements. Then our elements are added to the Schema.
```
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
```

__getEntityContainerInfo()__
```
public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {
    // This method is invoked when displaying the service document at e.g. 
    // http://localhost:8080/DemoService/EvatrService.svc
    if(entityContainerName == null || entityContainerName.equals(CONTAINER)){
      CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
      entityContainerInfo.setContainerName(CONTAINER);
      return entityContainerInfo;
    }
    return null;
}
```
__Summary:__ We have created a class that declares the metadata of our OData service. We have declared the main elements of an OData service: _EntityType_,_Function_, _FunctionImport_, _EntitySet_, _EntityContainer_ and `Schema` (with the corresponding Olingo classes `CsdlEntityType`,`CsdlFunction`,`CsdlFunctionImport`,`CsdlEntitySet`,`CsdlEntityContainer` and `CsdlSchema`).

At runtime of an OData service, such metadata can be viewed by invoking the Metadata Document.

In our service invokation of the URL: `http://localhost:8080/DemoService/EvatrService.svc/$metadata`

Give us the result below:
```
<?xml version='1.0' encoding='UTF-8'?>
<edmx:Edmx xmlns:edmx="http://docs.oasis-open.org/odata/ns/edmx" Version="4.0">
  <edmx:DataServices>
    <Schema xmlns="http://docs.oasis-open.org/odata/ns/edm" Namespace="OData.All41">
      <EntityType Name="Response">
        <Key>
          <PropertyRef Name="UstId_1"/>
        </Key>
        <Property Name="Datum" Type="Edm.String"/>
        <Property Name="Uhrzeit" Type="Edm.String"/>
        <Property Name="ErrorCode" Type="Edm.String"/>
        <Property Name="UstId_1" Type="Edm.String"/>
        <Property Name="UstId_2" Type="Edm.String"/>
        <Property Name="Firmenname" Type="Edm.String"/>
        <Property Name="Ort" Type="Edm.String"/>
        <Property Name="PLZ" Type="Edm.String"/>
        <Property Name="Strasse" Type="Edm.String"/>
        <Property Name="Erg_Name" Type="Edm.String"/>
        <Property Name="Erg_Ort" Type="Edm.String"/>
        <Property Name="Erg_PLZ" Type="Edm.String"/>
        <Property Name="Erg_Str" Type="Edm.String"/>
        <Property Name="Gueltig_ab" Type="Edm.String"/>
        <Property Name="Gueltig_bis" Type="Edm.String"/>
        <Property Name="Druck" Type="Edm.String"/>
      </EntityType>
      <Function Name="EvatrVatIDVerificationCall">
        <Parameter Name="UstId_1" Type="Edm.String" Nullable="false"/>
        <Parameter Name="UstId_2" Type="Edm.String" Nullable="false"/>
        <Parameter Name="Firmenname" Type="Edm.String"/>
        <Parameer Name="Ort" Type="Edm.String"/>
        <Parameter Name="PLZ" Type="Edm.String"/>
        <Parameter Name="Strasse" Type="Edm.String"/>
        <Parameter Name="Druck" Type="Edm.String"/>
       <ReturnType Type="OData.All41.Response"/>
      </Function>
      <EntityContainer Name="Container">
        <EntitySet Name="Responses" EntityType="OData.All41.Response"/>
        <FunctionImport Name="EvatrVatIDVerificationCall" 
         Function="OData.All41.EvatrVatIDVerificationCall" 
         EntitySet="OData.All41.Responses" IncludeInServiceDocument="true"/>
      </EntityContainer>
     </Schema>
  </edmx:DataServices>
</edmx:Edmx>
```

The Service Document can be invoked to view the Entity Sets, like in our example at the URL: `http://localhost:8080/DemoService/EvatrService.svc/`

Which give us the Service Document as result:
```
<app:service xmlns:atom="http://www.w3.org/2005/Atom" xmlns:app="http://www.w3.org/2007/app" 
             xmlns:metadata="http://docs.oasis-open.org/odata/ns/metadata" 
             metadata:context="$metadata">
  <app:workspace>
    <atom:title>OData.All41.Container</atom:title>
    <app:collection href="Responses">
      <atom:title>Responses</atom:title>
    </app:collection>
    <metadata:function-import href="EvatrVatIDVerificationCall">
      <atom:title>EvatrVatIDVerificationCall</atom:title>
    </metadata:function-import>
  </app:workspace>
</app:service>
```

__Note:__ After implementing the EvatrEdmProvider, we can, as an intermediate step, build/deploy the service and invoke the 2 static pages: Service Document and Metadata Document. 

# Provide the data

After implementing the `EvatrEdmProvider`, the next step is the main task of an OData service: provide data. Our service is going to be invoked by a user who wants to check response of a web service call( Evatr Vat Identification Service ) which responses are offered by our  `cloud-services-xmlrpc-evatr.jar` . 

The tasks that you have to do to provide data can be divided into 4 tasks:

1. Check the URI We need to identify the requested resource and have to consider Query Options (if available)
2. Provide the data Based on the URI info, we have to obtain the data from our data store (can be e.g. a database or our web service result coming from jar file.)
3. Serialize the data The data has to be transformed into the required format
4. Configure the response Since we are implementing a “processor”, the last step is to provide the response object

These 4 steps will be considered in the implementation of the `readEntityCollection()` method.

## Background

In terms of _Olingo_, while processing a service request, a Processor instance is invoked that is supposed to understand the (user HTTP-) request and deliver the desired data. _Olingo_ provides API for processing different kind of service requests: Such a service request can ask for a list of entities, or for one entity, or one property.

Example: In our service, we have stated in our Metadata Document that we will provide a list of “response” whenever the _EntitySet_ with name “_Responses_” is invoked. This means that the user of our OData service will append the EntitySet name to the root URL of the service and then invoke the full URL. This is `http://localhost:8080/DemoService/EvatrService.svc/Responses` So, whenever this URL is fired, _Olingo_ will invoke the `EntityCollectionProcessor` implementation of our OData service. Then our `EntityCollectionProcessor` implementation is expected to provide a list of responses.
As we have already mentioned, the Metadata Document is the contract for providing data. This means that when it comes to provide the actual data, we have to do it according to the specified metadata. For example, the property names have to match, also the types of the properties, and, if specified, the length of the strings, etc

## Create class

Within our package `com.all41.sap.cloud.service`, we create a Java class `EvatrEntityCollectionProcessor` that implements the interface `org.apache.olingo.server.api.processor.EntityCollectionProcessor`.

 ![Methods To Implement](docs/pic11.png?raw=true "Methods To Implement In Entity Collection Processor")


## Implement the required methods

After creation of the Java class, we can see that there are 2 methods to be implemented:

* _init()_ This method is invoked by the Olingo library, allowing us to store the context object
* _readEntityCollection()_ Here we have to fetch the required data and pass it back to the Olingo library

Let’s have a closer look

__init()__

This method is common to all processor interfaces. The _Olingo_ framework initializes the processor with an instance of the OData object. According to the Javadoc, this object is the “Root object for serving factory tasks…” We will need it later, so we store it as member variable.
```
public void init(OData odata, ServiceMetadata serviceMetadata) {
  this.odata = odata;
  this.serviceMetadata = serviceMetadata;
}
```

Don’t forget to declare the member variables

```
private OData odata;
private ServiceMetadata serviceMetadata;
constructor()
After creating the class we have to add a Constructor that takes the Storage.java object instance and stores it as a member variable which you will learn in following pages of that document :
public EvatrEntityCollectionProcessor( Storage storage ){
	  this.storage = storage;
  }
```

__readEntityCollection()__

The `EntityCollectionProcessor` exposes only one method: `readEntityCollection(...)`

Here we have to understand that this `readEntityCollection(...)` method is invoked, when the _OData_ service is called with an HTTP GET operation for an entity collection.

The readEntityCollection(...) method is used to “read” the data in the backend (this can be e.g. a database) and to deliver it to the user who calls the OData service.

The method signature:

The “request” parameter contains raw HTTP information. It is typically used for creation scenario, where a request body is sent along with the request.

With the second parameter, the “response” object is passed to our method in order to carry the response data. So here we have to set the response body, along with status code and content-type header.

The third parameter, the “uriInfo”, contains information about the relevant part of the URL. This means, the segments starting after the service name.

__Example:__ If the user calls the following URL: `http://localhost:8080/DemoService/EvatrService.svc/Responses` The `readEntityCollection(...)` method is invoked and the __uriInfoobject__ contains one segment: “_Responses_”

If the user calls the following URL: `http://localhost:8080/DemoService/EvatrService.svc/Responses?$filter=ID eq 1` Then the `readEntity(...)` method is invoked and the __uriInfo__ contains the information about the entity set and furthermore the system query option $filter and its value. __( It is just a sample. We did not implemented it )__

The last parameter, the “__responseFormat__”, contains information about the content type that is requested by the user. This means that the user has the choice to receive the data either in XML or in JSON.

__Example:__ If the user calls the following URL: `http://localhost:8080/DemoService/EvatrService.svc/Responses?$format=application/json;odata.metadata=minimal`
then the content type is: __application/json;odata.metadata=minimal__ which means that the payload is formatted in JSON (like it is shown in the introduction section of this tutorial)

__Note:__ _The content type can as well be specified via the following request header Accept: application/json;odata.metadata=minimal In this case as well, our readEntityCollection() method will be called with the parameter responseFormat containing the content type > information._
__Note:__ _If the user doesn’t specify any content type, then the default is JSON._

Why is this parameter needed? Because the `readEntityCollection(...)` method is supposed to deliver the data in the format that is requested by the user. We will use this parameter when creating a serializer based on it.
The steps for implementating the method `readEntityCollection(...)` are:

1. Which data is requested? Usually, an OData service provides different EntitySets, so first it is required to identify which EntitySet has been requested. This information can be retrieved from the uriInfo object.
2. Fetch the data As a developer of the OData service, you have to know how and where the data is stored. In many cases, this would be a database. At this point, you would connect to your database and fetch the requested data with an appropriate SQL statement. The data that is fetched from the data storage has to be put into an _EntityCollectionobject_. The package `org.apache.olingo.commons.api.data` provides interfaces that describe the actual data, not the metadata.
 ![org.apache.olingo.commons.api.data](docs/pic12.png?raw=true "org.apache.olingo.commons.api.data")

3. Transform the data Olingo expects us to provide the data as low-level InputStream object. However, Olingo supports us in doing so, by providing us with a proper "serializer". So what we have to do is create the serializer based on the requested content type, configure it and call it.
4. Configure the response The response object has been passed to us in the method signature. We use it to set the serialized data (the InputStream object). Furthermore, we have to set the HTTP status code, which means that we have the opportunity to do proper error handling. And finally we have to set the content type.
 
## About Functions and Function Import

The [OData V4 specification](http://docs.oasis-open.org/odata/odata/v4.0/errata02/os/complete/part1-protocol/odata-v4.0-errata02-os-part1-protocol-complete.html#_Toc406398201) gives us a definition what Functions, Actions are:

_Operations allow the execution of custom logic on parts of a data model. Functions are operations that do not have side effects and may support further composition, for example, with additional filter operations, functions or an action. Actions are operations that allow side effects, such as data modification, and cannot be further composed in order to avoid non-deterministic behavior. Actions and functions are either bound to a type, enabling them to be called as members of an instance of that type, or unbound, in which case they are called as static operations. Action imports and function imports enable unbound actions and functions to be called from the service root._
 
The code is simple and straight forward. First, we check which function we have to return. Then, a list of parameters and the return type are created. At the end all parts are fit together and get returned as new `CsdlFunction` Object.
 
 
Full implementation of `EvatrEntityCollectionProcessor` :
```
package com.all41.sap.cloud.service.odata;
 
import java.util.List;
import java.util.Locale;
 
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
 
import com.all41.sap.cloud.data.Storage;
 
/**
 * This class is invoked by the Olingo framework when the the OData service is invoked order to display a list/collection of data (entities).
 * This is the case if an EntitySet is requested by the user.
 * Such an example URL would be:
 * http://localhost:8080/ExampleService1/ExampleService1.svc/Products
 */
public class EvatrEntityCollectionProcessor implements EntityCollectionProcessor {
 
  private OData odata;
  private ServiceMetadata serviceMetadata;
  private Storage storage;
 
  // our processor is initialized with the OData context object
  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }
  
  public EvatrEntityCollectionProcessor( Storage storage ){
	  this.storage = storage;
  }
  
  
 
  private void readFunctionImportCollection(final ODataRequest request, final ODataResponse response,
		  final UriInfo uriInfo, final ContentType responseFormat) throws ODataApplicationException, SerializerException {
 
		  // 1st step: Analyze the URI and fetch the entity collection returned by the function import
		  // Function Imports are always the first segment of the resource path
		  final UriResource firstSegment = uriInfo.getUriResourceParts().get(0);
 
		  if(!(firstSegment instanceof UriResourceFunction)) {
		    throw new ODataApplicationException("Not implemented",
		      HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
		  }
 
		  final UriResourceFunction uriResourceFunction = (UriResourceFunction) firstSegment;
		  final EntityCollection entityCol = storage.readFunctionImportCollection(uriResourceFunction, serviceMetadata);
 
		  // 2nd step: Serialize the response entity
		  final EdmEntityType edmEntityType = (EdmEntityType) uriResourceFunction.getFunction().getReturnType().getType();
		  final ContextURL contextURL = ContextURL.with().asCollection().type(edmEntityType).build();
		  EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().contextURL(contextURL).build();
		  final ODataSerializer serializer = odata.createSerializer(responseFormat);
		  final SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entityCol, opts);
 
		  // 3rd configure the response object
		  response.setContent(serializerResult.getContent());
		  response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		  response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }
		  
  // the only method that is declared in the EntityCollectionProcessor interface
  // this method is called, when the user fires a request to an EntitySet
  // in our example, the URL would be:
  // http://localhost:8080/ExampleService1/ExampleServlet1.svc/Products
  
  public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {
 
	  final UriResource firstResourceSegment = uriInfo.getUriResourceParts().get(0);
 
	  if(firstResourceSegment instanceof UriResourceEntitySet) {
	    readEntityCollectionInternal(request, response, uriInfo, responseFormat);
	  } else if(firstResourceSegment instanceof UriResourceFunction) {
	    readFunctionImportCollection(request, response, uriInfo, responseFormat);
	  } else {
	    throw new ODataApplicationException("Not implemented",
	      HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
	    Locale.ENGLISH);
	  }
	}  
  
  public void readEntityCollectionInternal(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {
 
    // 1st we have retrieve the requested EntitySet from the uriInfo object (representation of the parsed service URI)
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
 
    // 2nd: fetch the data from backend for this requested EntitySetName // it has to be delivered as EntitySet object
//    EntityCollection entitySet = getData(edmEntitySet);
    EntityCollection entityCollection = storage.readEntitySetData(edmEntitySet);
    
    // 3rd: create a serializer based on the requested format (json)
    ODataSerializer serializer = odata.createSerializer(responseFormat);
    
    // 4th: Now serialize the content: transform from the EntitySet object to InputStream
    EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
 
    final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
    EntityCollectionSerializerOptions opts =
        EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
    SerializerResult serializedContent = serializer.entityCollection(serviceMetadata, edmEntityType, entityCollection, opts);
 
    // Finally: configure the response object: set the body, headers and status code
    response.setContent(serializedContent.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }
  
}
```

## Create a data-class

Create a new package `com.all41.sap.cloud.data`
Within this package, create a new class `Storage.java` to simulate the data layer (in a real scenario, this would be e.g. a database or any other data storage)

Here’s the full implementation of this class:
```
package com.all41.sap.cloud.data;
 
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
 
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceFunction;
 
import com.all41.sap.cloud.service.odata.EvatrEdmProvider;
import com.all41.sap.cloud.services.evatr.xmlrpc.ResultData;
import com.all41.sap.cloud.util.Util;
 
public class Storage {
 
	private List<Entity> responseList;
 
	public Storage() {
		responseList = new ArrayList<Entity>();
		initSampleData();
	}
 
	/* PUBLIC FACADE */
 
	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet) throws ODataApplicationException {
 
		// actually, this is only required if we have more than one Entity Sets
		if (edmEntitySet.getName().equals(EvatrEdmProvider.ES_RESPONSES_NAME)) {
			return getResponses();
		}
 
		return null;
	}
 
	public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
			throws ODataApplicationException {
 
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();
 
		// actually, this is only required if we have more than one Entity Type
		if (edmEntityType.getName().equals(EvatrEdmProvider.ET_RESPONSE_NAME)) {
			return getResponse(edmEntityType, keyParams);
		}
 
		return null;
	}
 
	/* INTERNAL */
 
	private EntityCollection getResponses() {
		EntityCollection retEntitySet = new EntityCollection();
 
		for (Entity responseEntity : this.responseList) {
			retEntitySet.getEntities().add(responseEntity);
		}
 
		return retEntitySet;
	}
 
	private Entity getResponse(EdmEntityType edmEntityType, List<UriParameter> keyParams)
			throws ODataApplicationException {
 
		// the list of entities at runtime
		EntityCollection entitySet = getResponses();
 
		/* generic approach to find the requested entity */
		Entity requestedEntity = Util.findEntity(edmEntityType, entitySet, keyParams);
 
		if (requestedEntity == null) {
			// this variable is null if our data doesn't contain an entity for
			// the requested key
			// Throw suitable exception
			throw new ODataApplicationException("Entity for requested key doesn't exist",
					HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
		}
 
		return requestedEntity;
	}
 
	/* HELPER */
	private void initSampleData() {
 
		// add some sample response entities
		final Entity e1 = new Entity()
				.addProperty(new Property(null, "Datum", ValueType.PRIMITIVE, new String("13.09.2018")))
				.addProperty(new Property(null, "Uhrzeit", ValueType.PRIMITIVE, new String("14:22:00")))
				.addProperty(new Property(null, "ErrorCode", ValueType.PRIMITIVE, new String("200")))
				.addProperty(new Property(null, "UstId_1", ValueType.PRIMITIVE, new String("'DE199226238'")))
				.addProperty(new Property(null, "UstId_2", ValueType.PRIMITIVE, new String("FR82542065479")))
				.addProperty(new Property(null, "Firmenname", ValueType.PRIMITIVE, new String("PSA AUTOMOBILES A")))
				.addProperty(new Property(null, "Ort", ValueType.PRIMITIVE, new String("POISSY")))
				.addProperty(new Property(null, "PLZ", ValueType.PRIMITIVE, new String("78307")))
				.addProperty(new Property(null, "Strasse", ValueType.PRIMITIVE, new String("strasse sample")))
				.addProperty(new Property(null, "Erg_Name", ValueType.PRIMITIVE, new String("A")))
				.addProperty(new Property(null, "Erg_Ort", ValueType.PRIMITIVE, new String("A")))
				.addProperty(new Property(null, "Erg_PLZ", ValueType.PRIMITIVE, new String("B")))
				.addProperty(new Property(null, "Erg_Str", ValueType.PRIMITIVE, new String("C")))
				.addProperty(new Property(null, "Gueltig_ab", ValueType.PRIMITIVE, new String("1.11.1.")))
				.addProperty(new Property(null, "Gueltig_bis", ValueType.PRIMITIVE, new String("22.2.2.")))
				.addProperty(new Property(null, "Druck", ValueType.PRIMITIVE, "nein"));
		e1.setId(createId("Responses", 1));
		responseList.add(e1);
	}
 
	public Entity createEntityFromEvatrExecuteResult(ResultData rs) {
 
		// add some sample response entities
		Entity e1 = new Entity().addProperty(new Property(null, "Datum", ValueType.PRIMITIVE, rs.getDatum()))
				.addProperty(new Property(null, "Uhrzeit", ValueType.PRIMITIVE, rs.getUhrzeit()))
				.addProperty(new Property(null, "ErrorCode", ValueType.PRIMITIVE, rs.getErrorCode()))
				.addProperty(new Property(null, "UstId_1", ValueType.PRIMITIVE, rs.getUstId_1()))
				.addProperty(new Property(null, "UstId_2", ValueType.PRIMITIVE, rs.getUstId_2()))
				.addProperty(new Property(null, "Firmenname", ValueType.PRIMITIVE, rs.getFirmenname()))
				.addProperty(new Property(null, "Ort", ValueType.PRIMITIVE, rs.getOrt()))
				.addProperty(new Property(null, "PLZ", ValueType.PRIMITIVE, rs.getPLZ()))
				.addProperty(new Property(null, "Strasse", ValueType.PRIMITIVE, rs.getStrasse()))
				.addProperty(new Property(null, "Erg_Name", ValueType.PRIMITIVE, rs.getErg_Name()))
				.addProperty(new Property(null, "Erg_Ort", ValueType.PRIMITIVE, rs.getErg_Ort()))
				.addProperty(new Property(null, "Erg_PLZ", ValueType.PRIMITIVE, rs.getErg_PLZ()))
				.addProperty(new Property(null, "Erg_Str", ValueType.PRIMITIVE, rs.getErg_Str()))
				.addProperty(new Property(null, "Gueltig_ab", ValueType.PRIMITIVE, rs.getGueltig_ab()))
				.addProperty(new Property(null, "Gueltig_bis", ValueType.PRIMITIVE, rs.getGueltig_bis()))
				.addProperty(new Property(null, "Druck", ValueType.PRIMITIVE, rs.getDruck()));
		e1.setId(createId("Responses", 1));
		responseList.add(e1);
 
		return e1;
	}
 
	private URI createId(String entitySetName, Object id) {
		try {
			return new URI(entitySetName + "(" + String.valueOf(id) + ")");
		} catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
		}
	}
 
	public EntityCollection readFunctionImportCollection(final UriResourceFunction uriResourceFunction,
			final ServiceMetadata serviceMetadata) throws ODataApplicationException {
 
		if (EvatrEdmProvider.FUNCTION_EVATR_VATID_VERIFICATION_CALL
				.equals(uriResourceFunction.getFunctionImport().getName())) {
 
			final EdmEntityType responseEntityType = serviceMetadata.getEdm()
					.getEntityType(EvatrEdmProvider.ET_RESPONSE_FQN);
			final List<Entity> resultEntityList = new ArrayList<Entity>();
 
			final EntityCollection resultCollection = new EntityCollection();
			resultCollection.getEntities().addAll(resultEntityList);
			return resultCollection;
		} else {
			throw new ODataApplicationException("Function not implemented",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
		}
	}
 
	public Entity readFunctionImportEntity(final UriResourceFunction uriResourceFunction,
			final ServiceMetadata serviceMetadata) throws ODataApplicationException {
 
		final EntityCollection entityCollection = readFunctionImportCollection(uriResourceFunction, serviceMetadata);
		final EdmEntityType edmEntityType = (EdmEntityType) uriResourceFunction.getFunction().getReturnType().getType();
 
		ResultData rs = null;
		try {
			rs = Util.executeEvatrVerificationService(uriResourceFunction.getParameters());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Entity em = createEntityFromEvatrExecuteResult(rs);
		return em;
	}
 
 
}
```
The _Public Façade_ contains the methods that are called from outside.
They are data-layer-agnostic; their parameters are objects from the OData world.
The implementation of these methods simply delegates the logic to the internal methods.

The internal methods do know about the names of tables or columns and these methods know how to e.g. find a single response.
 
__initSampleData ()__

We have not elaborated on fetching the actual data. In our tutorial, to keep the code as simple as possible, we use a little helper method that delivers some hardcoded entries. Since we are supposed to deliver the data inside an `EntityCollection` instance, we create the instance, ask it for the (initially empty) list of entities and add some new entities to it. We create the entities and their properties according to what we declared in our `EvatrEdmProvider` class. So we have to take care to provide the correct names to the new property objects. If a client requests the response in [ATOM](http://docs.oasis-open.org/odata/odata-atom-format/v4.0/odata-atom-format-v4.0.html) format, each entity have to provide it`s own entity id. The method createId allows us to create an id in a convenient way.
```
private void initSampleData(){
 
       final Entity e1 = new Entity()
                 .addProperty(new Property(null, "Datum", ValueType.PRIMITIVE, new String( "13.09.2018"        ) ) )
        	      .addProperty(new Property(null, "Uhrzeit", ValueType.PRIMITIVE, new String("14:22:00") ) )
        	      .addProperty(new Property(null, "ErrorCode", ValueType.PRIMITIVE, new String("200")))
        	      .addProperty(new Property(null, "UstId_1", ValueType.PRIMITIVE, new String("'DE199226238'")))
        	      .addProperty(new Property(null, "UstId_2", ValueType.PRIMITIVE, new String("FR82542065479")))
        	      .addProperty(new Property(null, "Firmenname", ValueType.PRIMITIVE, new String("PSA AUTOMOBILES A")))
        	      .addProperty(new Property(null, "Ort", ValueType.PRIMITIVE, new String("POISSY")))
        	      .addProperty(new Property(null, "PLZ", ValueType.PRIMITIVE, new String("78307")))
        	      .addProperty(new Property(null, "Strasse", ValueType.PRIMITIVE, new String("strasse sample")))
        	      .addProperty(new Property(null, "Erg_Name", ValueType.PRIMITIVE, new String("A")))
        	      .addProperty(new Property(null, "Erg_Ort", ValueType.PRIMITIVE, new String("A")))
        	      .addProperty(new Property(null, "Erg_PLZ", ValueType.PRIMITIVE, new String("B")))
        	      .addProperty(new Property(null, "Erg_Str", ValueType.PRIMITIVE, new String("C") ) )
        	      .addProperty(new Property(null, "Gueltig_ab", ValueType.PRIMITIVE, new String("1.11.1.") ) )
        	      .addProperty(new Property(null, "Gueltig_bis", ValueType.PRIMITIVE, new String("22.2.2.") ) )
        	      .addProperty(new Property(null, "Druck", ValueType.PRIMITIVE, "nein"));
        	      e1.setId(createId("Responses", 1));
        	      responseList.add(e1);
}
createId()
private URI createId(String entitySetName, Object id) {
    try {
        return new URI(entitySetName + "(" + String.valueOf(id) + ")");
    } catch (URISyntaxException e) {
        throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
    }
}
```
# Web Application

After declaring the metadata and providing the data, our OData service implementation is done. The last step is to enable our OData service to be called on a web server. Therefore, we are wrapping our service by a web application.

The web application is defined in the `web.xml` file, where a servlet is registered. The servlet is a standard `HttpServlet` which dispatches the user requests to the _Olingo_ framework.

Let’s quickly do the remaining steps:

## Create and implement the Servlet

Create a new package `com.all41.sap.cloud.web`. Create Java class with name `EvatrServlet` that inherits from `HttpServlet`.

![HttpServlet](docs/pic13.png?raw=true "HttpServlet")

Override the _service()_ method. Basically, what we are doing here is to create an `ODataHttpHandler`, which is a class that is provided by _Olingo_. It receives the user request and if the URL conforms to the OData specification, the request is delegated to the processor implementation of the OData service. This means that the handler has to be configured with all processor implementations that have been created along with the OData service (in our example, only one processor). Furthermore, the `ODataHttpHandler` needs to carry the knowledge about the `CsdlEdmProvider`.

This is where our two implemented classes come together, the metadata declaration and the data provisioning.
```
public class EvatrServlet extends HttpServlet {
 
  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(EvatrServlet.class);
 
  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
 
      HttpSession session = req.getSession(true);
      Storage storage = (Storage) session.getAttribute(Storage.class.getName());
      if (storage == null) {
         storage = new Storage();
         session.setAttribute(Storage.class.getName(), storage);
      }	  
	  
    try {
      // create odata handler and configure it with EdmProvider and Processor
      OData odata = OData.newInstance();
      ServiceMetadata edm = odata.createServiceMetadata(new EvatrEdmProvider(), new ArrayList<EdmxReference>());
      ODataHttpHandler handler = odata.createHandler(edm);
      handler.register(new EvatrEntityCollectionProcessor( storage));
      handler.register(new EvatrEntityProcessor(storage));
 
      // let the handler do the work
      handler.process(req, resp);
 
    } catch (RuntimeException e) {
      LOG.error("Server Error occurred in ExampleServlet", e);
      throw new ServletException(e);
    }
  }
}
```

## Edit the web.xml

The very last step of our tutorial is to register the Servlet in the web.xml file. Furthermore, we need to specify the url-pattern for the servlet, such that our OData service can be invoked.
Open the _src/main/webapp/WEB-INF/web.xml_ file and paste the following content into it:
```
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
    id="WebApp_ID" version="2.5">
 
<servlet>
  <servlet-name>EvatrServlet</servlet-name>
  <servlet-class> com.all41.sap.cloud.web.EvatrServlet </servlet-class>
  <load-on-startup>1</load-on-startup>
</servlet>
 
<servlet-mapping>
  <servlet-name>EvatrServlet</servlet-name>
  <url-pattern>/EvatrService.svc/*</url-pattern>
</servlet-mapping>
</web-app>
```

That’s it. Now we can build and run the web application.

# Run the service

Running the service means build the war file and deploy it on a server. In our tutorial, we are using the Eclipse web integration tools, which make life easier.

## Run with Eclipse

Select your project and from the context menu choose _Run As -> Run on Server_ If you don’t have any server configured in Eclipse, you have to “manually define a new server” in the subsequent dialog. If you have installed a Tomcat server on your local file system, you can use it here. If not, you can use the _Basic -> J2EE Preview_ option, which is should be enough for our tutorial.

![RunProject](docs/pic14.png?raw=true "RunProject")

__Note:__ _You might have to first execute maven build and also press F5 to refresh the content of the Eclipse project_

After pressing "run", Eclipse starts the internal server and deploys the web application on it. Then the Eclipse internal Browser View is opened and the index.jsp file that has been generated into our Example project is opened. We ignore it. Instead, we open our OData service in our favorite browser.

The content of index.jsp is :
```
<html>
<body>
<h2>EVATR FOREIGN VAT IDENTIFICATION SERVICE</h2>
<a href="EvatrService.svc/">OData Olingo V4 Evatr Service</a>
</body>
</html>
```
__Note:__ _If you face problems related to the server, it helps to restart your Eclipse IDE._

## The Service URLs

Try the following URLs:

### Service Document

`http://localhost:8080/DemoService/EvatrService.svc/`

The expected result is the Service Document which displays our `EntityContainerInfo`:

```
<app:service xmlns:atom="http://www.w3.org/2005/Atom" xmlns:app="http://www.w3.org/2007/app" 
             xmlns:metadata="http://docs.oasis-open.org/odata/ns/metadata" 
             metadata:context="$metadata">
  <app:workspace>
    <atom:title>OData.All41.Container</atom:title>
    <app:collection href="Responses">
      <atom:title>Responses</atom:title>
    </app:collection>
    <metadata:function-import href="EvatrVatIDVerificationCall">
      <atom:title>EvatrVatIDVerificationCall</atom:title>
    </metadata:function-import>
  </app:workspace>
</app:service>
```

### Metadata Document

`http://localhost:8080/DemoService/EvatrService.svc/$metadata`

The expected result is the Metadata Document that displays our Schema, EntityType, EntityContainer and EntitySet.

```
<?xml version='1.0' encoding='UTF-8'?>
<edmx:Edmx xmlns:edmx="http://docs.oasis-open.org/odata/ns/edmx" Version="4.0">
  <edmx:DataServices>
    <Schema xmlns="http://docs.oasis-open.org/odata/ns/edm" Namespace="OData.All41">
      <EntityType Name="Response">
        <Key>
          <PropertyRef Name="UstId_1"/>
        </Key>
        <Property Name="Datum" Type="Edm.String"/>
        <Property Name="Uhrzeit" Type="Edm.String"/>
        <Property Name="ErrorCode" Type="Edm.String"/>
        <Property Name="UstId_1" Type="Edm.String"/>
        <Property Name="UstId_2" Type="Edm.String"/>
        <Property Name="Firmenname" Type="Edm.String"/>
        <Property Name="Ort" Type="Edm.String"/>
        <Property Name="PLZ" Type="Edm.String"/>
        <Property Name="Strasse" Type="Edm.String"/>
        <Property Name="Erg_Name" Type="Edm.String"/>
        <Property Name="Erg_Ort" Type="Edm.String"/>
        <Property Name="Erg_PLZ" Type="Edm.String"/>
        <Property Name="Erg_Str" Type="Edm.String"/>
        <Property Name="Gueltig_ab" Type="Edm.String"/>
        <Property Name="Gueltig_bis" Type="Edm.String"/>
        <Property Name="Druck" Type="Edm.String"/>
      </EntityType>
      <Function Name="EvatrVatIDVerificationCall">
        <Parameter Name="UstId_1" Type="Edm.String" Nullable="false"/>
        <Parameter Name="UstId_2" Type="Edm.String" Nullable="false"/>
        <Parameter Name="Firmenname" Type="Edm.String"/>
        <Parameer Name="Ort" Type="Edm.String"/>
        <Parameter Name="PLZ" Type="Edm.String"/>
        <Parameter Name="Strasse" Type="Edm.String"/>
        <Parameter Name="Druck" Type="Edm.String"/>
       <ReturnType Type="OData.All41.Response"/>
      </Function>
      <EntityContainer Name="Container">
        <EntitySet Name="Responses" EntityType="OData.All41.Response"/>
        <FunctionImport Name="EvatrVatIDVerificationCall" 
         Function="OData.All41.EvatrVatIDVerificationCall" 
         EntitySet="OData.All41.Responses" IncludeInServiceDocument="true"/>
      </EntityContainer>
     </Schema>
  </edmx:DataServices>
</edmx:Edmx>
``` 
### Query / EntitySet

`http://localhost:8080/DemoService/EvatrService.svc/Responses`

The expected result is the hardcoded list of response entries, which we have coded in our processor implementation:

```
<a:feed xmlns:a="http://www.w3.org/2005/Atom" xmlns:m="http://docs.oasis-open.org/odata/ns/metadata" xmlns:d="http://docs.oasis-open.org/odata/ns/data" m:context="$metadata#Responses">
  <a:id>
   https://cloudservicesods0016633819tria.hanatrial.ondemand.com/DemoService/EvatrService.svc/Responses
  </a:id>
  <a:entry>
    <a:id>Responses(1)</a:id>
    <a:title/>
    <a:summary/>
    <a:updated>2018-09-24T06:30:16Z</a:updated>
    <a:author>
    <a:name/>
    </a:author>
    <a:link rel="edit" href="Responses(1)"/>
    <a:category scheme="http://docs.oasis-open.org/odata/ns/scheme" term="#OData.All41.Response"/>
    <a:content type="application/xml">
    <m:properties>
    <d:Datum>13.09.2018</d:Datum>
    <d:Uhrzeit>14:22:00</d:Uhrzeit>
    <d:ErrorCode>200</d:ErrorCode>
    <d:UstId_1>'DE199226238'</d:UstId_1>
    <d:UstId_2>FR82542065479</d:UstId_2>
    <d:Firmenname>PSA AUTOMOBILES A</d:Firmenname>
    <d:Ort>POISSY</d:Ort>
    <d:PLZ>78307</d:PLZ>
    <d:Strasse>strasse sample</d:Strasse>
    <d:Erg_Name>A</d:Erg_Name>
    <d:Erg_Ort>A</d:Erg_Ort>
    <d:Erg_PLZ>B</d:Erg_PLZ>
    <d:Erg_Str>C</d:Erg_Str>
    <d:Gueltig_ab>1.11.1.</d:Gueltig_ab>
    <d:Gueltig_bis>22.2.2.</d:Gueltig_bis>
    <d:Druck>nein</d:Druck>
    </m:properties>
    </a:content>
  </a:entry>
</a:feed>
```

### Function / Entity

`http://localhost:8080/DemoService/EvatrService.svc/Responseshttps://cloudservicesods0016633819tria.hanatrial.ondemand.com/DemoService/EvatrService.svc/EvatrVatIDVerificationCall(UstId_1='DE199226238',UstId_2='FR82542065479',Firmenname='PSA%20AUTOMOBILES%20SA',Ort='POISSY',PLZ='78307',Strasse='',Druck='nein')`

The expected result is :

```
<a:entry xmlns:a="http://www.w3.org/2005/Atom" xmlns:m="http://docs.oasis-open.org/odata/ns/metadata" xmlns:d="http://docs.oasis-open.org/odata/ns/data" m:context="$metadata#OData.All41.Response">
  <a:id>Responses(1)</a:id>
  <a:title/>
  <a:summary/>
  <a:updated>2018-09-24T07:07:34Z</a:updated>
  <a:author>
  <a:name/>
  </a:author>
  <a:link rel="edit" href="Responses(1)"/>
  <a:category scheme="http://docs.oasis-open.org/odata/ns/scheme" term="#OData.All  41.Response"/>
  <a:content type="application/xml">
  <m:properties>
  <d:Datum>24.09.2018</d:Datum>
  <d:Uhrzeit>09:07:33</d:Uhrzeit>
  <d:ErrorCode>200</d:ErrorCode>
  <d:UstId_1>DE199226238</d:UstId_1>
  <d:UstId_2>FR82542065479</d:UstId_2>
  <d:Firmenname/>
  <d:Ort>POISSY</d:Ort>
  <d:PLZ>78307</d:PLZ>
  <d:Strasse>Firmenname</d:Strasse>
  <d:Erg_Name>A</d:Erg_Name>
  <d:Erg_Ort>A</d:Erg_Ort>
  <d:Erg_PLZ>B</d:Erg_PLZ>
  <d:Erg_Str>C</d:Erg_Str>
  <d:Gueltig_ab>Gueltig_bis</d:Gueltig_ab>
  <d:Gueltig_bis/>
  <d:Druck>nein</d:Druck>
  </m:properties>
  </a:content>
</a:entry>
```

This result is converted from the response of Evatr xmlrpc service using __cloud-services-xmlrpc-evatr.jar__ .
