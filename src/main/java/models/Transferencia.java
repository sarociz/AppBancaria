package models;

import com.sun.istack.NotNull;
import jakarta.persistence.*;

@Entity
public class Transferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(length = 20)
    @NotNull
    private String cuentaDestino;
    @Column(length = 20)
    @NotNull
    private Double valorTransferir;
    @Column(length = 16)
    @NotNull
    private String NombreDestinatario;
    @Column(length = 16)
    @NotNull
    private String ApellidosDestinatario;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "numeroCuenta", referencedColumnName = "numeroCuenta")
    private CuentaBancaria cuentaBancaria;

    //para tranferencias directas
    public Transferencia(String cuentaDestino, Double valorTransferir, String nombreDestinatario, String apellidosDestinatario, CuentaBancaria cuentaBancaria) {
        this.cuentaDestino = cuentaDestino;
        this.valorTransferir = valorTransferir;
        NombreDestinatario = nombreDestinatario;
        ApellidosDestinatario = apellidosDestinatario;
        this.cuentaBancaria = cuentaBancaria;
    }

    //para tranferencias cuenta-cuenta
    public Transferencia(String cuentaDestino, Double valorTransferir, CuentaBancaria cuentaBancaria) {
        this.cuentaDestino = cuentaDestino;
        this.valorTransferir = valorTransferir;
        this.cuentaBancaria = cuentaBancaria;
    }

    public Transferencia() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public Double getValorTransferir() {
        return valorTransferir;
    }

    public void setValorTransferir(Double valorTransferir) {
        this.valorTransferir = valorTransferir;
    }

    public String getNombreDestinatario() {
        return NombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        NombreDestinatario = nombreDestinatario;
    }

    public String getApellidosDestinatario() {
        return ApellidosDestinatario;
    }

    public void setApellidosDestinatario(String apellidosDestinatario) {
        ApellidosDestinatario = apellidosDestinatario;
    }

    public CuentaBancaria getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(CuentaBancaria cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }
}
