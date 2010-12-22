package com.infy.addressbook.db;

import com.infy.addressbook.entities.Contact;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ContactDB.java - This class communicates with the database
 * @author senthilkumar_v04
 */
public class ContactDB {
    // <editor-fold defaultstate="collapsed" desc="Database Credentials. Change it according to your database">
    String userName = "senthil";
    String password = "12345678";
    String url = "jdbc:oracle:thin:@10.122.122.208:1521:findb01";
    String driverClass = "oracle.jdbc.driver.OracleDriver";
    // </editor-fold>

    Connection conn;

    /**
     * Connection to the database is initialized
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ContactDB() throws SQLException, ClassNotFoundException {
        Class.forName(driverClass);
        conn = DriverManager.getConnection(url, userName, password);
    }

    /**
     * This method fetches all the contacts from the database
     * @return List of contacts
     * @throws SQLException
     */
    public List<Contact> findContacts() throws SQLException{
        List<Contact> contacts = new ArrayList<Contact>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM contact");
        while(rs.next()){
            Contact contact = new Contact();
            contact.setGuid(rs.getInt("contactId"));
            contact.setFirstName(rs.getString("firstName"));
            contact.setLastName(rs.getString("lastName"));
            contact.setEmailId(rs.getString("emailId"));
            contact.setMobileNo(rs.getString("mobileNo"));
            contact.setAge(rs.getInt("age"));
            contact.setState(rs.getString("state"));
            contact.setCountry(rs.getString("country"));
            contacts.add(contact);
        }
        return contacts;
    }

    /**
     * Fetches the contact details from the database for the given contact id
     * @param id - Contact Id
     * @return - Contact details
     * @throws SQLException
     */
    public Contact findContact(int id) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM contact WHERE contactId = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        Contact contact = null;
        if(rs.next()){
            contact = new Contact();
            contact.setGuid(rs.getInt("contactId"));
            contact.setFirstName(rs.getString("firstName"));
            contact.setLastName(rs.getString("lastName"));
            contact.setEmailId(rs.getString("emailId"));
            contact.setMobileNo(rs.getString("mobileNo"));
            contact.setAge(rs.getInt("age"));
            contact.setState(rs.getString("state"));
            contact.setCountry(rs.getString("country"));
        }
        return contact;
    }

    /**
     * Creates a new contact record in the database
     * @param contact - Contact Record
     * @throws SQLException
     */
    public void createContact(Contact contact) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO contact(contactId, firstName, lastName, emailId, mobileNo, age, state, country) VALUES(Seq_AddressBook.nextval, ?, ?, ?, ?, ?, ?, ?)");
        stmt.setString(1, contact.getFirstName());
        stmt.setString(2, contact.getLastName());
        stmt.setString(3, contact.getEmailId());
        stmt.setString(4, contact.getMobileNo());
        stmt.setInt(5, contact.getAge());
        stmt.setString(6, contact.getState());
        stmt.setString(7, contact.getCountry());
        stmt.executeUpdate();
        stmt = conn.prepareStatement("SELECT contactId FROM contact WHERE emailId = ?");
        stmt.setString(1, contact.getEmailId());
        ResultSet rs = stmt.executeQuery();
        if(rs.next()){
            contact.setGuid(rs.getInt("contactId"));
        }
        return;
    }
    /**
     * Updated the existing record with the given data
     * @param contact - Modified contact record
     * @throws SQLException
     */
    public void editContact(Contact contact) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("UPDATE contact SET firstName = ?, lastName = ?, emailId = ?, mobileNo = ?, age = ?, state = ?, country = ? WHERE contactId = ?");
        stmt.setString(1, contact.getFirstName());
        stmt.setString(2, contact.getLastName());
        stmt.setString(3, contact.getEmailId());
        stmt.setString(4, contact.getMobileNo());
        stmt.setInt(5, contact.getAge());
        stmt.setString(6, contact.getState());
        stmt.setString(7, contact.getCountry());
        stmt.setInt(8, contact.getGuid());
        stmt.executeUpdate();
        return;
    }

    /**
     * Delete the contact record with the given contact id
     * @param id - Contact Id
     * @throws SQLException
     */
    public void deleteContact(int id) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM contact WHERE contactId = ?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        return;
    }

    public void closeConnection() throws SQLException{
        conn.close();
    }
}
