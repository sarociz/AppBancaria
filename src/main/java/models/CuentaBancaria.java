package models;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import jakarta.persistence.*;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
@Entity
public class CuentaBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 16)
    @NotNull
    private String numeroCuenta;

    @Column(length = 20)
    @NotNull
    private Double saldo;

    @Column(length = 20)
    @NotNull
    private String tipoCuenta;

    @NotNull
    private Date fechaCreacion;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarioID", referencedColumnName = "id")
    private Usuario usuario;

    public CuentaBancaria() {
    }

    public CuentaBancaria(String numeroCuenta, Double saldo, String tipoCuenta, Date fechaCreacion, Usuario usuario) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.tipoCuenta = tipoCuenta;
        LocalDate localDate = LocalDate.now();
        this.fechaCreacion = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
