package ar.com.benjamd.test.springboot.app;

import ar.com.benjamd.test.springboot.app.exceptions.DineroInsuficienteException;
import ar.com.benjamd.test.springboot.app.models.Banco;
import ar.com.benjamd.test.springboot.app.models.Cuenta;
import ar.com.benjamd.test.springboot.app.repository.BancoRepository;
import ar.com.benjamd.test.springboot.app.repository.CuentaRepository;
import ar.com.benjamd.test.springboot.app.services.CuentaService;
import ar.com.benjamd.test.springboot.app.services.CuentaServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static ar.com.benjamd.test.springboot.app.Datos.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

	/*
	@Mock
	CuentaRepository cuentaRepository;
	@Mock
	BancoRepository bancoRepository;

	//InyectMocks debe inyectar a la clase concreta que tiene el constructor, no a la inerfaz
	@InjectMocks
	CuentaServiceImpl service;
	//CuentaService service;
	 */

	//Inyeccion como componente springframework.boot.test.mock.mockito
	@MockBean
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;

	//inyección desde el framework, se debe anotar @Service en la clase concreta CuentaServiceImpl
	//Se puede inyectar tanto la clase concreta como su interfaz
	@Autowired
	CuentaService service;


	@BeforeEach
	void setUp() {
		/*
			Se instancian así el mock y service en caso no inyectar con annotations
			cuentaRepository =  mock(CuentaRepository.class);
			bancoRepository = mock(BancoRepository.class);
			service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
		*/
		/*Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
		Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
		Datos.BANCO.setTotalTransferencias(0);
		 */
	}

	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		service.transferir(1L,2L,new BigDecimal("100"), 1L);
		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);
		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencias(1L);
		assertEquals(1, total);
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);
		verify(cuentaRepository,times(2)).save(any(Cuenta.class));

		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, never()).findAll();
		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(bancoRepository,never()).findAll();


	}


	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());


		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, () -> {
			service.transferir(1L,2L,new BigDecimal("1200"), 1L);
		});

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);
		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencias(1L);
		assertEquals(0, total);
		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);
		verify(cuentaRepository,never()).save(any(Cuenta.class));

		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));


		verify(cuentaRepository, never()).findAll();
		verify(cuentaRepository, times(5)).findById(anyLong());
		verify(bancoRepository,never()).findAll();


	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());

		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);

		assertSame(cuenta1,cuenta2);
		assertTrue(cuenta1 == cuenta2);
		assertEquals("Benjamin", cuenta1.getPersona());
		assertEquals("Benjamin", cuenta2.getPersona());

		verify(cuentaRepository, times(2)).findById(1L);

	}

	@Test
	void findAllTest() {
		//Given
		List<Cuenta> datos = Arrays.asList(Datos.crearCuenta001().orElseThrow(),Datos.crearCuenta002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(datos);

		//When
		List<Cuenta> cuentas = service.findAll();

		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(Datos.crearCuenta002().orElseThrow()));

		verify(cuentaRepository).findAll();

		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(crearCuenta002().orElseThrow()));

		verify(cuentaRepository).findAll();
	}

	@Test
	void saveTest() {
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then( invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return  c;
		});

		Cuenta cuenta = service.save(cuentaPepe);

		assertEquals("Pepe", cuenta.getPersona());
		assertEquals(3L, cuenta.getId());
		assertEquals("3000", cuenta.getSaldo().toPlainString());

		verify(cuentaRepository).save(any());

	}



}


