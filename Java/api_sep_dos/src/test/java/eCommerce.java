import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Name;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class eCommerce {
    //Variables
    static private String url_base = "webapi.segundamano.mx";
    static private String email = "test2022_agente@mailinator.com";
    static private String password = "54321";

    @Name("Obtener Token")
    private String obtener_Token(){

        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .auth().preemptive().basic(email,password)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Cambiar el body a JSon
        JsonPath jsonResponse = response.jsonPath();

        String access_token =jsonResponse.get("access_token");
        System.out.println("Token " + access_token);

        //Otras variables
        String accountID =jsonResponse.get("account.account_id");
        System.out.println(accountID);

        return access_token;

    }

    @Test
    @Order(1)
    public void get_obtenerCategorias_200(){
        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/public/categories/filter?lang=es",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .get();

        //Imprimir el body response
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Pruebas
        //al menos 5 pruebas

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("id"));
        assertTrue(body_response.contains("categories"));
        assertTrue(body_response.contains("all_label"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 1800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        //System.out.println("Las cabeceras: " + headers_response);
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(2)
    public void post_crearUsuario_401(){

        //Crear Usuario
        String new_user = "agente_ventas" + (Math.floor(Math.random()*987)) + "@mailinator.com";
        //String password = "12345";
        String bodyRequest = "{\"account\":{\"email\":\""+new_user+"\"}}";

        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/private/accounts?lang=es",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .contentType("application/json")
                .auth().preemptive().basic(new_user,password)
                .body(bodyRequest)
                .post();

        //Imprimir el body response
        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Pruebas
        //al menos 5 pruebas

        //Validar el status response
        assertEquals(401,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("error"));
        assertTrue(body_response.contains("code"));
        assertTrue(body_response.contains("ACCOUNT_VERIFICATION_REQUIRED"));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 1800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();
        //System.out.println("Las cabeceras: " + headers_response);
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(3)
    public void post_ObtenerUsuario_200(){
        //Configurar
        String bodyRequest = "{\"account\":{\"email\":\""+email+"\"}}";

        //ejecucion
        RestAssured.baseURI=String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .contentType("application/json")
                .auth().preemptive().basic(email,password)
                .body(bodyRequest)
                .post();

        String body_response = response.getBody().asString();
        System.out.println("Body response: " + body_response );

        //Pruebas
        //al menos 5 pruebas

        //Validar el status response
        assertEquals(200,response.getStatusCode());

        //Validar que nuestro body no este vacio
        assertNotNull(body_response);

        //Validar que el body contenga la palabra ID
        assertTrue(body_response.contains("access_token"));
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("test2022_agente@mailinator.com"));
        assertTrue(body_response.contains(email));

        //Validar el tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 1800);

        //Validar los headers
        String headers_response = response.getHeaders().toString();

        assertTrue(headers_response.contains("application/json"));

    }

}
