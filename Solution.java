package com.example.bfh.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Solution {
    @Id
    @GeneratedValue
    private UUID id;

    private String regNo;

    @Lob
    private String finalQuery;

    private Instant createdAt = Instant.now();

    public Solution() {}

    public Solution(String regNo, String finalQuery) {
        this.regNo = regNo;
        this.finalQuery = finalQuery;
    }

    public UUID getId() { return id; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getFinalQuery() { return finalQuery; }
    public void setFinalQuery(String finalQuery) { this.finalQuery = finalQuery; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
