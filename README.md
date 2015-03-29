Java-GoogleOauth2.0
===========

The Google OAuth 2.0 endpoint supports web server applications that use languages and frameworks such as PHP, Java, Python, Ruby, and ASP.NET. These Java applications might access a Google API while the user is present at the application or after the user has left the application. This flow requires that the application can keep a secret.


OAuth 2.0 Web Application Flow
--------------------------------
The authorization sequence begins when your user connect to index.html. Here a javascript code call a Google signin web services. Google handles user authentication, session selection, and user consent. The result is an authorization code, which Google returns to client/user. By Javascript client send this code to web java application (GET or POST). After receiving the code, the application can exchange the code (along with a client ID and client secret) for an access token and, in some cases, a refresh token. Now the application can to list, for example, a list of people.

![Alt text](https://developers.google.com/+/images/server_side_code_flow.png "OAuth 2.0 Web Application Flow")

From your application, the user clicks the sign-in button in your client app. This sends an authorization request to Google authorization servers (1).

If your user hasn't authorized this app yet, the request triggers the OAuth 2.0 dialog, which pops up for the user (2) and asks them to grant your application the permissions listed by your scopes. If the user does so, the access_token, id_token, and a one-time code are returned to your client (3). You can then send the one-time code from the sign-in button to your server (4). When the server has the code, the server can exchange it for an access_token (5, 6) that can be stored locally on the server side. The server can then make Google+ API calls independently of the client. A final, optional step, involves sending a message from your server to your client, confirming that the user is now "fully logged in" (7).

Authenticating Google+ Sign-In with one-time-code flow requires you to:

<pre><code>
Create a client ID and client secret.
Create an anti-request forgery state token.
Include the Google+ script on your index.html page.
Add the sign-in button to your page.
Sign in the user.
Send the authorization code to the server (in this case, gplus.java).
Initialize the Google API client library and start the Google+ service.
</code></pre>
