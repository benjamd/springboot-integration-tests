package ar.com.benjamd.test.springboot.app.controllers;

import ar.com.benjamd.test.springboot.app.models.Cuenta;
import ar.com.benjamd.test.springboot.app.models.TransaccionDTO;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@Tag("IntegracionWebClient")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerWebClientTest {

    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }


    @Test
    @Order(1)
    void transferirTest() throws JsonProcessingException {
        //Given
        TransaccionDTO dto = new TransaccionDTO();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "transferencia realizada con exito");
        response.put("transaccion", dto);

        //When
        client.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .consumeWith(respuesta -> {
                    try {
                        String jsonSTR = respuesta.getResponseBody();
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals("transferencia realizada con exito", json.path("mensaje").asText());
                        assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(2L, json.path("transaccion").path("cuentaDestinoId").asLong());
                        assertEquals(1L, json.path("transaccion").path("bancoId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaccion").path("monto").asText());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
        ;
/*                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("transferencia realizada con exito"))
                .jsonPath("$.mensaje").value(valor -> {
                    assertEquals("transferencia realizada con exito", valor);
                })
                .jsonPath("$.mensaje").isEqualTo("transferencia realizada con exito")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.transaccion.cuentaDestinoId").isEqualTo(dto.getCuentaDestinoId())
                .jsonPath("$.transaccion.bancoId").isEqualTo(dto.getBancoId())
                .jsonPath("date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));*/

    }


    @Test
    @Order(2)
    void detalleTest() throws JsonProcessingException {

        Cuenta cuenta = new Cuenta(1L, "Benjamin", new BigDecimal("900"));
        client.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Benjamin")
                .jsonPath("$.saldo").isEqualTo(900)
                .json(objectMapper.writeValueAsString(cuenta));
    }

    @Test
    @Order(3)
    void detalleTest2() {
        client.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                   Cuenta cuenta = response.getResponseBody();
                   assertNotNull(cuenta);
                   assertEquals("Pablo",cuenta.getPersona());
                   assertEquals("2100.00",cuenta.getSaldo().toPlainString());
                });
    }


    @Test
    @Order(4)
    void listarTest() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].persona").isEqualTo("Benjamin")
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].saldo").isEqualTo(900)
                .jsonPath("$[1].persona").isEqualTo("Pablo")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].saldo").isEqualTo(2100)
                .jsonPath( "$").isArray()
                .jsonPath("$").value(hasSize(2));

    }

    @Test
    @Order(5)
    void listarTest2() {
        client.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response -> {
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2, cuentas.size());
                    assertEquals(1L, cuentas.get(0).getId());
                    assertEquals("Benjamin", cuentas.get(0).getPersona());
                    assertEquals("900.0", cuentas.get(0).getSaldo().toPlainString());
                    assertEquals(2L, cuentas.get(1).getId());
                    assertEquals("Pablo", cuentas.get(1).getPersona());
                    assertEquals("2100.0", cuentas.get(1).getSaldo().toPlainString());
                })
                .hasSize(2)
                .value(hasSize(2));

    }

    @Test
    @Order(6)
    void guardarTest() {
        //Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        //When
        client.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
        //Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.persona").isEqualTo("Pepe")
                .jsonPath("$.saldo").isEqualTo(3000);
    }

    @Test
    @Order(7)
    void guardarTest2() {
        //Given
        Cuenta cuenta = new Cuenta(null, "Pepa", new BigDecimal("3500"));

        //When
        client.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                //Then
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta c = response.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4L, c.getId());
                    assertEquals("Pepa", c.getPersona().toString());
                    assertEquals("3500", c.getSaldo().toPlainString());

                });
    }

    @Test
    @Order(8)
    void eliminarTest() {
        client.get().uri("/api/cuentas/").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        client.delete().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

       client.get().uri("/api/cuentas/").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        client.get().uri("/api/cuentas/3").exchange()
//                .expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

    }
}