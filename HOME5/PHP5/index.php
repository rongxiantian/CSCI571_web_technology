<html>
 <head>
 <title>PHP Test</title>
 <meta http-equiv="Content-Type" content="text/html;
charset=ISO-8859-1">
 </head>
 <body>
 <h1>PHP Test</h1>
 <p>
 <b>An Example of PHP in Action</b><br />
 <?php date_default_timezone_set('America/Los_Angeles');?>
 <?php echo "The Current Date and Time is: <br>";
 echo date("g:i A l, F j Y.");?>
 </p>
 <h2>PHP Information</h2>
 <p>
 <?php phpinfo(); ?>
 </p>
 </body>
 </html> 
