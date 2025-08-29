package com.example.bfh.repo;

import com.example.bfh.model.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SolutionRepository extends JpaRepository<Solution, UUID> { }
