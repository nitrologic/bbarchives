; ID: 323
; Author: jfk EO-11110
; Date: 2002-05-23 18:16:11
; Title: IP Management
; Description: Game Host IP Management: Perl talks to BB

file hosted_games.txt
cut here..............................
Host 65464.64646
127.0.0.1
This is a Dummy Host
10 102175228100
Don't
try
to connect!

cut here..............................






















file list_games.pl
cut here..............................
#!/usr/bin/perl -w

###########################################################################################
#
#                                 ************************************
#                                 Net Test for CSP Games IP Management
#                                 ************************************
#
# Note: You should unrem all 'flock'-Commands if possible!!!! I had to deactivate
# them cause it didn't work on WinME, but the flocks are needed to prevent multiple
# Clients writing into the same File at the same Time. This could result in complete
# Loss of the Game List Content.
#
# On Unix you'll have to chmod the file 'hosted_games.txt' as 666 and this Script as 755.
# On Windows you'll have to set the file 'hosted_games.txt' properties to 'hidden'.
#
# Well, this Script is the work of a beginner, so please don't worry.
#
# Be carefull when you edit this Script: the Order of the Values can be messed up easily.
#
###########################################################################################

$Daten=$ENV{'QUERY_STRING'};

$host_timeout=180;  # timeout for mute Hosts ins Seconds (must be higher than in BB)

# use 5.004;  # maybe you need these 2 lines for flock
# use Fcntl qw(:DEFAULT :flock);

print "Content-type: text/html\n\n";
print "<html><br>\n";

$hpass=$^T * $ENV{'REMOTE_ADDR'};  # generate password
$count=0;
$hisip= $ENV{'REMOTE_ADDR'};  # get clients IP

## Split Parameters found in Adressline
$i=0;
@vliste=("");
@Formularfelder = split(/&/, $Daten);
foreach $Feld (@Formularfelder)  
 {
  ($name, $value) = split(/=/, $Feld);   
  $value =~ tr/+/ /;
  $value =~ s/%([a-fA-F0-9][a-fA-F0-9])/pack("C", hex($1))/eg;
  $value =~ s/<!--(.|\n)*-->//g;      
  $Formular{$name} = $name;   
  $Formular{$value} = $value;
  $vliste[$i]=$value;
  $i++;
  }
$anzahl_paras=$i;



#------------------------------------------------------------------------------------
#                               REQUEST FOR HOSTED GAMES LIST
#------------------------------------------------------------------------------------
# This will send a list to the client. It will also delete Hosts which have a
# Timeout. It must be accessed with http GET as follows:
# http://127.0.0.1/cgi-bin/list_games.pl?action=list

if($vliste[0] eq "list")
 #read hostlist from disk
 {
  $i=0; 
  @Zeilen = ("");
  @Zeilen2 = ("");
  open(NICKLIST, "<hosted_games.txt") || die "File not found\n";
  # flock(NICKLIST,1) or die "Cannot LOCK file: $!";
  while(<NICKLIST>)
   {
    push(@Zeilen,$_);
    $i++;
   }
   # flock(NICKLIST,8) or die "Cannot UNLOCK file: $!";
   close(NICKLIST);
   $anzahl=$i+1;
 $i2=0;
 $flag=0;
 $upd=0;
 for($i=1;$i<$anzahl;$i++)
  {
   $check=$Zeilen[$i];
   chomp($check);
   if(length($check) >= 5)
   {
    $check=substr($check,0,4);
   if($check eq "Host") # check for timeout of a Host
    {
      $lastping=$Zeilen[$i+3];
      chomp($lastping);
      $lastpingl=length($lastping);
      $wo = rindex($lastping," ");
      $otime=substr($lastping,$wo,$lastpingl-$wo);
      $sec=($^T-$otime);  # secs since last info-update by host
      $omax=substr($lastping,0,$wo);
      $otopic=$Zeilen[$i+2];
      chomp($otopic);
      if($sec > $host_timeout) # timeout! delete host from list
      {
       $upd=1;
       $flag=1;
      }
    }
   }
   if($flag == 0)
    {
     $Zeilen2[$i2]=$Zeilen[$i];
     if($check eq "Host") {print "$check\n";}
     else {print $Zeilen2[$i2];}
     $i2++;
    }
   if($check eq "") # check for last line of a Game Block
    {
     $flag=0;
    }
  }

  # decide if the file must be updated
  if($upd == 1)
   {
    $anzahl=$i2-1;
    # open games list to overwrite
    open(NICKLIST, ">hosted_games.txt") || die "File not found\n";
    # flock(NICKLIST,2) or die "Cannot LOCK";
    for($i=0;$i<$anzahl;$i++)
      {
        print NICKLIST $Zeilen2[$i];
      }
    # flock(NICKLIST,8) or die "Cannot UNLOCK";
    close(NICKLIST);
   }
 }




