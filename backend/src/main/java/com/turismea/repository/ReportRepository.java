package com.turismea.repository;

import com.turismea.model.Admin;
import com.turismea.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query(value = "SELECT r FROM Report r WHERE r.admin.id = :admin_id")
    List<Report> getByAdmin(@Param("admin_id") Long id);
}
