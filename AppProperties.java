package com.example.bfh.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotBlank
    private String name;

    @NotBlank
    private String regNo;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String generateEndpoint;

    @NotBlank
    private String submitEndpoint;

    private boolean dryRun = false;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getGenerateEndpoint() { return generateEndpoint; }
    public void setGenerateEndpoint(String generateEndpoint) { this.generateEndpoint = generateEndpoint; }

    public String getSubmitEndpoint() { return submitEndpoint; }
    public void setSubmitEndpoint(String submitEndpoint) { this.submitEndpoint = submitEndpoint; }

    public boolean isDryRun() { return dryRun; }
    public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }
}
