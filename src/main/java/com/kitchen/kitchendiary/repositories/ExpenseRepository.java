package com.kitchen.kitchendiary.repositories;


import com.kitchen.kitchendiary.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByBusinessIdAndExpenseDateBetween(
            Long businessId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Expense> findAllByBusinessIdAndCategoryIgnoreCaseAndExpenseDateBetween(
            Long businessId,
            String category,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Expense> findByIdAndBusinessId(Long expenseId, Long businessId);

    @Query("""
           select coalesce(sum(e.amount), 0)
           from Expense e
           where e.business.id = :businessId
             and e.expenseDate between :startDate and :endDate
           """)
    BigDecimal sumAmount(@Param("businessId") Long businessId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate);
}