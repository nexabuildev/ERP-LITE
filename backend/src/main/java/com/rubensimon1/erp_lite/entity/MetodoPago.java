package com.rubensimon1.erp_lite.entity;

import com.rubensimon1.erp_lite.config.CryptoConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "metodos_pago")
@Getter
@Setter
@NoArgsConstructor
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMetodoPago tipo;

    @Column(nullable = false)
    private String alias; // Ej: "Visa personal", "PayPal compras"

    // --- Solo si tipo = TARJETA ---
    private String titular;

    @Convert(converter = CryptoConverter.class)
    private String numero;

    private String fechaCaducidad; // MM/AA

    @Convert(converter = CryptoConverter.class)
    private String cvv;

    // --- Solo si tipo = PAYPAL ---
    @Convert(converter = CryptoConverter.class)
    private String emailPaypal;

    // Cuenta bancaria a la que pertenece la tarjeta (opcional)
    @ManyToOne
    @JoinColumn(name = "cuenta_vinculada_id")
    private CuentaBancaria cuentaVinculada;
}