#------------------------------------------------------------------------------------
#                             CLIENT WANTS TO BE A NEW HOST
#------------------------------------------------------------------------------------
# The Client chooses to be a new Host. Accessed ny http GET as follows:
#http://127.0.0.1/cgi-bin/list_games.pl?action=nuhost&nick=harry&topic=Capture%20flag!&max=10
#                                            action 0   nick 1   topic 2       max players 3

if($vliste[0] eq "nuhost")
{
 $i=0; 
 @Zeilen = ("");
 open(NICKLIST, "<hosted_games.txt") || die "File not found\n";
 # flock(NICKLIST,1) or die "Cannot LOCK file: $!";
 while(<NICKLIST>)
  {
   push(@Zeilen,$_);
   #   print $Zeilen[$i],"<br>\n";
   $i++;
  }
 # flock(NICKLIST,8) or die "Cannot LOCK file: $!";
 close(NICKLIST);
 $anzahl=$i+1;
 $ngames=0;
 $double=0;  # check if IP is already hosting
 for($i=1;$i<$anzahl;$i++)
 {
  $weniger=length($Zeilen[$i])-1;
  chomp($Zeilen[$i]);
#  print $Zeilen[$i],"<br>\n";
  if($weniger >= 5)
  {
   $check=substr($Zeilen[$i],0,4);
   if($check eq "Host")
   {
    $ngames++;
    if(substr($Zeilen[$i+1],0,length($Zeilen[$i+1])-1) eq $hisip)
    {
      $double=$i+1;
    }
   }
  }
 }
 if($double != 0)
 {
 print "Error 1   :IP is already hosting"; # IP is already hosting.
 }
 else # yes, a legal new Host
 {
  # open games list to overwrite
  open(NICKLIST, ">hosted_games.txt") || die "File not found\n";
  # flock(NICKLIST,2) or die "Cannot LOCK";
  for($i=1;$i<$anzahl;$i++)
  {
   print NICKLIST $Zeilen[$i],"\n";
   # $imerk=$i;
  }
  print NICKLIST "\n";                              # line feed
  print NICKLIST "Host $hpass\n";                   # "Host" Label
  print NICKLIST $ENV{'REMOTE_ADDR'},"\n";          # New Host IP
  print NICKLIST "$vliste[2]\n";                    # Topic
  print NICKLIST "$vliste[3] $^T\n";                # Max number of Players
  if(($vliste[1] eq "") || ($vliste[1] eq "Host") || ($vliste[1] eq "e_o"))
   { print NICKLIST "noname",$anzahl,"\n";}         # (legalized) Nick of Host
  else 
   { print NICKLIST "$vliste[1]\n"; }               # Nick of Host
  # flock(NICKLIST,8) or die "Cannot UNLOCK";
  close(NICKLIST);

  print "Welcome on CSP GS\n";  # Say hi to host
  print "$hpass\n";  # Send unique Password to host
 } # eo else ok nu host
} # eo nuhost





