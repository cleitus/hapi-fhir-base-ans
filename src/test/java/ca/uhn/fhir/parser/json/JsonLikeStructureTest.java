package ca.uhn.fhir.parser.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.*;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Ignore;
import org.junit.Test;

public class JsonLikeStructureTest {
//	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(JsonLikeStructureTest.class);

	private static final String TEST_STRUCTURELOADING_DATA = 
		"{" +
		"    \"resourceType\":\"Organization\"," +
		"    \"id\":\"11111\"," +
		"    \"active\":null," +
		"    \"meta\":{" +
		"        \"lastUpdated\":\"3900-09-20T10:10:10.000-07:00\"" +
		"    }," +
		"    \"identifier\":[" +
		"        {" +
		"            \"value\":\"15250\"" +
		"        }" +
		"    ]," +
		"    \"type\":{" +
		"        \"coding\":[" +
		"            {" +
		"                \"system\":\"http://test\"," +
		"                \"code\":\"ins\"," +
		"                \"display\":\"General Ledger System\"," +
		"                \"userSelected\":false" +
		"            }" +
		"        ]" +
		"    }," +
		"    \"name\":\"Acme Investments\"" +
		"}";

	@Test
	@Ignore
	public void testNulls() {
		final FhirContext ctx = FhirContext.forR4();

		String path = "src/test/resources/";

		File file = new File(path + "testPM.json");

		String nextFileContents = null;
		try {
			nextFileContents = IOUtils.toString(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Parse JSON file
		IBaseResource parsedResource = ctx.newJsonParser().parseResource(nextFileContents);

		String encoded = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(parsedResource);

		System.out.println(encoded);
	}

	@Test
	public void testStructureLoading() {
		StringReader reader = new StringReader(TEST_STRUCTURELOADING_DATA);
		JsonLikeStructure jsonStructure = new GsonStructure();
		jsonStructure.load(reader);
		
		JsonLikeObject rootObject = jsonStructure.getRootObject();

		assertNotNull(rootObject);

		assertEquals(JsonLikeValue.ValueType.OBJECT, rootObject.getJsonType());
	}

	private static final String TEST_JSONTYPES_DATA = 
			"{" +
			"    \"scalar-string\":\"A scalar string\"," +
			"    \"scalar-number\":11111," +
			"    \"scalar-boolean\":true," +
			"    \"null-value\":null," +
			"    \"object-value\":{" +
			"        \"lastUpdated\":\"3900-09-20T10:10:10.000-07:00\"," +
			"        \"deleted\":\"3909-09-20T10:10:10.000-07:00\"" +
			"    }," +
			"    \"array-value\":[" +
			"        12345," +
			"        {" +
			"            \"value\":\"15250\"" +
			"        }" +
			"    ]" +
			"}";


	@Test
	public void testJsonAndDataTypes() {
		StringReader reader = new StringReader(TEST_JSONTYPES_DATA);
		JsonLikeStructure jsonStructure = new GsonStructure();
		jsonStructure.load(reader);
		
		JsonLikeObject rootObject = jsonStructure.getRootObject();
		
		assertNotNull(rootObject);
		
		JsonLikeValue value = rootObject.get("object-value");
		assertNotNull(value);
		assertEquals(JsonLikeValue.ValueType.OBJECT, value.getJsonType());
		assertEquals(true, value.isObject());
		assertEquals(false, value.isArray());
		assertEquals(false, value.isScalar());
		assertEquals(false, value.isNull());

		JsonLikeObject obj = value.getAsObject();
		assertNotNull(obj);
		assertEquals(JsonLikeValue.ValueType.OBJECT, obj.getJsonType());
		assertEquals(true, obj.isObject());
		assertEquals(false, obj.isArray());
		assertEquals(false, obj.isScalar());
		assertEquals(false, obj.isNull());
		
		value = rootObject.get("array-value");
		assertNotNull(value);
		assertEquals(JsonLikeValue.ValueType.ARRAY, value.getJsonType());
		assertEquals(false, value.isObject());
		assertEquals(true, value.isArray());
		assertEquals(false, value.isScalar());
		assertEquals(false, value.isNull());

		JsonLikeArray array = value.getAsArray();
		assertNotNull(array);
		assertEquals(JsonLikeValue.ValueType.ARRAY, array.getJsonType());
		assertEquals(false, array.isObject());
		assertEquals(true, array.isArray());
		assertEquals(false, array.isScalar());
		assertEquals(false, array.isNull());

		value = rootObject.get("null-value");
		assertNotNull(value);
		assertEquals(JsonLikeValue.ValueType.NULL, value.getJsonType());
		assertEquals(false, value.isObject());
		assertEquals(false, value.isArray());
		assertEquals(false, value.isScalar());
		assertEquals(true, value.isNull());

		value = rootObject.get("scalar-string");
		assertNotNull(value);
		assertEquals(JsonLikeValue.ValueType.SCALAR, value.getJsonType());
		assertEquals(false, value.isObject());
		assertEquals(false, value.isArray());
		assertEquals(true, value.isScalar());
		assertEquals(false, value.isNull());
		assertEquals(JsonLikeValue.ScalarType.STRING, value.getDataType());
		assertEquals(value.getAsString(), "A scalar string");

		value = rootObject.get("scalar-number");
		assertNotNull(value);
		assertEquals(JsonLikeValue.ValueType.SCALAR, value.getJsonType());
		assertEquals(JsonLikeValue.ScalarType.NUMBER, value.getDataType());
		assertEquals(value.getAsString(), "11111");

		value = rootObject.get("scalar-boolean");
		assertNotNull(value);
		assertEquals(JsonLikeValue.ValueType.SCALAR, value.getJsonType());
		assertEquals(JsonLikeValue.ScalarType.BOOLEAN, value.getDataType());
		assertEquals(value.getAsString(), "true");
	}

}
