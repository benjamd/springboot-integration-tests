package ar.com.benjamd.test.springboot.app.models;

import java.math.BigDecimal;
import java.util.Objects;

public class TransaccionDTO {

    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;

    private Long bancoId;

    public Long getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public void setCuentaOrigenId(Long cuentaOrigenId) {
        this.cuentaOrigenId = cuentaOrigenId;
    }

    public Long getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public void setCuentaDestinoId(Long cuentaDestinoId) {
        this.cuentaDestinoId = cuentaDestinoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Long getBancoId() {
        return bancoId;
    }

    public void setBancoId(Long bancoId) {
        this.bancoId = bancoId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransaccionDTO that = (TransaccionDTO) o;
        return Objects.equals(cuentaOrigenId, that.cuentaOrigenId) && Objects.equals(cuentaDestinoId, that.cuentaDestinoId) && Objects.equals(monto, that.monto) && Objects.equals(bancoId, that.bancoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cuentaOrigenId, cuentaDestinoId, monto, bancoId);
    }
}
