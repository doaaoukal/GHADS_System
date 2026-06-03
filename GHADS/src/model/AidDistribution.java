/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

public class AidDistribution {
    private int distributionId;
    private int familyId;
    private int orgId;
    private int distributedBy;
    private LocalDate distributionDate;
    private String aidType; // Bonus

    // For display purposes
    private String familyName;
    private String orgName;
    private String coordinatorName;

    public AidDistribution() {}

    public AidDistribution(int distributionId, int familyId, int orgId,
                           int distributedBy, LocalDate distributionDate, String aidType) {
        this.distributionId = distributionId;
        this.familyId = familyId;
        this.orgId = orgId;
        this.distributedBy = distributedBy;
        this.distributionDate = distributionDate;
        this.aidType = aidType;
    }

    public int getDistributionId() { return distributionId; }
    public void setDistributionId(int distributionId) { this.distributionId = distributionId; }

    public int getFamilyId() { return familyId; }
    public void setFamilyId(int familyId) { this.familyId = familyId; }

    public int getOrgId() { return orgId; }
    public void setOrgId(int orgId) { this.orgId = orgId; }

    public int getDistributedBy() { return distributedBy; }
    public void setDistributedBy(int distributedBy) { this.distributedBy = distributedBy; }

    public LocalDate getDistributionDate() { return distributionDate; }
    public void setDistributionDate(LocalDate distributionDate) { this.distributionDate = distributionDate; }

    public String getAidType() { return aidType; }
    public void setAidType(String aidType) { this.aidType = aidType; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }

    public String getCoordinatorName() { return coordinatorName; }
    public void setCoordinatorName(String coordinatorName) { this.coordinatorName = coordinatorName; }
}