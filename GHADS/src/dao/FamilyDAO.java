/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Family;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FamilyDAO {

    private Connection conn = DBConnection.getInstance().getConnection();

    public boolean addFamily(Family family) {
        String sql = "INSERT INTO families (household_name, phone, location, family_size, national_id, vulnerability_level, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, family.getHouseholdName());
            ps.setString(2, family.getPhone());
            ps.setString(3, family.getLocation());
            ps.setInt(4, family.getFamilySize());
            ps.setString(5, family.getNationalId());
            ps.setString(6, family.getVulnerabilityLevel());
            ps.setDate(7, Date.valueOf(family.getRegistrationDate()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addFamily error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFamily(Family family) {
        String sql = "UPDATE families SET household_name=?, phone=?, location=?, family_size=?, national_id=?, vulnerability_level=? WHERE family_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, family.getHouseholdName());
            ps.setString(2, family.getPhone());
            ps.setString(3, family.getLocation());
            ps.setInt(4, family.getFamilySize());
            ps.setString(5, family.getNationalId());
            ps.setString(6, family.getVulnerabilityLevel());
            ps.setInt(7, family.getFamilyId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateFamily error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFamily(int familyId) {
        String sql = "DELETE FROM families WHERE family_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, familyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteFamily error: " + e.getMessage());
            return false;
        }
    }

    public List<Family> getAllFamilies() {
        List<Family> list = new ArrayList<>();
        String sql = "SELECT * FROM families";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapFamily(rs));
        } catch (SQLException e) {
            System.err.println("getAllFamilies error: " + e.getMessage());
        }
        return list;
    }

    public Family getFamilyByNationalId(String nationalId) {
        String sql = "SELECT * FROM families WHERE national_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nationalId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapFamily(rs);
        } catch (SQLException e) {
            System.err.println("getFamilyByNationalId error: " + e.getMessage());
        }
        return null;
    }

    // الأسر اللي ما أخذت مساعدة خالص
    public List<Family> getFamiliesNotServed() {
        List<Family> list = new ArrayList<>();
        String sql = "SELECT * FROM families WHERE family_id NOT IN (SELECT DISTINCT family_id FROM aid_distribution)";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapFamily(rs));
        } catch (SQLException e) {
            System.err.println("getFamiliesNotServed error: " + e.getMessage());
        }
        return list;
    }

    // الأسر مرتبة حسب الـ vulnerability (HIGH أولاً)
    public List<Family> getFamiliesByVulnerability() {
        List<Family> list = new ArrayList<>();
        String sql = """
            SELECT * FROM families
            ORDER BY FIELD(vulnerability_level, 'HIGH', 'MEDIUM', 'LOW')
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapFamily(rs));
        } catch (SQLException e) {
            System.err.println("getFamiliesByVulnerability error: " + e.getMessage());
        }
        return list;
    }

    public int getTotalFamilies() {
        String sql = "SELECT COUNT(*) FROM families";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("getTotalFamilies error: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalFamiliesServed() {
        String sql = "SELECT COUNT(DISTINCT family_id) FROM aid_distribution";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("getTotalFamiliesServed error: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalFamiliesServedByOrg(int orgId) {
        String sql = "SELECT COUNT(DISTINCT family_id) FROM aid_distribution WHERE org_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orgId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("getTotalFamiliesServedByOrg error: " + e.getMessage());
        }
        return 0;
    }

    public boolean updateLastAidDate(int familyId, Date date) {
        String sql = "UPDATE families SET last_aid_date=? WHERE family_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            ps.setInt(2, familyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateLastAidDate error: " + e.getMessage());
            return false;
        }
    }

    private Family mapFamily(ResultSet rs) throws SQLException {
        return new Family(
            rs.getInt("family_id"),
            rs.getString("household_name"),
            rs.getString("phone"),
            rs.getString("location"),
            rs.getInt("family_size"),
            rs.getString("national_id"),
            rs.getString("vulnerability_level"),
            rs.getDate("registration_date") != null ?
                rs.getDate("registration_date").toLocalDate() : null,
            rs.getDate("last_aid_date") != null ?
                rs.getDate("last_aid_date").toLocalDate() : null
        );
    }
}