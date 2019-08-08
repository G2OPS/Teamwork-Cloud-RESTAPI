1.Open Eclipse
2.File - Open Project from folder - Select TWCRESTAPI
3.Right Click TWCRESTAPI - Select Configure - Configure Build Path - Click on Classpath - Add External Jars - Add all jars in jar_files.zip
4.Change the IP Address in the baseURL string in the TWCRestAPI class
5.Find the TWCAuthenticator class and change the username and password for a user that has Administrative Priviledges to edit a user etc..
6.Make sure you dont accidentally disable a user when using the enable or disable user method
7.Data does take time to pull. I only have 18 users so I can't guarantee how this will work with 500+ users
In the java file you may have to change the value of the con.setConnectTimeout and con.setReadTimeout method calls to wait a little longer.
8. Thanks for a great internship.

