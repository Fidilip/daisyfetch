#Daisyfetch

Daisyfetch is web application that allows users fetch web pages as JSON. You can find it [deployed on Openshift](http://daisyfetch-fsivak.rhcloud.com/)

##Internals
###Backend
Backend is JEE7 application running on Wildfly application server. The data layer is serviced by embedded Infinispan cache with filesystem passivation.

###Frontend
Frontend is AngularJS module that is communicating with backend over RESTfull interface.

###Security
REST and client are secured by servlet security that is serviced by PicketLink, allowing users to log in using Google or Facebook.
