package com.turismea.repository;

import com.turismea.model.Report;
import com.turismea.model.Request;
import com.turismea.model.Tourist;
import com.turismea.model.User;
import com.turismea.model.enumerations.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "SELECT r FROM Request r WHERE r.type = :type")
    List<Request> getRequestByType(@Param("type")RequestType type);


    boolean existsByUserAndType(User user, RequestType type);
}
