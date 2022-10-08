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
    static private String access_token;
    static private String account_id;
    static private String uuid;

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

        String accesstoken =jsonResponse.get("access_token");
        System.out.println("Token en funcion: " + accesstoken);
        access_token = accesstoken;

        //Otras variables
        String accountID =jsonResponse.get("account.account_id");
        System.out.println("account id en funcion: " + accountID);
        account_id = accountID;

        //Asignar la variable uuid
        String uid = jsonResponse.get("account.uuid");
        System.out.println("uuid en funcion: " + uid);
        uuid = uid;


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

    @Test
    @Order(4)
    public void post_CrearUnaDireccion_201(){

        String token = obtener_Token();
        System.out.println("Token: " + token);

        System.out.println("Token de funcion: " + access_token);
        System.out.println("account id de funcion: " + account_id);
        System.out.println("uuid de funcion: " + uuid);


        //ejecucion
        RestAssured.baseURI=String.format("https://%s/addresses/v1/create",url_base);

        Response response = given()
                .log().all()
                .formParam("contact","Agente de ventas")
                .formParam("phone","8776655443")
                .formParam("rfc","CAPL800101")
                .formParam("zipCode","45999")
                .formParam("exteriorInfo","Miguel Hidalgo 4232")
                .formParam("interiorInfo","2")
                .formParam("region","11")
                .formParam("municipality","300")
                .formParam("area","8094")
                .formParam("alias","La oficina")
                .header("Content-type","application/x-www-form-urlencoded")
                .header("Accept","application/json, text/plain, */*")
                .post();



        /*assertEquals(email,jsonResponse.get("account.email"));
        assertEquals("tag:scmcoord.com,2013:api", jsonResponse.get("token_type"));
        //Validar que el contenido de datos del token
        assertTrue(access_token.matches("[A-Za-z0-9-_]+"));
        assertTrue(headers_response.contains("Content-Type"));
        */




    }

}
