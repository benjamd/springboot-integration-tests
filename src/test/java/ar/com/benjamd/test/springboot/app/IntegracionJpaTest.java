package ar.com.benjamd.test.springboot.app;

import ar.com.benjamd.test.springboot.app.models.Cuenta;
import ar.com.benjamd.test.springboot.app.repository.CuentaRepository;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Tag("IntegracionJPA")
@DataJpaTest
public class IntegracionJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById(){
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Benjamin", cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByIdPersona(){
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Benjamin");
        assertTrue(cuenta.isPresent());
        assertEquals("Benjamin", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByIdPersonaThrowException(){
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Juan");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll(){
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave(){

        //Given
        Cuenta cuentaPepe = new Cuenta(null,"Pepe",new BigDecimal("3000"));
        cuentaRepository.save(cuentaPepe);

        //When
        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        Cuenta cuenta = cuentaRepository.findById(3L).orElseThrow();

        //Then
        assertEquals("Pepe",cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        //assertEquals(3, cuenta.getId());

    }

    @Test
    void testUpdate(){

        //Given
        Cuenta cuentaPepe = new Cuenta(null,"Pepe",new BigDecimal("3000"));
        cuentaRepository.save(cuentaPepe);

        //When
        Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        //Cuenta cuenta = cuentaRepository.findById(3L).orElseThrow();

        //Then
        assertEquals("Pepe",cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        //assertEquals(3, cuenta.getId());

        //When
        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        //Then
        assertEquals("Pepe",cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Pablo", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> {
            //cuentaRepository.findByPersona("Pablo").orElseThrow();
            cuentaRepository.findById(2L).orElseThrow();
        });

        assertEquals(1, cuentaRepository.findAll().size());
    }
}
