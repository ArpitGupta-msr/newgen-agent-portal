package com.newgen.agent.repository;

import com.newgen.agent.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByAgencyCode(String agencyCode);

    boolean existsByAgencyCode(String agencyCode);
}
