package models;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import jakarta.persistence.*;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Entity
public class CuentaBancaria {
    @Id
    @Column(name = "numeroCuenta", nullable = false, length = 12)
    private String numeroCuenta;

    @Column(length = 20)
    @NotNull
    private Double saldo = 0.0;

    @Column(length = 20)
    @NotNull
    private String tipoCuenta;

    @NotNull
    private Date fechaCreacion;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarioID", referencedColumnName = "id")
    private Usuario usuario;

    @OneToMany(mappedBy = "cuentaBancaria", fetch = FetchType.LAZY)
    private List<Transferencia> transferenciaList = new ArrayList<>();

    public CuentaBancaria() {
    }

    public CuentaBancaria(String tipoCuenta, Usuario usuario) {
        this.saldo = 0.0;
        this.tipoCuenta = tipoCuenta;
        LocalDate localDate = LocalDate.now();
        this.fechaCreacion = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.usuario = usuario;
    }

    @PrePersist
    protected void generateNumeroCuenta() {
        // Generar un número aleatorio de 12 dígitos
        Random random = new Random();

        int numero = random.nextInt(999999999) + 1;
        this.numeroCuenta = String.format("%012d", numero);


    }


    public String getNumeroCuenta() {
        return numeroCuenta;
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

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public List<Transferencia> getTransferenciaList() {
        return transferenciaList;
    }

    public void setTransferenciaList(List<Transferencia> transferenciaList) {
        this.transferenciaList = transferenciaList;
    }
}
