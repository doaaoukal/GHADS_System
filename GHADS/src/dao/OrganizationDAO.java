/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Organization;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDAO {

    private Connection conn = DBConnection.getInstance().getConnection();

    public boolean addOrganization(Organization org) {
        String sql = "INSERT INTO organizations (name, type, contact_info) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, org.getName());
            ps.setString(2, org.getType());
            ps.setString(3, org.getContactInfo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("addOrganization error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateOrganization(Organization org) {
        String sql = "UPDATE organizations SET name=?, type=?, contact_info=? WHERE organization_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, org.getName());
            ps.setString(2, org.getType());
            ps.setString(3, org.getContactInfo());
            ps.setInt(4, org.getOrgId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateOrganization error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteOrganization(int orgId) {
        String sql = "DELETE FROM organizations WHERE organization_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orgId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("deleteOrganization error: " + e.getMessage());
            return false;
        }
    }

    public List<Organization> getAllOrganizations() {
        List<Organization> list = new ArrayList<>();
        String sql = "SELECT * FROM organizations";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapOrg(rs));
        } catch (SQLException e) {
            System.err.println("getAllOrganizations error: " + e.getMessage());
        }
        return list;
    }

    public Organization getOrganizationById(int orgId) {
        String sql = "SELECT * FROM organizations WHERE organization_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orgId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapOrg(rs);
        } catch (SQLException e) {
            System.err.println("getOrganizationById error: " + e.getMessage());
        }
        return null;
    }

    public int getTotalOrganizations() {
        String sql = "SELECT COUNT(*) FROM organizations";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("getTotalOrganizations error: " + e.getMessage());
        }
        return 0;
    }

    private Organization mapOrg(ResultSet rs) throws SQLException {
    return new Organization(
        rs.getInt("organization_id"), 
        rs.getString("name"),
        rs.getString("type"),
        rs.getString("contact_info")
    );
}
}
