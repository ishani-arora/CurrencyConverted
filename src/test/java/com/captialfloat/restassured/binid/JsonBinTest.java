package com.captialfloat.restassured.binid;

import java.util.LinkedHashMap;

import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class JsonBinTest {

	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String BASE_URL = "https://api.jsonbin.io";
	private static final String SECRET_KEY = "secret-key";
	private static final String SECRET_KEY_VALUE = "$2b$10$z8x9Mw3m2EwdVql63LzDZ.4lO7BeESuUalap4AnEtVUc7E2y8Ejxa";
	private String binId = "";

	@Test(priority = 1)
	public void createDataTest() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		JSONObject request = new JSONObject();
		request.put("sample", "Hello World");
		httpRequest.header(CONTENT_TYPE, APPLICATION_JSON);
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		httpRequest.body(request.toJSONString());
		Response response = httpRequest.request(Method.POST, "/b");
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertTrue(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		Assert.assertTrue(Boolean.valueOf(String.valueOf(jsonResponse.get("private"))));
		Assert.assertNotNull(jsonResponse.get("id"));
		LinkedHashMap<String, String> data = jsonResponse.get("data");
		Assert.assertEquals(data.get("sample"), "Hello World");
		binId = jsonResponse.get("id");
	}
@Test(priority=2)
	public void createDataByInvalidSecureKey()
	{
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		JSONObject request = new JSONObject();
		request.put("sample", "Hello World");
		httpRequest.header("secret-key" ,"$2b$10$z8x9Mw3m2EwdVql63LzDZ.4lO7BeESuUalap4AnEtVUc7E2y8Ejx");
		httpRequest.header(CONTENT_TYPE, APPLICATION_JSON);
		httpRequest.body(request.toJSONString());
		Response response = httpRequest.request(Method.POST, "/b");
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertEquals(jsonResponse.get("message"), "Invalid secret key provided.");	
		Assert.assertFalse(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		
	}
@Test(priority =3)
public void createDataWithoutSecretKey()
{
	RestAssured.baseURI = BASE_URL;
	RequestSpecification httpRequest = RestAssured.given();
	JSONObject request = new JSONObject();
	request.put("sample", "Hello World");
	httpRequest.header(CONTENT_TYPE, APPLICATION_JSON);
	httpRequest.body(request.toJSONString());
	Response response = httpRequest.request(Method.POST, "/b");
	JsonPath jsonResponse = response.jsonPath();
	Assert.assertEquals(jsonResponse.get("message"), "You need to pass a secret-key in the header to Create a Bin");	
	Assert.assertFalse(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
}

	@Test(priority = 4)
	public void getDataByIdTest() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		Response response = httpRequest.request(Method.GET, "/b/" + binId);
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertEquals(jsonResponse.get("sample"), "Hello World");

	}

	@Test(priority = 5)
	public void getDataByWrongVesionZeroTest() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		Response response = httpRequest.request(Method.GET, "/b/" + binId + "/0");
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertFalse(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		Assert.assertEquals(jsonResponse.get("message"), "Bin version not found");
	}

	@Test(priority=6)
	public void getDataByWrongBinId()
	{
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		Response response = httpRequest.request(Method.GET, "/b/" +"6026b35587173a3d2f5c347" );
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertEquals(jsonResponse.get("message"), "Invalid Record ID");
	}
	
	@Test(priority = 7)
	public void getDataByNagativeVesionTest() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		Response response = httpRequest.request(Method.GET, "/b/" + binId + "/-1");
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertFalse(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		Assert.assertEquals(jsonResponse.get("message"), "Bin version not found");
	}

	@Test(priority = 8)
	public void updateDataByIdTest() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(CONTENT_TYPE, APPLICATION_JSON);
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		JSONObject request = new JSONObject();
		request.put("sample", "Hello Rest Assured");
		httpRequest.body(request.toJSONString());
		Response response = httpRequest.request(Method.PUT, "/b/" + binId);
		JsonPath jsonResponse = response.jsonPath();
		LinkedHashMap<String, String> data = jsonResponse.get("data");
		Assert.assertEquals(data.get("sample"), "Hello Rest Assured");
		Assert.assertTrue(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		Assert.assertEquals(jsonResponse.get("version"), 1);
		Assert.assertEquals(jsonResponse.get("parentId"), binId);
	}

	@Test(priority = 9)
	public void getDataByVesionOneTest() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		Response response = httpRequest.request(Method.GET, "/b/" + binId + "/1");
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertEquals(jsonResponse.get("sample"), "Hello Rest Assured");
	}

	@Test(priority = 10)
	public void withouScretKeyTest() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(Method.GET, "/b/" + binId);
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertFalse(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		Assert.assertEquals(jsonResponse.get("message"), "Need to provide a secret-key to READ private bins");
	}
	
	@Test(priority=11)
	public void updateDataInvalidBinIdTest()
	{
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(CONTENT_TYPE, APPLICATION_JSON);
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		JSONObject request = new JSONObject();
		request.put("sample", "Hello Rest Assured");
		httpRequest.body(request.toJSONString());
		Response response = httpRequest.request(Method.PUT, "/b/" + binId +"6026b35587173a3d2f5c347");
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertEquals(jsonResponse.get("message"), "Invalid Record ID");
		
	}
	@Test(priority=12)
	public void updateDataWithoutSecureKeyTest()
	{
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(CONTENT_TYPE, APPLICATION_JSON);
		JSONObject request = new JSONObject();
		request.put("sample", "Hello World12");
		httpRequest.body(request.toJSONString());
		Response response = httpRequest.request(Method.PUT,"/b/" + binId );
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertFalse(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		Assert.assertEquals(jsonResponse.get("message"), "Need to provide a secret-key to UPDATE private bins");
		
	}
	
	@Test(priority = 13)
	public void deleteDataByBinIdTest()
	{
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		Response response = httpRequest.request(Method.DELETE,"/b/" + binId );
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertTrue(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		Assert.assertEquals(jsonResponse.get("id"), binId);
		Assert.assertEquals(jsonResponse.get("message"), "Bin " +binId +" is deleted successfully. 1 versions removed.");
		
	}
	
	@Test(priority=14)
	public void deleteDataByInvalidBinIdTest() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(SECRET_KEY, SECRET_KEY_VALUE);
		Response response = httpRequest.request(Method.DELETE,"/b/" + binId );
		JsonPath jsonResponse = response.jsonPath();
		Assert.assertEquals(jsonResponse.get("message"), "Bin not found");
		Assert.assertFalse(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		
	}
	
	@Test(priority=15)
	public void deleteDataWithoutSecretKeyTest()
	{
		RestAssured.baseURI = BASE_URL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header(CONTENT_TYPE, APPLICATION_JSON);
		Response response = httpRequest.request(Method.DELETE,"/b/" + binId );
		JsonPath jsonResponse = response.jsonPath();
		
		Assert.assertFalse(Boolean.valueOf(String.valueOf(jsonResponse.get("success"))));
		Assert.assertEquals(jsonResponse.get("message"), "Need to provide a secret-key to DELETE bins");
		
	}

}
