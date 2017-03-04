/*
 * To change this license header choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.management.Query.plus;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 *
 * @author maverick
 */
public class gplus extends HttpServlet {
    
    private static final String CLIENT_ID = "XXXX.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "nP7oww0xETy0f-bpCvqljKwc";
    private static final String APPLICATION_NAME = "Java-OAuth-example  ";
    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    private static final Gson GSON = new Gson();
    
    private static String tokenData;
    private static String code;
    private static GoogleTokenResponse tokenResponse;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet gplus</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet gplus at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Call me GET");
        callGoogle(request,response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Call me POST");
        callGoogle(request, response);
    }
    
    private void callGoogle(HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        try 
        {
            getContent(request.getInputStream(), resultStream);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(gplus.class.getName()).log(Level.SEVERE, null, ex);
        }
        try 
        {
            code = new String(resultStream.toByteArray(), "UTF-8");
            System.out.println(code);
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(gplus.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Create a token
        try 
        {
            tokenResponse = new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY,CLIENT_ID, CLIENT_SECRET, code, "postmessage").execute();
            GoogleIdToken idToken = tokenResponse.parseIdToken();
            String gplusId = idToken.getPayload().getSubject();
            tokenData = tokenResponse.toString();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(GSON.toJson("Successfully connected user."));   
        } 
        catch (TokenResponseException e) 
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(GSON.toJson("Failed to upgrade the authorization code."));
        } 
        catch (IOException e) 
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(GSON.toJson("Failed to read token data from Google. " +
            e.getMessage()));
        }
         try {
        // Build credential from stored token data.
        GoogleCredential credential = new GoogleCredential.Builder()
            .setJsonFactory(JSON_FACTORY)
            .setTransport(TRANSPORT)
            .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
            .setFromTokenResponse(JSON_FACTORY.fromString(
                tokenData, GoogleTokenResponse.class));
        // Create a new authorized API client.
        Plus service = new Plus.Builder(TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
        // Get a list of people that this user has shared with this app.
        PeopleFeed people = service.people().list("me", "visible").execute();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(GSON.toJson(people));
        System.out.println(people.toString());
      } catch (IOException e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().print(GSON.toJson("Failed to read data from Google. " +
            e.getMessage()));
      }
    }
 
    private static void getContent(InputStream inputStream, ByteArrayOutputStream outputStream) throws IOException
    {
      // Read the response into a buffered stream
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      int readChar;
      while ((readChar = reader.read()) != -1) 
      {
        outputStream.write(readChar);
      }
      reader.close();
    }
}
