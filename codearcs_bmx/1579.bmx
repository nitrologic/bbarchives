; ID: 1579
; Author: Will
; Date: 2005-12-27 20:19:26
; Title: Send POST or GET data
; Description: Combined into useable code

Rem
The Blitz Functions
EndRem

Const METHOD_GET=1
Const METHOD_POST=2
Function SendPoGdata:TSocketStream(host:String,path:String,port=80,..
								 porg=METHOD_GET,data:String[])
	Local sock:TSocket = CreateTCPSocket()
	ConnectSocket(sock,HostIp(host),port)
	Local stream:TSocketStream = CreateSocketStream(sock,True)
	Local str:String
        Local i:Int = 0
	Select porg
	Case METHOD_GET
		str:String = "GET "+path+"?"
		For i=0 To data.length-1 Step 2
			str = str + "&" + esc(data[i]) + "=" + esc(data[i+1])
		Next
		str = str + " HTTP/1.0"
		WriteLine(stream,str)
		WriteLine(stream,"HOST: "+host)
		WriteLine(stream,"")
	Case METHOD_POST
		Local combined:String
		For i=0 To data.length-1 Step 2
			combined = combined + esc(data[i])+"="+esc(data[i+1])+"&"
		Next
		str:String = "POST "+path+" HTTP/1.0"
		WriteLine(stream,str)
		WriteLine(stream,"Accept: */*")
		WriteLine(stream,"Host: "+host)
		WriteLine(stream,"Content-type: application/x-www-form-urlencoded")
		WriteLine(stream,"User-agent: blitzconn") 
		WriteLine(stream,"Content-length: " + combined.length)
		WriteLine(stream,"Pragma: no-cache")
		WriteLine(stream,"Connection: keep-alive")
		WriteLine(stream,"")
		WriteLine(stream,combined)
		WriteLine(stream,"")
	EndSelect	
	FlushStream(stream)
	Local returns:String = ReadLine(stream)
	While returns <> ""
		Print returns
		returns = ReadLine(stream)
	Wend
	Return stream
EndFunction
Function esc:String(t:String)
	t = Replace(t,"&","")
	t = Replace(t,"%","")
	t = Replace(t,"'","")
	t = Replace(t,Chr(34),"")
	t = Replace(t," ","_")	
	Return t
End Function
Function PrintRecievedPoGData(sockstream:TSocketStream)
	If sockstream <> Null
		Local line:String = ReadLine(sockstream)
		While line <> ""
			Print line
			line = ReadLine(sockstream)
		Wend
	EndIf
EndFunction




