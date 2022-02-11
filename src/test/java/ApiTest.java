import io.restassured.http.ContentType;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import pojo.CreateUserRequest;
import pojo.CreateUserResponse;
import pojo.UserData;
import registration.Registration;
import registration.SuccessfulUserRegistration;
import registration.UnsuccessfulUserRegistration;
import specification.Specification;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

@Data
public class ApiTest {

    private final static String URL = "https://reqres.in";

    @Test
    public void getUsersTest() {
        List<UserData> users = given()
                .spec(Specification.REQUEST_SPECIFICATION)
                .get()
                .jsonPath().getList("data", UserData.class);

        assertThat(users).extracting(UserData::getFirstName).contains("Eve");
    }

    @Test
    public void checkFirstUserEmailTest() {
        given()
                .spec(Specification.REQUEST_SPECIFICATION)
                .when().get()
                .then()
                .statusCode(200)
                .body("data[0].email", equalTo("george.bluth@reqres.in"));
    }

    @Test
    public void getResponseBodyTest() {
        given()
                .spec(Specification.REQUEST_SPECIFICATION)
                .when().get()
                .then().log()
                .all();

        given().when().get(URL).then().assertThat().statusCode(200);
    }

    @Test
    public void getResponseTimeTest() {
        System.out.println("The time taken to the response " +
                given()
                        .spec(Specification.REQUEST_SPECIFICATION)
                        .when().get(URL)
                        .getTimeIn(TimeUnit.MILLISECONDS));
    }

    @Test
    public void createUserTest() {
        CreateUserRequest request = CreateUserRequest.builder()
                .name("morpheus")
                .job("leader")
                .build();

        CreateUserResponse response = (CreateUserResponse) given()
                .spec(Specification.REQUEST_SPECIFICATION)
                .body(request)
                .when().post()
                .then().assertThat().statusCode(201)
                .extract().as(CreateUserResponse.class);

        assertThat(response)
                .isNotNull()
                .extracting(CreateUserResponse::getName)
                .isEqualTo(request.getName());
    }

    @Test
    public void deleteUserTest() {
        Specification.responseSpecification(204);
        given()
                .spec(Specification.REQUEST_SPECIFICATION)
                .when().delete("/api/users/2")
                .then()
                .log().all();
    }

    @Test
    public void updateUserTest() {
        CreateUserRequest user = new CreateUserRequest("morpheus", "zion resident");
        CreateUserResponse response = given()
                .spec(Specification.REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .put("/api/users/2")
                .then().log().all()
                .extract().as(CreateUserResponse.class);

        assertThat(Specification.responseSpecification(200));
    }

    @Test
    public void successfulUserRegistrationTest() {
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Registration user = new Registration("eve.holt@reqres.in", "pistol");
        SuccessfulUserRegistration successUserReg = given()
                .spec(Specification.REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post("api/register")
                .then()
                .log().all()
                .extract().as(SuccessfulUserRegistration.class);

        assertThat(successUserReg.getId()).isNotNull();
    }

    @Test
    public void unsuccessfulUserRegistrationTest() {
        Registration unsuccessfulUser = new Registration("sydney@fife", "");
        UnsuccessfulUserRegistration unsuccessfulUserRegistration = given()
                .baseUri("https://reqres.in/api")
                .contentType(ContentType.JSON)
                .body(unsuccessfulUser)
                .when()
                .post("/register")
                .then().assertThat().statusCode(400)
                .log().body()
                .extract().as(UnsuccessfulUserRegistration.class);
        assertThat(unsuccessfulUserRegistration.getError()).isNotNull();
        Assert.assertEquals("Missing password", unsuccessfulUserRegistration.getError());
    }

    @Test
    public void checkAvatarContainsIdTest() {
        List<UserData> users = given()
                .spec(Specification.REQUEST_SPECIFICATION)
                .when()
                .get("?page=2")
                .then().assertThat().statusCode(200)
                .log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        Assert.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));
    }
}