#------------------------------------------------------------------------------------
#                       A RUNNING GAME HOST SENDED UPDATED INFO
#------------------------------------------------------------------------------------
# Game Hosts request to update its Games Information in the list. Accessed by http GET:
# http://127.0.0.1/cgi-bin/list_games.pl?action=update&pass=7777777&max=12&n1=harry&n2=suzi&n3=rocky&n4=e_o
#                                            action=0      password=1   max.players=2   all nicks (host first)       "e_o"= end mark            

if($vliste[0] eq "update")
{
 $i=0; 
 @Zeilen = ("");
 open(NICKLIST, "<hosted_games.txt") || die "File not found\n";
 # flock(NICKLIST,1) or die "Cannot LOCK file: $!";
 while(<NICKLIST>)
  {
   push(@Zeilen,$_);
   #   print $Zeilen[$i],"<br>\n";
   $i++;
  }
 # flock(NICKLIST,8) or die "Cannot LOCK file: $!";
 close(NICKLIST);
 $anzahl=$i+1;
 $ngames=0;
 $double=0;  # check if IP is already hosting
 $lastping="";
 $otime="";
 $lastpingl=0;
 $wo=0;
 for($i=1;$i<$anzahl;$i++)
 {
  $weniger=length($Zeilen[$i])-1;
  chomp($Zeilen[$i]);
#  print $Zeilen[$i],"<br>\n";
  if($weniger >= 5)
  {
   $check=substr($Zeilen[$i],0,4);
   if($check eq "Host")
   {
    $check2=substr($Zeilen[$i],5,$weniger-5);
#    print "<b>",$check2,"</b><br>";
    $ngames++;
    if($vliste[1] eq $check2)  # compare passwords
    {
      $double=$i+1;
      #     print "<h1>Pass found!</h1>"; # Matching Password found
      $lastping=$Zeilen[$i+3];
      chomp($lastping);
      $lastpingl=length($lastping);
      $wo = rindex($lastping," ");
      $otime=substr($lastping,$wo,$lastpingl-$wo);
      $omax=substr($lastping,0,$wo);
      $otopic=$Zeilen[$i+2];
      chomp($otopic);
    }
   }
  }
 }
 if($double == 0)
 {
  print "Error 2   :Incorrect Password.";
 }
 else         # yes, a legal Host is updating
 {
  $i=1;
  $i2=1;
  # sort out old
  for($i=1;$i<$double-1;$i++) # copy first block
   {
    $Zeilen2[$i2]=$Zeilen[$i];
    $i2++;
   }
  # $i2=$i;
  $i++;
  while($Zeilen[$i] ne "")  # ignore old Info
  {
   $i++;
  }
  $imerk=$i+1;
  if($anzahl>$imerk)
  {
   for($i=$imerk;$i<$anzahl;$i++) # add rest block
    {
     $Zeilen2[$i2]=$Zeilen[$i];
     $i2++;
    }
  }
  #                         # add updated Infos
  $Zeilen2[$i2]="Host ".$vliste[1];
  $i2++;
  $Zeilen2[$i2]=$ENV{'REMOTE_ADDR'};
  $i2++;
  $Zeilen2[$i2]=$otopic;
  $i2++;
  $i3=2;
  $vliste[$i3]=$vliste[$i3]." $^T";  # Add timestamp
  while($vliste[$i3] ne "e_o")  # seek end of of updateinfo.
  {
   $Zeilen2[$i2]=$vliste[$i3];
   $i2++;
   $i3++;
  }
  $anzahl=$i2;
  # overwrite List File with updated Version
  open(NICKLIST, ">hosted_games.txt") || die "File not found\n";
  # flock(NICKLIST,2) or die "Cannot LOCK";
  for($i=1;$i<$anzahl;$i++)
  {
   print NICKLIST $Zeilen2[$i],"\n";
  }
  # flock(NICKLIST,8) or die "Cannot UNLOCK";
  close(NICKLIST);
  print "Thanks for updating on CSP GS\n";
 } # eo else ok nu host
} # eo nuhost







