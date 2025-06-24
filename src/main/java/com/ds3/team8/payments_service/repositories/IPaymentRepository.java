package com.ds3.team8.payments_service.repositories;

import com.ds3.team8.payments_service.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndIsActiveTrue(Long id); // Obtener pago por ID y activo
    List<Payment> findAllByCustomerIdAndIsActiveTrue(Long customerId); // Obtener pagos por ID de cliente y activos
    Page<Payment> findAllByCustomerIdAndIsActiveTrue(Long customerId, Pageable pageable); // Obtener pagos por ID de cliente y activos con paginación
    List<Payment> findAllByIsActiveTrue(); // Obtener todos los pagos activos
    Page<Payment> findAllByIsActiveTrue(Pageable pageable); // Obtener todos los pagos activos con paginación
    Boolean existsByChargeIdAndIsActiveTrue(String chargeId); // Verificar si existe un pago por ID de cargo y activo
}
