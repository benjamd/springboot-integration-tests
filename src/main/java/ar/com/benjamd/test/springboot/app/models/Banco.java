package ar.com.benjamd.test.springboot.app.models;

import javax.persistence.*;
import java.util.Objects;
@Entity
@Table(name = "bancos")
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre")
    private String banco;
    @Column(name = "total_transferencias")
    private int totalTransferencias;

    public Banco() {
    }

    public Banco(Long id, String banco, int totalTransferencias) {
        this.id = id;
        this.banco = banco;
        this.totalTransferencias = totalTransferencias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public int getTotalTransferencias() {
        return totalTransferencias;
    }

    public void setTotalTransferencias(int totalTransferencias) {
        this.totalTransferencias = totalTransferencias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banco banco1 = (Banco) o;
        return Objects.equals(id, banco1.id) && Objects.equals(banco, banco1.banco) && Objects.equals(totalTransferencias, banco1.totalTransferencias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, banco, totalTransferencias);
    }
}