#------------------------------------------------------------------------------------
#                               RUNNING GAME HOST WANTS TO QUIT
#------------------------------------------------------------------------------------
# This will remove your Host from the List. It must be accessed with http GET as follows:
# http://127.0.0.1/cgi-bin/list_games.pl?action=byebye&pass=7777777

if($vliste[0] eq "byebye")
 #read hostlist from disk
 {
  $i=0; 
  @Zeilen = ("");
  @Zeilen2 = ("");
  open(NICKLIST, "<hosted_games.txt") || die "File not found\n";
  # flock(NICKLIST,1) or die "Cannot LOCK file: $!";
  while(<NICKLIST>)
   {
    push(@Zeilen,$_);
    $i++;
   }
   # flock(NICKLIST,8) or die "Cannot UNLOCK file: $!";
   close(NICKLIST);
   $anzahl=$i+1;
 $i2=0;
 $flag=0;
 $upd=0;
 for($i=1;$i<$anzahl;$i++)
  {
   $check=$Zeilen[$i];
   chomp($check);
   if(length($check) >= 5)
   {
    $check=substr($check,0,4);
   if($check eq "Host") # check for timeout of a Host
    {
      $lpass=substr($Zeilen[$i],5,length($Zeilen[$i])-6);
      if($lpass eq $vliste[1]) # password found, game will be removed from list
      {
       $upd=1;
       $flag=1;
      }
    }
   }
   if($flag == 0)
    {
     $Zeilen2[$i2]=$Zeilen[$i];
     $i2++;
    }
   if($check eq "") # check for last line of a Game Block
    {
     $flag=0;
    }
  }

  # decide if the file must be updated
  if($upd == 1)
   {
    $anzahl=$i2-1;
    # open games list to overwrite
    open(NICKLIST, ">hosted_games.txt") || die "File not found\n";
    # flock(NICKLIST,2) or die "Cannot LOCK";
    for($i=0;$i<$anzahl;$i++)
      {
        print NICKLIST $Zeilen2[$i];
      }
    # flock(NICKLIST,8) or die "Cannot UNLOCK";
    close(NICKLIST);
    print "byebye!";
   }
 }





#------------------------------------------------------------------------------------



print "\n>>>>Server Time: $^T\n";
print "<br></html>\n";
cut here..............................





















file net_test.bb
cut here..............................
;                                 ************************************
;                                 Net Test for CSP Games IP Management
;                                 ************************************
;
; Usually People have to know a Host IP to join a Game. This Program will help to keep
; a List of active Game Hosts on a Web Server with a static Adress and let the Player choose
; a running Game or to start a new Game as a Host and add his IP to the List.
;
; The following Program is used to interact with a webserver which hosts the
; Perl-Script '/cgi-bin/list_games.pl'. The file 'hosted_games.txt' must be in the cgi-bin folder 
; of the server. Note: this will not work without a server which is hosting the Script.
; This Program can still be optimized a lot. Error Handling, GFX and so on are very poor ATM. 
; However, if there's a Server then it should work. READ THE COMMENTS IN 'list_games.pl' !!!
;
; There is a maximum of about 25 Games with 25 Players each because of this Menu
; and maybe because of limited Bandwith of your Web-Server.
; Nicknames and Topics shouldn't be too long.
;
; This Sources are designed for Use with BlitzPlay, the UDP-Network-Library for BlitzBasic.
; Visit www.blitzbasic.com if you wonder what BlitzBasic is.
;
; How it works:
; *************
; game_list     will download a list of Game-Host IPs
; game_nuhost   will write your IP to the List on the Server
; game_update   will update your Game with the Players Names etc. in the List on the Server
; game_quit     will remove your Game-Hosts IP from the IP Management Server
;
; usually, when a Host want's to quit, the Host should chanche to the next Player. This Script
; and Program is capable of Handling such a Host Takeover. But the new Host must know the 
; Password. (password$)
;
; Tested on WinME, XITAMI Webserver, ActivePerl 5.04 s well as Apache/1.3.12 on Unix
; 2002 CSP

