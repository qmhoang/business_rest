package business;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by Quang-Minh on 4/18/2016.
 */
public class BusinessServiceTest {
  private BusinessService service;

  @Before
  public void setUp() throws Exception {
    service = new BusinessService();
    service.run(12345, "src/test/resources/50k_businesses.csv");
    RestAssured.baseURI = "http://localhost/";
    RestAssured.port = 12345;
  }

  @After
  public void tearDown() throws Exception {
    service.stop();
  }

  @Test
  public void testGet() throws Exception {
    when()
      .get("/business/6")
    .then()
      .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("name", equalTo("Fritsch and Sons"))
        .body("address", equalTo("428 Hunt Highway"))
        .body("address2", equalTo(""))
        .body("city", equalTo("Stracketon"))
        .body("state", equalTo("VT"))
        .body("zip", equalTo("22863"))
        .body("country", equalTo("US"))
        .body("phone", equalTo("6068112791"))
        .body("website", equalTo("http://www.schmeler.com/"))
        .body("created_at", equalTo("2014-01-14 13:17:54"));
  }

  @Test
  public void getTestMalformedId() throws Exception {
    when()
      .get("/business/1a")
    .then()
      .assertThat()
        .statusCode(400)
        .contentType(ContentType.JSON)
        .body("message", equalTo("java.lang.NumberFormatException: For input string: \"1a\""))
        .body("status", equalTo("400"))
        .body("type", equalTo("error"));

  }

  @Test
  public void testGetAll() throws Exception {
    when()
      .get("/businesses/")
    .then()
      .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("businesses[0].id", equalTo(0))
        .body("businesses", hasSize(50))
        .body("index", equalTo(0))
        .body("entries", equalTo(50));
  }

  @Test
  public void testPagination() throws Exception {
    given()
      .queryParam("index", "50")
    .when()
      .get("/businesses/")
    .then()
      .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("businesses[0].id", equalTo(50))
        .body("businesses", hasSize(50))
        .body("index", equalTo(50))
        .body("entries", equalTo(50));

  }

  @Test
  public void testMalformedPagination() throws Exception {
    given()
      .queryParam("index", "50a")
    .when()
      .get("/businesses/")
    .then()
      .assertThat()
        .statusCode(400)
        .contentType(ContentType.JSON)
        .body("message", equalTo("java.lang.NumberFormatException: For input string: \"50a\""))
        .body("status", equalTo("400"))
        .body("type", equalTo("error"));

  }
}