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