ip$="http://www.yourserver.com"  ; Enter the IP-Management Servers IP or Hostname here
port=80               ; Enter the IP-Management Servers HTTP Port here (usually 80)
list_upd_time=160000  ; Intervall Updates IP Management Server in ms. (should be lower than the one in the Perl Script)
;-----------------------------------------------------------------------------------

Graphics3D 800,600,16,2
SetBuffer BackBuffer()

Dim io$(1000)  ; Buffer for tcp i/o
.refresh
Global status$=""
Global iocount=0

               ; request for hosted games list
AppTitle "Connecting..."
game_list$(ip$,port)
AppTitle status$

For i=0 To iocount   ; check Start of Content Information
 If io$(i)="Host"
  start_content=i
  Exit
 EndIf
Next

If iocount>start_content   ; check integrity
 If io$(start_content)<>"Host"
  errexit("Error: The received File isn't a proper Games List")
 EndIf
Else
  errexit("Error: The received File isn't a proper Games List")
EndIf

ngames=0 ;       count games
For i=0 To iocount
 If io$(i)="Host"
  ngames=ngames+1
 EndIf
Next

If ngames=0
 errexit("Error: No Games in List") ; one Dummy-Game should always be there
EndIf


Dim nmembers(ngames)     ; Number of Players for each Game
Dim games(ngames)        ; Index of IP in io-Buffer
Dim maxmembers$(ngames)  ; Maximum Players per Game
Dim topic$(ngames)       ; Topics for each Game
Dim timestamp$(ngames)   ; Last ping (secs since 1970) for each Game
Dim nicks$(1000)         ; nicks for each Game

zz=0
For i=0 To iocount       ; sort stuff
 If io$(i)="Host"
  games(zz)=i+1
  topic$(zz)=io$(i+2)
  maxmembers$(zz)=io$(i+3)
  For k=1 To Len(maxmembers$(zz))  
   If Mid$(maxmembers$(zz),k,1)=" "
    cuthere=k
    Exit
   EndIf
  Next
  maxmembers$(zz)=Left$(maxmembers$(zz),cuthere)

  timestamp$(zz)=io$(i+3)
  timestamp$(zz)=Right$(timestamp$(zz),Len(timestamp$(zz))-(cuthere+3))
  zz2=0
  While io$(i+zz2+2)<>"" And zz2<1000
   zz2=zz2+1
  Wend
  nmembers(zz)=zz2
  If zz2>mostmembers Then mostmembers=zz2
  zz=zz+1
 EndIf
Next

Dim allmembers$(ngames,mostmembers)
For i=0 To ngames
 For j=0 To nmembers(i)
  allmembers(i,j)=io$(games(i)+3+j)
 Next
Next

servertime$="101010"
For i=iocount To 0 Step -1
If Len(io$(i))>4
 If Left$(io$(i),4)=">>>>"
  servertime$=io$(i)
 EndIf
EndIf
Next
  For k=Len(servertime$)  To 1 Step -1
   If Mid$(servertime$,k,1)=" "
    cuthere=k
    servertime$=Right$(servertime$,Len(servertime$)-(cuthere+3))
    Exit
   EndIf
  Next
