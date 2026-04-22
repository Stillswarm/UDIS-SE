package com.udis.model;

import java.time.LocalDate;

public class ResearchProject {
    private int projectId;
    private String title;
    private String pi;
    private String fundingSource;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public ResearchProject() { }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPi() { return pi; }
    public void setPi(String pi) { this.pi = pi; }
    public String getFundingSource() { return fundingSource; }
    public void setFundingSource(String fundingSource) { this.fundingSource = fundingSource; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