Rem
'Function usage
Local t:TSocketStream
'SendPoGData( url(something.com), pathtofile(/blah/blah.php),port=80,porg(METHOD_GET or METHOD_POST),data:String[]( ["fieldname","vaue","otherfieldname","othervalue","etc....","etcvalue.."] )
t = SendPoGData( "blitzbasic.com", "/gnet/gnet.php", 80, METHOD_GET, ["opt","add" , "game","mygame" , "server","testserver"]
'Stream is now ready to return whatever the php script is displaying
Local line:String
If t <> Null
	line = ReadLine(t)
	While line <> ""
		Print line
		line = ReadLine(t)
	Wend
EndIf

'the gnet.php script will return OK if it works adding a game.






'Example PHP Script
'1 that doesn't use databases

'fieldname would be replaced with the name of the field
<?php
$postdat = $_POST['fieldname'];
$getdat = $_GET['fieldname'];
If ($postdat <> Null)
{
       echo "Got POST data: " . $postdat . "";
}
If ($getdat <> Null)
{
       echo "Got GET data: " . $getdat . "";
}
?>






'1 example that does use databases
'To get this example working, you enter all of the database information here at the top, and then to make the 'highscores' table you use:" setup(#ofScoresToBeSaved);". After that you can remove that statement and it should run

$username="YOURDATABASEUSERNAMEHERE";
$password="YOURDATABASEPASSWORDHERE";
$database="YOURDATABASENAMEHERE";
$databaseserver = "mostlikely localhost but could differ";
mysql_connect($databaseserver,$username,$password);
@mysql_select_db($database) Or die( "Unable to select database");
//See's if there is a database, or any users
$sql = "SELECT * FROM `highscores`";
If (mysql_query($sql) == Null)
{
	echo "The database is not setup correctly, in the begining of this file please add setup(); ";
}
/*
Recieves the POST data sent in the fields action And auth
*/
$action = $_POST['action'];
$authorization = $_POST['auth'];
$scorer = $_POST['user'];
$score = $_POST['score'];
$maxscores = $_POST['maxscores'];
switch ($action)
{
Case "addhigh":
	/*
	For this example there is a hardcoded password To upload highscores, this is To ward off cheaters, though a better system would be 	welcomed
	*/
	If ($authorization == "77344927ha345")
	{
		addhighscore($scorer, $score);
	}
	break;
Case "viewscores":
	showhighscores();
	break;
Case Null:
	//This is just a html view of the scores
	showwebhighscores("HIGHER");
	break;
Case "reset":
	removetable();
	$tmp = $maxscores;
	If ($maxscores == Null)
	{
		$tmp = 10;
	}
	setup($tmp);
	break;
Default:
	echo "Action not understood";
	break;
}
	
	
Function addhighscore($user, $score)
{	
		//Adds our user anywhere into the list
	$sql = "INSERT INTO `highscores` (`user`, `score`) VALUES ('" . $user . "', " . $score . ")";
	mysql_query($sql);
		//Sorts the list so the smallest scores are on top
	$sql = "SELECT *FROM `highscores` ORDER BY `score` ASC";
	$response = mysql_query($sql);
		//Finds the info about the top reccord (least scoring)
	$removalname = mysql_result($response, 0, "user");
	$removalscore = mysql_result($response, 0, "score");
		//Deletes the record using the data found above
	$sql = "DELETE FROM `highscores` WHERE `user` = '" . $removalname . "' AND `score` = " . $removalscore . " LIMIT 1";
	mysql_query($sql);
	showhighscores("HIGHER");
}
Function showhighscores($higherscore="HIGHER")
{
	If ($higherscore=="HIGHER")
	{
		$sortdir="DESC";
	}
	If ($higherscore=="LOWER")
	{
		$sortdir="ASC";
	}
	$sql = "SELECT *FROM `highscores` ORDER BY `score` " . $sortdir;
	$response = mysql_query($sql);
	$i=0;
	$Max=mysql_numrows($response);
	While ($i < $Max)
	{
		echo mysql_result($response, $i, "user") . " - " . mysql_result($response, $i, "score") . webend(0);
		$i = $i + 1;
	}
}
Function showwebhighscores($higherscore="HIGHER")
{
	If ($higherscore=="HIGHER")
	{
		$sortdir="DESC";
	}
	If ($higherscore=="LOWER")
	{
		$sortdir="ASC";
	}
	$sql = "SELECT *FROM `highscores` ORDER BY `score` " . $sortdir;
	$response = mysql_query($sql);
	$i=0;
	$Max=mysql_numrows($response);
	echo "<table border=1>";
	echo "<tr><td><b>Usernames</td><td>Scores</td><td>Place</b></td></tr>";
	While ($i < $Max)
	{
	echo "<tr align=center><td align=right> " . mysql_result($response, $i, "user") . " </td><td align=center> " . mysql_result($response, $i, "score") . " </td><td>" . ($i+1) . "</td></tr>";
	$i = $i + 1;
	}
	echo "</table>";
}
Function outputquery($response,$fieldname,$forweb=0)
{
	$Max=mysql_numrows($response);
	$i=0;
	While ($i < $Max)
	{
	echo mysql_result($response,$i,$fieldname) . webend($forweb);
	$i = $i + 1;
	}
}

Function webend($forweb)
{
	If ($forweb == 0)
	{
		Return "
";
	} Else {
		Return "<br>";
	}
}

Function setup($maxscores=10)
{
	$sql = "CREATE TABLE `highscores` ("
        . " `user` VARCHAR(60) NOT NULL, "
        . " `score` INT(11) NOT NULL,"
        . " INDEX (`score`)"
        . " )"
        . " TYPE = myisam";
	mysql_query($sql);
	$sql = "SELECT *FROM `highscores`";
	$results = mysql_query($sql);
	If (mysql_numrows($results) <> 0)
	{
		echo "There are already entries in the highscores table, please remove them, or if you have already setup please remove the setup() statement. To remove the table, simply add the removetable() command.";
	} Else {
	
	$sql = "INSERT INTO `highscores` (`user`, `score`) VALUES ('empty', 0)";
	For ($i=0; $i<$maxscores; $i++)
	{
		mysql_query($sql);
	}
	echo "This table was setup correctly, please remove the setup() statement. ";
	}
}
Function removetable()
{
	$sql = "DROP TABLE `highscores`";
	mysql_query($sql);
	echo "The table was removed, please remove this statment and add setup(#OfScores) to set the database up again.";
}
?>







To use this database version, the following blitzcode can be used
SENDPoGData(host,path,port,METHOD_POST,..
["action","addhigh","auth","77344927ha345","user","Adam C.","score","100"])
Local t:TSocketStream = SENDPoGData(host,path,port,METHOD_POST,["action","viewscores"])
PrintRecievedPoGData(t)


EndRem