; ________________________________________ SELECT HOST (or create new)___________________________________
ip_choice$=""
nuhost$=""
While KeyDown(1)=0 And ip_choice$="" And nuhost$=""
 Cls
 Color 0,0,50
 For i=0 To 600 Step 12
  Rect 0,i+1,800,12,0
 Next
 Color 0,0,255
 myline=MouseY()/12
 For i=0 To ngames-1
  Text 20,i*12,topic$(i)+" ("+io$(games(i))+") Hosted by: "+allmembers(i,0)
 Next
 Color 255,255,0
 Text 20,ngames*12,"Host a new Game"
 Color 255,255,255
 Text 20,(1+ngames)*12,"Refresh"
 If myline>=0 And myline< ngames
  i=myline
  Color 0,255,0
  Text 500,180,topic(i)
  Color 255,255,255
  Text 500,192,"IP: "+io$(games(i))
  Text 500,204,"Maximum of Players: "+maxmembers$(i)
  Text 500,216,"Last Livesign: "+(Int(servertime$)-Int(timestamp$(i)))+" Secs"
  Color 255,0,0
  For j=0 To nmembers(i)-3
   Text 500,240+j*12,allmembers(i,j)
  Next
  Color 255,255,255
  If MouseDown(1)=1
   ip_choice$=io$(games(i))
  EndIf
 EndIf
 If myline= ngames
  Color 0,255,0
  Text 500,180,"Click to Host a new Game"
  If MouseDown(1)=1
   nuhost$="yes"
  EndIf
 EndIf
 If myline= ngames+1
  Color 0,255,0
  Text 500,180,"Click to refresh Games List"
  If MouseDown(1)=1
   Goto refresh
  EndIf
 EndIf
 Flip()
Wend

hosting=0
joining=0

; ---------------------------------if JOIN --------------------------------
 If ip_choice$<>""
  Cls
  Text 20,20,"You decided to join a running Game"
  Text 20,40,ip_choice$
  Flip
  WaitKey()
  joining=1
 EndIf
; ---------------------------------if NEW HOST --------------------------------

 If nuhost$<>"" And ngames<26
  Cls
  Text 20,20,"You decided to host a new Game"
  Flip
  Locate 20,40
  nu_topic$=Input$("Topic? (max.20 Chrs.) ")
  If Len(nu_topic$)>20 Then nu_topic$=Left$(nu_topic$,20)
  Locate 20,60
  nu_nick$=Input$("Your Nickname? ")
  If Len(nu_nick$)>16 Then nu_nick$=Left$(nu_nick$,16)
  Locate 20,80
  nu_max$=Input$("Max. Number of Players? ")
  nmax=nu_max$
  If nmax<2 Then nmax=2
  If nmax>100 Then nmax=100

  nu_topic$=url_encode$(nu_topic$)
  nu_nick$=url_encode$(nu_nick$)
  
  Print "Trying to establish Host on Games Server..."
  AppTitle "Connecting..."
  game_nuhost(ip$,port,nu_topic$,nu_nick$,nmax)
  If iocount>start_content+1
   password$=io$(start_content+1)
   everr$=io$(start_content)
   hosting=1
  EndIf
  AppTitle status$
  If Len(everr$)>=5
   If Left$(everr$,5)="Error"
    AppTitle everr$
    hosting=0
   EndIf
  EndIf
  ; Some Test-Nicks for our Host - instead use your Games Nicks !!!
  nicks$(0)=nu_nick$
  nicks$(1)=url_encode$("Superman")
  nicks$(2)=url_encode$("Obi van")
  nicks$(3)=url_encode$("Ronin")
  nicks$(4)=url_encode$("Morpheus")
  nicks$(5)=url_encode$("Gorbi")
  nnicks=5 ; number of nicks -1 (important!)
 EndIf

; Add your Game-Connection Initialisation here ()


While KeyDown(1)<>0:VWait:Wend

; ---------------------------------GAME-------------------------------
tt=MilliSecs()+list_upd_time
While KeyDown(1)=0
 Cls
 Text 200,200,"I am a Game :).  Hosting: "+hosting+"  Joining: "+joining+"."
 ;
 ;  Here is your Game, doing all kinds of stuff
 ;
 If hosting=1
  If MilliSecs()>tt ; update Game List frequently to remain in the Host List AND after Chanches.
   game_update(ip$,port,password$,nmax,nnicks)
   tt=MilliSecs()+list_upd_time
  EndIf
 EndIf

 Flip
Wend

If hosting=1
 game_quit(ip$,port,password$) ; do this only when none of the other Players is the new Host
EndIf

