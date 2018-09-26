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
