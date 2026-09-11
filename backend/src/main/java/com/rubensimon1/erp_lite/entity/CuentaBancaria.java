package com.rubensimon1.erp_lite.entity;

import com.rubensimon1.erp_lite.config.CryptoConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cuentas_bancarias")
@Getter
@Setter
@NoArgsConstructor
public class CuentaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private String alias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaCuenta categoria; // BANCO o EFECTIVO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCuenta tipoCuenta; // PRINCIPAL, SECUNDARIA u OTRA

    // Solo aplica si categoria = BANCO
    @Convert(converter = CryptoConverter.class)
    private String iban;

    private String banco;

    @Column(nullable = false)
    private Double saldoActual;
}