End
; Please note: The Host is using a Password to update the List on the IP Management Server.
; If the Host disconnects and a Client will continue Hosting then the old Host should tell
; the new Host the Password before disconnecting to enable the new Host to do the Update Job.
; You can also tell the Password to the next Client from beginning on - to make shure the
; Game can be continued even if the old Host was disconnected by accident.

; You have to update your Nick List and the Variable 'nnicks'(number of nicks) before you 
; call'game_update()'. The first Nick in the List should be the Hosts Nickname.






;------------------------------------------------------------------------------------
;                            REQUEST FOR HOSTED GAMES IP-LIST
;------------------------------------------------------------------------------------
; This will ask the IP Management Server for a list. It will also force the Server to delete 
; Hosts which have a Timeout. It is accessed with http GET as follows:
; http://127.0.0.1/cgi-bin/list_games.pl?action=list
Function game_list$(ip$,port)
 tcp=OpenTCPStream(ip$,port )
 If  tcp
  Status$="Error: Connection to "+ip$+" failed"
   WriteLine tcp,"GET /cgi-bin/list_games.pl?action=list HTTP/1.1"
   WriteLine tcp,"Host: krokant.tripod.com"
   WriteLine tcp,"User-Agent: BlitzBrowser"
   WriteLine tcp,"Accept: */*"
   WriteLine tcp,""

   If Eof(tcp)
     Status$="Error: Connection "+ip$+" does not answer"
   EndIf
  For i=0 To 1000
   io$(i)=""
  Next
  iocount=0
  wr=WriteFile("rappi.txt")
  While Not Eof(tcp)
    io$(iocount)= ReadLine$( tcp )
    Print io$(iocount)
    WriteLine wr,io$(iocount)
   iocount=iocount+1
  Wend
  CloseFile wr

  If Eof(tcp)=1 Then 
    Status$="Connected"
  Else
    Status$="Error: EOF not reached"
  EndIf
  CloseTCPStream tcp
Else
 Status$="Error: Connection to "+ip$+" failed"
EndIf
Return status$

End Function






;------------------------------------------------------------------------------------
;                          BB-CLIENT WANTS TO BE A NEW BB-HOST
;------------------------------------------------------------------------------------
; You choosed to be a New Host. Accessed by http GET as follows:
; http://127.0.0.1/cgi-bin/list_games.pl?action=nuhost&nick=harry&topic=Capture%20flag!&max=10
;                                            action 0   nick 1   topic 2       max players 3
Function game_nuhost(ip$,port,lnu_topic$,lnu_nick$,lnmax)
 tcp=OpenTCPStream(ip$,port )
 If  tcp
  Status$="Error: Connection to "+ip$+" failed"
  WriteLine tcp,"GET /cgi-bin/list_games.pl?action=nuhost&nick="+lnu_nick$+"&topic="+lnu_topic$+"&max="+Str$(lnmax)+" HTTP/1.1"
  WriteLine tcp,"Host: krokant.tripod.com"
  WriteLine tcp,"User-Agent: BlitzBrowser"
  WriteLine tcp,"Accept: */*"
  WriteLine tcp,""
   If Eof(tcp)
     Status$="Error: Connection "+ip$+" does not answer"
   EndIf
  For i=0 To 1000
   io$(i)=""
  Next
  iocount=0
  While Not Eof(tcp)
    io$(iocount)= ReadLine$( tcp )
   iocount=iocount+1
  Wend

  If Eof(tcp)=1 Then 
    Status$="Connected"
  Else
    Status$="Error: EOF not reached"
  EndIf
  CloseTCPStream tcp
Else
 Status$="Error: Connection to "+ip$+" failed"
 If iocount>0
  
 EndIf
EndIf
Return status$

End Function








