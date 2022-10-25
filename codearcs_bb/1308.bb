; ID: 1308
; Author: Techlord
; Date: 2005-03-05 23:33:33
; Title: MySQL Database.bb and PHP Handler Script
; Description: A Simple MySQL Database Lib and PHP Handler Script

;============================
;DATABASE.bb
;============================
Const DATABASE_DAT_MAX%=256
Dim databaseTable$(DATABASE_DAT_MAX,DATABASE_DAT_MAX)

Type database
	Field id%
	Field datasource$
	Field host$
	Field SQL$ 
	Field port% 
	Field folder$
	Field filename$ 
	Field post$ 
	Field stream%
	Field error$
	Field state%
	Field count%
	Field fields%
	Field row%
	Field column%
	Field dats%
	Field dat$[DATABASE_DAT_MAX%]
End Type

Function databaseNew.database()
	;Purpose: Creates a Database Object Instance; Sets Defaults (constructor)
	;Parameters: None
	;Returns: None
	this.database=New database
	this\id%=1
	this\datasource$=""
	this\host$=""
	this\SQL$ =""
	this\port% =80
	this\folder$="/"
	this\filename$ =""
	this\post$ =""
	this\stream%=0
	this\error$=""
	this\state%=0
	this\count%=0
	this\fields%=0
	this\row%=0
	this\column%=0
	this\dats%=0
	Return this
End Function

Function databaseDelete(this.database)
	;Purpose: Deletes a Database Object Instance (destructor)
	;Parameters: Database Object
	;Returns: None
	Delete this
End Function

Function databaseCreate.database(host$,datasource$,folder$="/",filename$="database.php")
	;Purpose: Creates a Database
	;Parameters:
	;	host$ = Host Name ie: www
	;	datasource$ = Database name
	;	folder$ = folder of Database Handler Script
	;	filename$ =  name of Database Handler Script	
	;Returns: Database Object
	this.database=databaseNew()
	this\host$=host$
	this\datasource$=datasource$
	this\folder$=folder$
	this\filename$=filename$
	Return this
End Function

Function databaseConnect(this.database)
	;Purpose: Connects to Host
	;Parameters: Database Object
	;Returns: Connection Stream
	this\stream%=OpenTCPStream(this\host$,this\port)
	If Not this\stream% this\error$="Error: Cannot Connect to Database" 	
	Return this\stream%
End Function 

Function databaseClose(this.database)
	;Purpose: Closes Connection to Host
	;Parameters: Database Object
	;Returns: None
	If this\stream% CloseTCPStream this\stream%	
End Function

Function databaseSQL(this.database,sql$)
	;Purpose: Sets SQL String Value
	;Parameters: Database Object and SQL String
	;Returns: None
	this\sql$=sql$
End Function

Function databaseQuery(this.database,sql$)
	;Purpose: Post SQL String to Host
	;Parameters: Database Object and SQL String
	;Returns: None, but acquires Query Row and Field counts
	this\sql$=sql$
	If this\stream%
		this\post$=databasehttpurlencodestring$("host="+this\host$+"&datasource="+this\datasource$+"&sql="+this\sql$)
		WriteLine this\stream%,"GET "+this\folder$+this\filename$+"?" + this\post$ + " HTTP/1.0" 
		WriteLine this\stream%,"Host: "+this\host$
		WriteLine this\stream%,"User-Agent: Database.bb" 
		WriteLine this\stream%,"Accept: */*" 
		WriteLine this\stream%,"" 
		;skip	
		Repeat
			this\error$=ReadLine$(this\stream%)
		Until this\error$="results" Or this\error$="error"
		;error trap
		If this\error$="error"	
			this\error$="Error: "+ReadLine$(this\stream%)
		Else
			this\error$=""
		EndIf		
	    ;Setup Results Array table
		this\count%=ReadLine$(this\stream%)
		this\fields%=ReadLine$(this\stream%) 
	EndIf	
End Function

Function databaseResultNext(this.database)
	;Purpose: Stores Record Results one at a time in DatabaseTable Array.
	;	This can be useful if you want to increment the storage process within a 
	;	update loop.
	;Parameters: Database Object
	;Returns: None, but records are stored in DatabaseTable(row,field) Array
	If this\stream%
		this\row%=this\row%+1
		For this\column% = 1 To this\fields%
			;this\dats%=this\dats%+1
			;this\dat$[this\dats%]=ReadLine$(this\stream%)
			databaseTable$(this\row%,this\column%)=ReadLine$(this\stream)
		Next
	EndIf 	
End Function

Function databaseResultsAll(this.database)
	;Purpose: Stores Record Results All at once in DatabaseTable Array.
	;Parameters: Database Object
	;Returns: None, but records are stored in DatabaseTable(row,field) Array
	If this\stream%
		For this\row% = 1  To this\count%
			For this\column% = 1 To this\fields%
				;this\dats%=this\dats%+1
				;this\dat$[this\dats%]=ReadLine$(this\stream)
				databaseTable$(this\row%,this\column%)=ReadLine$(this\stream)
			Next
		Next
	EndIf		
End Function

Function databasehttpurlencodestring$(post$)
	;Purpose: Performs http string encoding
	;Parameters: Post String
	;Returns: Post String
	Restore databasehttpurlencode
	Repeat
		Read ascii,urlcode
		If ascii post$=Replace(post$,Chr(ascii),"%"+Str(urlcode)) 
	Until ascii=0
	Return post$
End Function 

.databasehttpurlencode
Data 32,20,34,22,39,21,0,0 ;note replaced ' with ! for php compatibility[/codebox]

;============================
;END DATABASE.bb
;============================

<?php //PHP MySQL Handler v.2 F.L.Taylor 11/06/04
    /* Connect & Select database */
    $connection = mysql_pconnect('localhost','username','password') or die("\nerror\n Could not connect : " . mysql_error());
    mysql_select_db($_GET['datasource']) Or die("\nerror\n Could not select database: ".$_GET['datasource']);
    /* SQL query */
    $result = mysql_query(ereg_replace("!","'",$_GET['sql'])) or die("\nerror\n ".mysql_error());
	/* results,row,column */
    echo "\nresults\n";
	if (is_resource($result)){
		echo mysql_num_rows($result)."\n";
		echo mysql_num_fields($result)."\n";
		/* write results */
		while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {//rows
			foreach ($row as $field) {//columns per row (cells)
				echo "$field\n";
			}
		}
		/* Free result */
		mysql_free_result($result);
	}
    /* Closing connection */
    mysql_close($connection);
?>

;Example: 
database.database=databaseCreate.database("www.yourhost.com","database")
databaseConnect(database)
databaseQuery(database,"SELECT * FROM table")
databaseResultsAll(database)
For row = 1 To database\row%
	For fields =  1 To database\fields%
		Print databaseTable$(row,fields)
	Next
Next
