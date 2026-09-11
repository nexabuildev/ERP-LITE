package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private String concepto;

    @Column(nullable = false)
    private Double importe; // siempre en positivo, el signo lo da "tipo"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipo; // INGRESO o GASTO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPago medioPago; // NORMAL o BIZUM

    @Column(nullable = false)
    private LocalDate fecha;

    // De donde sale/entra el dinero (opcional). Si se indica un metodo de pago
    // con cuenta vinculada, el saldo que se actualiza es el de esa cuenta.
    @ManyToOne
    @JoinColumn(name = "cuenta_bancaria_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private CuentaBancaria cuentaBancaria;

    @ManyToOne
    @JoinColumn(name = "metodo_pago_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private MetodoPago metodoPago;
}