;------------------------------------------------------------------------------------
;                      SEND UPDATED INFO TO IP MANAGEMENT SERVER
;------------------------------------------------------------------------------------
; Your Games Hosts request To update its Games Information in the list od the Server. Accessed 
; by http GET:
; http://127.0.0.1/cgi-bin/list_games.pl?action=update&pass=7777777&max=12&n1=harry&n2=suzi&n3=rocky&n4=e_o
;                                            action=0      password=1   max.players=2   all nicks (host First)       "e_o"= End mark            
Function game_update(ip$,port,pw$,lnmax,nnicks)
 tcp=OpenTCPStream(ip$,port )
 If  tcp
  allnicks$=""  ; assemble all nicks + end-label
  For i=0 To nnicks
   allnicks$=allnicks$+"&n"+Str$(1+i)+"="+nicks$(i)
  Next
  allnicks$=allnicks$+"&n"+Str$(1+i)+"="+"e_o"
  Status$="Error: Connection to "+ip$+" failed"
  WriteLine tcp,"GET /cgi-bin/list_games.pl?action=update&pass="+pw$+"&max="+Str$(lnmax)+allnicks$+" HTTP/1.1"
  WriteLine tcp,"Host: krokant.tripod.com"
  WriteLine tcp,"User-Agent: BlitzBrowser"
  WriteLine tcp,"Accept: */*"
  WriteLine tcp,""
   If Eof(tcp)
     Status$="Error: Connection "+ip$+" does not answer"
   EndIf
  For i=0 To 1000
   io$(i)=""
  Next
  iocount=0
  While Not Eof(tcp)
    io$(iocount)= ReadLine$( tcp )
   iocount=iocount+1
  Wend

  If Eof(tcp)=1 Then 
    Status$="Connected"
  Else
    Status$="Error: EOF not reached"
  EndIf
  CloseTCPStream tcp
Else
 Status$="Error: Connection to "+ip$+" failed"
EndIf
Return status$

End Function




;------------------------------------------------------------------------------------
;                          QUIT GAME, REMOVE HOST FROM LIST
;------------------------------------------------------------------------------------
; Your Games Hosts Request to be removed from the Server List. Accessed  by http GET:

; http://127.0.0.1/cgi-bin/list_games.pl?action=byebye&pass=7777777
;                                            action=0      password=1
Function game_quit(ip$,port,pw$)
 tcp=OpenTCPStream(ip$,port)
 If  tcp
  Status$="Error: Connection to "+ip$+" failed"
  WriteLine tcp,"GET /cgi-bin/list_games.pl?action=byebye&pass="+pw$+" HTTP/1.1"
  WriteLine tcp,"Host: krokant.tripod.com"
  WriteLine tcp,"User-Agent: BlitzBrowser"
  WriteLine tcp,"Accept: */*"
  WriteLine tcp,""
   If Eof(tcp)
     Status$="Error: Connection "+ip$+" does not answer"
   EndIf
  For i=0 To 1000
   io$(i)=""
  Next
  iocount=0
  While Not Eof(tcp)
    io$(iocount)= ReadLine$( tcp )
   iocount=iocount+1
  Wend

  If Eof(tcp)=1 Then 
    Status$="Disconnected successfully"
  Else
    Status$="Error: EOF not reached"
  EndIf
  CloseTCPStream tcp
Else
 Status$="Error: Disconnection from "+ip$+" failed"
EndIf
Return status$

End Function






; ----------------------------------------------------------------------------
; this will End Program with an Errormessage:
Function errexit(err$)
 ; Cls
  Print err$
  Flip
  WaitKey()
  End
End Function


; ----------------------------------------------------------------------------
Function url_encode$(t$)
  tem$=""
  For i=1 To Len(t$)
   a=Asc(Mid$(t$,i,1))
   If a = 32
    tem$=tem$+"+"
    Goto my_end_select
   EndIf
   If (a<48)Or(a>57 And a<64)Or((a>=91) And (a<=96))Or(a>=123)
    tem$=tem$+"%"+Right$(Hex$(a),2)
    Goto my_end_select
   EndIf
   tem$=tem$+Chr$(a)
   .my_end_select
  Next
  Return tem$
End Function
; ----------------------------------------------------------------------------
;                          END OF TEST IP MANAGEMENT
; ----------------------------------------------------------------------------cut here..............................
