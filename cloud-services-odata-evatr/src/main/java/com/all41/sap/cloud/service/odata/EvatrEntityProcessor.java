package com.all41.sap.cloud.service.odata;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;

import com.all41.sap.cloud.data.Storage;

public class EvatrEntityProcessor implements EntityProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;
	private Storage storage;

	public EvatrEntityProcessor() {
		// TODO Auto-generated constructor stub
	}

	public EvatrEntityProcessor(Storage storage) {
		this.storage = storage;
	}

	public void init(OData odata, ServiceMetadata serviceMetadata) {
		// TODO Auto-generated method stub
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	private void readFunctionImportInternal(final ODataRequest request, final ODataResponse response,
			  final UriInfo uriInfo, final ContentType responseFormat) throws ODataApplicationException, SerializerException {

			  // 1st step: Analyze the URI and fetch the entity returned by the function import
			  // Function Imports are always the first segment of the resource path
			  final UriResource firstSegment = uriInfo.getUriResourceParts().get(0);

			  if(!(firstSegment instanceof UriResourceFunction)) {
			    throw new ODataApplicationException("Not implemented",
			      HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
			  }

			  final UriResourceFunction uriResourceFunction = (UriResourceFunction) firstSegment;
			  final Entity entity = storage.readFunctionImportEntity(uriResourceFunction, serviceMetadata);

			  if(entity == null) {
			    throw new ODataApplicationException("Nothing found.",
			      HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
			  }

			  // 2nd step: Serialize the response entity
			  final EdmEntityType edmEntityType = (EdmEntityType) uriResourceFunction.getFunction().getReturnType().getType();
			  final ContextURL contextURL = ContextURL.with().type(edmEntityType).build();
			  final EntitySerializerOptions opts = EntitySerializerOptions.with().contextURL(contextURL).build();
			  final ODataSerializer serializer = odata.createSerializer(responseFormat);
			  final SerializerResult serializerResult = serializer.entity(serviceMetadata, edmEntityType, entity, opts);

			  // 3rd configure the response object
			  response.setContent(serializerResult.getContent());
			  response.setStatusCode(HttpStatusCode.OK.getStatusCode());
			  response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
			}	
	
	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
			  throws ODataApplicationException, SerializerException {

			  // The sample service supports only functions imports and entity sets.
			  // We do not care about bound functions and composable functions.

			  UriResource uriResource = uriInfo.getUriResourceParts().get(0);

			  if(uriResource instanceof UriResourceEntitySet) {
			    readEntityInternal(request, response, uriInfo, responseFormat);
			  } else if(uriResource instanceof UriResourceFunction) {
			    readFunctionImportInternal(request, response, uriInfo, responseFormat);
			  } else {
			    throw new ODataApplicationException("Only EntitySet is supported",
			      HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
			  }
			}	
	
	public void readEntityInternal(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
			throws ODataApplicationException, SerializerException {

		// 1. retrieve the Entity Type
		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		// Note: only in our example we can assume that the first segment is the
		// EntitySet
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

		// 2. retrieve the data from backend
		List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
		Entity entity = storage.readEntityData(edmEntitySet, keyPredicates);

		// 3. serialize
		EdmEntityType entityType = edmEntitySet.getEntityType();

		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
		// expand and select currently not supported
		EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();

		ODataSerializer serializer = odata.createSerializer(responseFormat);
		SerializerResult serializerResult = serializer.entity(serviceMetadata, entityType, entity, options);
		InputStream entityStream = serializerResult.getContent();

		// 4. configure the response object
		response.setContent(entityStream);
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

}
