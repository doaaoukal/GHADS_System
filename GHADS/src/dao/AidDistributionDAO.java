/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.AidDistribution;
import util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AidDistributionDAO {

    private Connection conn = DBConnection.getInstance().getConnection();
    private FamilyDAO familyDAO = new FamilyDAO();

    public DuplicateCheckResult checkDuplicate(int familyId, String aidType) {
        var family = familyDAO.getAllFamilies().stream()
                .filter(f -> f.getFamilyId() == familyId)
                .findFirst().orElse(null);

        if (family == null) return new DuplicateCheckResult(false, null);

        // إذا HIGH → مسموح دايماً
        if (family.getVulnerabilityLevel().equals("HIGH")) {
            return new DuplicateCheckResult(false, null);
        }

        // MEDIUM أو LOW → تحقق إذا أخذت نفس النوع خلال 30 يوم
        String sql = """
            SELECT ad.*, o.name as org_name
            FROM aid_distribution ad
            JOIN organizations o ON ad.org_id = o.org_id
            WHERE ad.family_id = ?
            AND ad.aid_type = ?
            AND ad.distribution_date >= ?
            ORDER BY ad.distribution_date DESC
            LIMIT 1
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, familyId);
            ps.setString(2, aidType);
            ps.setDate(3, Date.valueOf(LocalDate.now().minusDays(30)));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AidDistribution existing = new AidDistribution();
                existing.setFamilyId(familyId);
                existing.setAidType(rs.getString("aid_type"));
                existing.setOrgName(rs.getString("org_name"));
                existing.setDistributionDate(rs.getDate("distribution_date").toLocalDate());
                return new DuplicateCheckResult(true, existing);
            }
        } catch (SQLException e) {
            System.err.println("checkDuplicate error: " + e.getMessage());
        }
        return new DuplicateCheckResult(false, null);
    }

    public boolean addDistribution(AidDistribution aid) {
        String sql = "INSERT INTO aid_distribution (family_id, org_id, distributed_by, distribution_date, aid_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, aid.getFamilyId());
            ps.setInt(2, aid.getOrgId());
            ps.setInt(3, aid.getDistributedBy());
            ps.setDate(4, Date.valueOf(aid.getDistributionDate()));
            ps.setString(5, aid.getAidType());
            boolean result = ps.executeUpdate() > 0;
            // تحديث last_aid_date في جدول الأسر
            if (result) {
                familyDAO.updateLastAidDate(aid.getFamilyId(),
                        Date.valueOf(aid.getDistributionDate()));
            }
            return result;
        } catch (SQLException e) {
            System.err.println("addDistribution error: " + e.getMessage());
            return false;
        }
    }

    public List<AidDistribution> getAllDistributions() {
        List<AidDistribution> list = new ArrayList<>();
        String sql = """
            SELECT ad.*, f.household_name as family_name,
                   o.name as org_name, u.full_name as coordinator_name
            FROM aid_distribution ad
            JOIN families f ON ad.family_id = f.family_id
            JOIN organizations o ON ad.org_id = o.org_id
            JOIN users u ON ad.distributed_by = u.user_id
            ORDER BY ad.distribution_date DESC
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapDistribution(rs));
        } catch (SQLException e) {
            System.err.println("getAllDistributions error: " + e.getMessage());
        }
        return list;
    }

    public List<AidDistribution> getDistributionsByOrg(int orgId) {
        List<AidDistribution> list = new ArrayList<>();
        String sql = """
            SELECT ad.*, f.household_name as family_name,
                   o.name as org_name, u.full_name as coordinator_name
            FROM aid_distribution ad
            JOIN families f ON ad.family_id = f.family_id
            JOIN organizations o ON ad.org_id = o.org_id
            JOIN users u ON ad.distributed_by = u.user_id
            WHERE ad.org_id = ?
            ORDER BY ad.distribution_date DESC
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orgId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapDistribution(rs));
        } catch (SQLException e) {
            System.err.println("getDistributionsByOrg error: " + e.getMessage());
        }
        return list;
    }

    private AidDistribution mapDistribution(ResultSet rs) throws SQLException {
        AidDistribution ad = new AidDistribution(
            rs.getInt("distribution_id"),
            rs.getInt("family_id"),
            rs.getInt("org_id"),
            rs.getInt("distributed_by"),
            rs.getDate("distribution_date").toLocalDate(),
            rs.getString("aid_type")
        );
        ad.setFamilyName(rs.getString("family_name"));
        ad.setOrgName(rs.getString("org_name"));
        ad.setCoordinatorName(rs.getString("coordinator_name"));
        return ad;
    }

    // Inner class للـ Duplicate Check Result
    public static class DuplicateCheckResult {
        private final boolean isDuplicate;
        private final AidDistribution existingRecord;

        public DuplicateCheckResult(boolean isDuplicate, AidDistribution existingRecord) {
            this.isDuplicate = isDuplicate;
            this.existingRecord = existingRecord;
        }

        public boolean isDuplicate() { return isDuplicate; }
        public AidDistribution getExistingRecord() { return existingRecord; }
    }
}
