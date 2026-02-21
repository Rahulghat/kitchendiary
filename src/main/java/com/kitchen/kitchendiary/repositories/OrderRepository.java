package com.kitchen.kitchendiary.repositories;

import com.kitchen.kitchendiary.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByBusinessIdAndOrderDateBetween(
            Long businessId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Order> findAllByBusinessIdAndPlatformIdAndOrderDateBetween(
            Long businessId,
            Long platformId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Order> findByIdAndBusinessId(Long orderId, Long businessId);

    // ---- aggregates for dashboards ----

    @Query("""
           select coalesce(sum(o.grossAmount), 0)
           from Order o
           where o.business.id = :businessId
             and o.orderDate between :startDate and :endDate
           """)
    BigDecimal sumGrossAmount(@Param("businessId") Long businessId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    @Query("""
           select coalesce(sum(o.commissionAmount), 0)
           from Order o
           where o.business.id = :businessId
             and o.orderDate between :startDate and :endDate
           """)
    BigDecimal sumCommissionAmount(@Param("businessId") Long businessId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("""
           select coalesce(sum(o.gstOnCommission), 0)
           from Order o
           where o.business.id = :businessId
             and o.orderDate between :startDate and :endDate
           """)
    BigDecimal sumGstOnCommission(@Param("businessId") Long businessId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    @Query("""
           select coalesce(sum(o.netExpected), 0)
           from Order o
           where o.business.id = :businessId
             and o.orderDate between :startDate and :endDate
           """)
    BigDecimal sumNetExpected(@Param("businessId") Long businessId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    @Query("""
           select coalesce(sum(o.netReceived), 0)
           from Order o
           where o.business.id = :businessId
             and o.orderDate between :startDate and :endDate
           """)
    BigDecimal sumNetReceived(@Param("businessId") Long businessId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    @Query("""
           select coalesce(sum(o.mismatchAmount), 0)
           from Order o
           where o.business.id = :businessId
             and o.orderDate between :startDate and :endDate
           """)
    BigDecimal sumMismatchAmount(@Param("businessId") Long businessId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);
}