package com.infy.addressbook.servlets;

import com.google.gson.Gson;
import com.infy.addressbook.db.ContactDB;
import com.infy.addressbook.entities.Contact;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author senthilkumar_v04
 */
public class AddressBookServlet extends HttpServlet {
    Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        gson = new Gson();
    }

    public String readReqBody(HttpServletRequest request){
        ServletInputStream sis = null;
        try {
            sis = request.getInputStream();
            BufferedReader rdr = new BufferedReader(new InputStreamReader(sis));
            String post = "";
            String str = "";
            while ((str = rdr.readLine()) != null) {
                post = post + str;
            }
            rdr.close();
            return post;
        } catch (IOException ex) {
            return "";
        } finally {
            try {
                sis.close();
            } catch (IOException ex) {
                return "";
            }
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String id = request.getParameter("id");
            ContactDB cdb = new ContactDB();
            if(id == null){
                List<Contact> contacts = cdb.findContacts();
                out.println(gson.toJson(contacts));
            }else {
                Contact contact = cdb.findContact(Integer.parseInt(id));
                out.println(gson.toJson(contact));
            }
            cdb.closeConnection();
        } catch (SQLException ex) {
            out.println("{}");
            Logger.getLogger(AddressBookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            out.println("{}");
            Logger.getLogger(AddressBookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String post = readReqBody(request);
            if(post != null && !post.equals("")){
                Contact contact = gson.fromJson(post, Contact.class);
                ContactDB cdb = new ContactDB();
                cdb.createContact(contact);
                response.addHeader("id", contact.getGuid() + "");
                response.setStatus(201);
                cdb.closeConnection();
            }
        } catch (SQLException ex) {
            response.setStatus(400);
            Logger.getLogger(AddressBookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            response.setStatus(400);
            Logger.getLogger(AddressBookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String param = request.getParameter("id");
            String post = readReqBody(request);
            if(post != null && !post.equals("")){
                Contact contact = gson.fromJson(post, Contact.class);
                contact.setGuid(Integer.parseInt(param));
                ContactDB cdb = new ContactDB();
                cdb.editContact(contact);
                out.println(gson.toJson(contact));
                cdb.closeConnection();
            }
        } catch (SQLException ex) {
            out.println("{}");
            Logger.getLogger(AddressBookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            out.println("{}");
            Logger.getLogger(AddressBookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String id = request.getParameter("id");
            ContactDB cdb = new ContactDB();
            if(id != null){
                cdb.deleteContact(Integer.parseInt(id));
                response.setStatus(201);
            }
            cdb.closeConnection();
        } catch (SQLException ex) {
            response.setStatus(400);
            Logger.getLogger(AddressBookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            response.setStatus(400);
            Logger.getLogger(AddressBookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }
}