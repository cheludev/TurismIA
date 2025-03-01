package com.turismea.repository;

import com.turismea.model.Admin;
import com.turismea.model.Report;
import com.turismea.model.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Query(value = "SELECT a.reportList FROM Admin a WHERE a.id = :id")
    public List<Report> getReportList(@Param("id") Long id);
}
