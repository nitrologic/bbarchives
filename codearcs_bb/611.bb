; ID: 611
; Author: ShadowTurtle
; Date: 2003-03-03 23:15:14
; Title: BlitzPlus Irc Client 0.7 (Update)
; Description: This is a Irc-chat client for BlitzPlus

Global Server$, Port, NickName$, AutoJoin$
Global Network, NetWin, NetMsg

Global TextOutPut$ = "´ %NAME% ` %TEXT%"

Type Query
  Field Name$
  Field Window
  Field MsgBox
  Field InpBox
  Field InpBut
End Type

Type Channel
  Field Name$
  Field Window
  Field MsgBox
  Field InpBox
  Field InpBut
  Field UseBox
End Type

Type User
  Field Name$
  Field DeleteNow
  Field Channel.Channel
End Type

Input_Infos()
Start_Network()

Function Input_Infos()
  wW = 250 : wH = 200
  Win = CreateWindow("BlitzPlus Irc 0.6", ClientWidth(Desktop())/2-wW/2, ClientHeight(Desktop())/2-wH/2, wW, wH, 0, 1)

  InputA = CreateTextField(60, 12+ToDown, wW-70, 20, Win) : SetGadgetText InputA, "irc.blitzed.org"
  CreateLabel("Server :", 15, 15+ToDown, 45, 20, Win, 0) : ToDown = ToDown + 25

  InputB = CreateTextField(60, 12+ToDown, wW-70, 20, Win) : SetGadgetText InputB, "6667"
  CreateLabel("Port :", 15, 15+ToDown, 45, 20, Win, 0) : ToDown = ToDown + 35

  InputC = CreateTextField(60, 12+ToDown, wW-70, 20, Win) : SetGadgetText InputC, "BlitzBasicUser"
  CreateLabel("Name :", 15, 15+ToDown, 45, 20, Win, 0) : ToDown = ToDown + 25

  InputD = CreateTextField(60, 12+ToDown, wW-70, 20, Win) : SetGadgetText InputD, "#Project"
  CreateLabel("Autojoin :", 15, 15+ToDown, 45, 20, Win, 0) : ToDown = ToDown + 25

  ButtonA = CreateButton("Connect", 15, 15+ToDown, wW-25, 20, Win) : ToDown = ToDown + 25
  ButtonB = CreateButton("Close", 15, 15+ToDown, wW-25, 20, Win)

  While 1
    tmp = WaitEvent()
    If EventSource() = ButtonA Then
      Server$ = TextFieldText(InputA)
      Port = TextFieldText(InputB)
      NickName$ = TextFieldText(InputC)
      AutoJoin$ = TextFieldText(InputD)
      FreeGadget Win
      Return
    End If
    If EventSource() = ButtonB Then End   
  Wend
End Function

Function Start_Network()
  wW = 400 : wH = 575 : iH = 20 : bW = 40
  NetWin = CreateWindow("BlitzPlus Irc - ["+Server$+"]", ClientWidth(Desktop())/5-wW/2, ClientHeight(Desktop())/2-wH/2, wW, wH, 0, 1)
  NetMsg = CreateTextArea(-1, 0, wW-3, wH-28-iH, NetWin)
  NetInput = CreateTextField(0, wH-26-iH, wW-6-bW, iH, NetWin)
  NetButInput = CreateButton("Send", wW-6-bW, wH-26-iH, bW, iH, NetWin)
  AddTextAreaText NetMsg, "Connection to '"+Server$+"' Server ..." + Chr$(10)

  Network = OpenTCPStream(Server$, Port)
  If OpenIRCSession(Network) Then
    AddTextAreaText NetMsg, "Connection is Ready." + Chr$(10) + Chr$(10)
  Else
    AddTextAreaText NetMsg, "Connection to '"+Server$+"' Server is Abordet." + Chr$(10)
    RuntimeError "Connection to '"+Server$+"' Server is Abordet." + Chr$(10)
  End If

  If AutoJoin$<>"" Then JoinChannel(AutoJoin$)

  NetClient = 1
  While NetClient
    If Network
      Bytes=ReadAvail(Network)
      While Bytes
        Irc_Read ReadLine(Network)
        Bytes=ReadAvail(Network)
      Wend
    End If

    tmp = WaitEvent(2)
    tmpB = EventID()
    If Network
      For Query.Query = Each Query
        If tmpB = $401 Then
          If EventSource() = Query\InpBut Or (EventData() = 13 And EventSource()=Query\InpBox) Then
            TheText$ = TextFieldText(Query\InpBox)
            If Not TheText$ = "" Then
              If Not EnterBef(TheText$) Then
                WriteLine Network, "PRIVMSG " + Query\Name$ + " :" + TheText$
                AddTextAreaText Query\MsgBox, MO$(NickName$, TheText$) + Chr$(10)
              End If
              SetGadgetText Query\InpBox, ""
            End If
          End If
        ElseIf tmpB = $803
          If EventSource() = Query\Window Then
            FreeGadget Query\Window
            Delete Query
          End If
        End If
      Next

      For Channel.Channel = Each Channel
        If tmpB = $401 Then
          If EventSource() = Channel\InpBut Or (EventData() = 13 And EventSource()=Channel\InpBox) Then
            TheText$ = TextFieldText(Channel\InpBox)
            If Not TheText$ = "" Then
              If Not EnterBef(TheText$) Then
                WriteLine Network, "PRIVMSG " + Channel\Name$ + " :" + TheText$
                AddTextAreaText Channel\MsgBox, MO$(NickName$, TheText$) + Chr$(10)
              End If
              SetGadgetText Channel\InpBox, ""
            End If
          End If
        ElseIf tmpB = $803 Then
          If EventSource() = Channel\Window Then
            WriteLine Network, "PART " + Channel\Name$

            For User.User = Each User
              If User\Channel = Channel Then
                Delete User
              End If
            Next
            FreeGadget Channel\Window
            Delete Channel
          End If
        End If
      Next
    End If

    If tmpB = $803 Then
      If EventSource() = NetWin Then End
    ElseIf tmpB = $401 Then
      If EventSource() = NetButInput Or (EventData() = 13 And EventSource()=NetInput) Then
        TheText$ = TextFieldText(NetInput)
        If Not TheText$ = "" Then        
          tp$ = Lower(Trim(TheText$))
          EnterBef(tp$)
          SetGadgetText NetInput, ""
        End If
      End If
    End If
  Wend
End Function

Function EnterBef(tp$, tna$ = "")
  If Lower(Mid(tp$,1,5)) = "/join" Then
    JoinChannel(Mid(Trim(tp$),7))
    Return 1
  ElseIf Lower(Mid(tp$,1,4)) = "/msg" Then
    jj$ = Trim(tp$)
    toyou$ = GetPar(jj$, 0)
    thetxt$ = Mid$(jj$,7+Len(toyou$))

    WriteLine Network, "PRIVMSG " + toyou$ + " :" + thetxt$

    For Channel.Channel = Each Channel
      If Lower(Channel\Name$) = Lower(toyou$) Then
        AddTextAreaText Channel\MsgBox, MO$(NickName$, thetxt$) + Chr$(10)
      End If
    Next

    For User.User = Each User
      If Lower(NS$(User\Name$)) = Lower(NS$(toyou$))  Then
        Query.Query = OpenQuery(toyou)
        AddTextAreaText Query\MsgBox, MO(Query\Name$, thetxt$) + Chr$(10)
      End If
    Next

    Return 1
  ElseIf Lower(Mid(tp$,1,6)) = "/query" Then
    jj$ = Trim(tp$)
    toyou$ = GetPar(jj$, 0)
    Query.Query = OpenQuery(toyou)
  Else
    Return 0
  End If
End Function

; Here comes the another Functions
Function MO$(TheName$, TheText$)
  Return Replace(Replace(TextOutPut$, "%TEXT%", TheText$), "%NAME%", TheName$)
End Function

Function JoinChannel(ChanName$)
  wW = 400 : wH = 375 : iH = 20 : bW = 40 : lbG = 120
  Channel.Channel = New Channel
  Channel\Name$ = ChanName$
  Channel\Window = CreateWindow("["+Server$+"] :: " + ChanName$, ClientWidth(Desktop())/4-wW/2+fA, ClientHeight(Desktop())/2-wH/2, wW, wH, NetWin, 1)
  Channel\MsgBox = CreateTextArea(-1, 0, wW-6-lbG, wH-28-iH, Channel\Window)
  Channel\InpBox = CreateTextField(0, wH-26-iH, wW-6-bW, iH, Channel\Window)
  Channel\InpBut = CreateButton("Send", wW-6-bW, wH-26-iH, bW, iH, Channel\Window)
  Channel\UseBox = CreateListBox(wW-6-lbG, 0, lbG, wH-28-iH, Channel\Window)

  WriteLine Network, "JOIN " + Channel\Name$
;      AddTextAreaText Query\MsgBox, "` "+Query\Name$+" ´ " + GetContext$(tLine$) + Chr$(10)
;      tmps=1
End Function

Function Irc_Read(tLine$)
  TheText$ = GetPar(tLine$, 0)
  If TheText$ = "NOTICE" Then
    AddTextAreaText NetMsg, "Notice from " + GetFromNick$(tLine$) + " :: " + GetContext$(tLine$) + Chr$(10)
  ElseIf TheText$ = "PRIVMSG" Then
    j$ = GetPar(tLine$, 1)
    If Lower(j$) = Lower(NickName$) Then
      tmps = 0
      For Query.Query = Each Query
        If Lower(NS$(Query\Name$)) = Lower(NS$(GetFromNick$(tLine$))) Then
          AddTextAreaText Query\MsgBox, MO(Query\Name$, GetContext$(tLine$)) + Chr$(10)
          tmps = tmps + 1
        End If
        fA = fA + 10
      Next

      If tmps=0 Then
        Query.Query = OpenQuery(GetFromNick$(tLine$))
        AddTextAreaText Query\MsgBox, MO(Query\Name$, GetContext$(tLine$)) + Chr$(10)
        tmps=1
      End If
    Else
      For Channel.Channel = Each Channel
        If Channel\Name$ = j$ Then
          AddTextAreaText Channel\MsgBox, MO(GetFromNick$(tLine$), GetContext$(tLine$)) + Chr$(10)
        End If
      Next
    End If
  ElseIf TheText$ = "353" Then
    j$ = GetPar(tLine$, 3)
    For Channel.Channel = Each Channel
      If Channel\Name$ = j$ Then
        For User.User = Each User
          If User\Channel = Channel Then
            Delete User
          End If
        Next

        list$=GetPar(tLine$,1,":")
        Repeat
          newname$=GetPar(list$,n)
          If newname$<>lastname$ And newname$<>"" Then
            User.User = New User
            User\Name$ = newname$
            User\Channel = Channel
          Else
            Exit
          End If
          n=n+1 : lastname$=newname$
        Forever
        ActualUserList(Channel)
      End If
    Next
  ElseIf TheText$ = "PART" Then
    ch$ = Lower(GetPar(tLine$,2))
    For User.User = Each User
      If Lower(User\Channel\Name$) = ch$ And NS(User\Name$) = NS(GetFromNick(tLine$)) Then
        AddTextAreaText User\Channel\MsgBox, GetFromNick(tLine$) + " has Left " + ch$ + "." + Chr(10)
        User\DeleteNow = 1
        ActualUserList(User\Channel)
        Delete User
      End If
    Next
  ElseIf TheText$ = "JOIN" Then
    Nick$ = GetFromNick(tLine$)
    TheCha$ = GetPar(tLine$,2,":")
    For Channel.Channel = Each Channel
      If Lower(Replace(Channel\Name$,"#","")) = Lower(Replace(TheCha$,"#","")) Then
        If Nick$ = NickName$ Then
          AddTextAreaText Channel\MsgBox, "You has joined " + TheCha$ + "." + Chr(10)
        Else
          AddTextAreaText Channel\MsgBox, Nick$ + " has joined " + TheCha$ + "." + Chr(10)
        End If
        User.User = New User
        User\Name$ = Nick$
        User\Channel = Channel
        ActualUserList(Channel)
      End If
    Next
  ElseIf TheText$ = "NICK" Then
    OldNick$ = GetFromNick(tLine$)
    NewNick$ = GetPar(tLine$,2,":")
    For User.User = Each User
      If User\Name$ = OldNick$ Then
        AddTextAreaText User\Channel\MsgBox, OldNick$ + " is now as " + NewNick$ + "." + Chr(10)
        User\Name$ = NewNick$
        ActualUserList(User\Channel)
      End If
    Next

    For Query.Query = Each Query
      If Query\Name$ = OldNick$ Then
        Query\Name$ = NewNick$
        SetGadgetText Query\Window, "["+Server$+"] :: Query with " + Query\Name$
      End If
    Next
  ElseIf TheText$ = "KICK" Then
    ANick$ = GetPar(tLine$, 2)
    BNick$ = GetFromNick(tLine$)
    Chan$ = GetPar(tLine$, 1)
    For Channel.Channel = Each Channel
      If Channel\Name$ = Chan$ Then
        If ANick$ = NickName$ Then
          AddTextAreaText Channel\MsgBox, "You was kicked by " + BNick$ + Chr$(10)
          AddTextAreaText Channel\MsgBox, "You rejoin now ..." + Chr$(10)
          WriteLine Network, "JOIN " + Chan$
        Else
          AddTextAreaText Channel\MsgBox, ANick$ + " was kicked by " + BNick$ + Chr$(10)
        End If
      End If
    Next
  ElseIf TheText$ = "MODE" Then
    Chan$ = GetPar(tLine$, 1)
    Nick$ = GetFromNick(tLine$)
    Art$ = GetPar(tLine$, 2)
    Where$ = GetPar(tLine$, 3)
    For Channel.Channel = Each Channel
      If Channel\Name$ = Chan$ Then
        AddTextAreaText Channel\MsgBox, Nick$ + " sets Mode: " + Art$ + " " + Where$ + Chr$(10)
      End If
    Next
  ElseIf TheText$ = "TOPIC" Then
    Nick$ = GetFromNick(tLine$)
    Chan$ = GetPar(tLine$,1)
    topic$ = GetContext(tLine$)
    For Channel.Channel = Each Channel
      If Channel\Name$ = Chan$ Then
        AddTextAreaText Channel\MsgBox, Nick$ + " changes Topic to '"+topic$+"'" + Chr$(10)
      End If
    Next
;    RuntimeError Chan$
  ElseIf "401" Then
;    AddTextAreaText NetMsg, "AddError[401]: No such nick/channel." + Chr$(10)
  ElseIf "402" Then
    AddTextAreaText NetMsg, "AddError[402]: No such server." + Chr$(10)
  ElseIf "403" Then
    AddTextAreaText NetMsg, "AddError[403]: No such channel." + Chr$(10)
  ElseIf "403" Then
    AddTextAreaText NetMsg, "AddError[403]: No such channel." + Chr$(10)
  ElseIf "404" Then
    AddTextAreaText NetMsg, "Error[404]: Cannot send to channel."
  ElseIf "405" Then
    AddTextAreaText NetMsg, "Error[405]: You cannot join anymore channels."
  ElseIf "406" Then
    AddTextAreaText NetMsg, "Error[406]: There was no such nickname."
  ElseIf "407" Then
    AddTextAreaText NetMsg, "Error[407]: Duplicate recipients, no message delivered."
  ElseIf "409" Then
    AddTextAreaText NetMsg, "Error[409]: No orgin specified."
  ElseIf "411" Then
    AddTextAreaText NetMsg, "Error[411]: No recipient given."
  ElseIf "412" Then
    AddTextAreaText NetMsg, "Error[412]: No text to send."
  ElseIf "413" Then
    AddTextAreaText NetMsg, "Error[413]: No top level domain specified."
  ElseIf "414" Then
    AddTextAreaText NetMsg, "Error[414]: Wildcard in top level domain."
  ElseIf "421" Then
    AddTextAreaText NetMsg, "Error[421]: Unknown command."
  ElseIf "422" Then
    AddTextAreaText NetMsg, "Error[422]: MOTD file missing."
  ElseIf "423" Then
    AddTextAreaText NetMsg, "Error[423]: No addministrative info available."
  ElseIf "424" Then
    AddTextAreaText NetMsg, "Error[424]: File error."
  ElseIf "431" Then
    AddTextAreaText NetMsg, "Error[431]: No nickname given."
  ElseIf "432" Then
    AddTextAreaText NetMsg, "Error[432]: Erroneus nickname."
  ElseIf "433" Then
    AddTextAreaText NetMsg, "Error[433]: Nickname already in use."
  ElseIf "436" Then
    AddTextAreaText NetMsg, "Error[436]: Nickname Collision KILL"
  ElseIf "441" Then
    AddTextAreaText NetMsg, "Error[441]: They aren't on that channel."
  ElseIf "442" Then
    AddTextAreaText NetMsg, "Error[442]: You're not on that channel."
  ElseIf "443" Then
    AddTextAreaText NetMsg, "Error[443]: User already in that channel."
  ElseIf "444" Then
    AddTextAreaText NetMsg, "Error[444]: User not logged on."
  ElseIf "445" Then
    AddTextAreaText NetMsg, "Error[445]: SUMMON has been disabled."
  ElseIf "446" Then
    AddTextAreaText NetMsg, "Error[446]: USERS has been disabled."
  ElseIf "451" Then
    AddTextAreaText NetMsg, "Error[451]: You have not registered."
  ElseIf "461" Then
    AddTextAreaText NetMsg, "Error[461]: Not enough parameters."
  ElseIf "462" Then 
    AddTextAreaText NetMsg, "Error[462]: You may not register."
  ElseIf "463" Then
    AddTextAreaText NetMsg, "Error[463]: You're host isn't among privliged."
  ElseIf "464" Then
    AddTextAreaText NetMsg, "Error[464]: Password incorecct."
  ElseIf "465" Then
    AddTextAreaText NetMsg, "Error[465]: You are banned from this server."
  ElseIf "467" Then
    AddTextAreaText NetMsg, "Error[467]: Channel key already set."
  ElseIf "471" Then
    AddTextAreaText NetMsg, "Error[471]: Cannot join channel (+l)."
  ElseIf "472" Then
    AddTextAreaText NetMsg, "Error[472]: Uknown mode."
  ElseIf "473" Then
    AddTextAreaText NetMsg, "Error[473]: Cannot join channel (+i)."
  ElseIf "474" Then
    AddTextAreaText NetMsg, "Error[474]: Cannot join channel (+b)."
  ElseIf "475" Then
    AddTextAreaText NetMsg, "Error[474]: Cannot join channel (+k)."
  ElseIf "481" Then
    AddTextAreaText NetMsg, "Error[481]: Permission denied, you are not an IRC operator."
  ElseIf "482" Then
    AddTextAreaText NetMsg, "Error[482]: You're not a channel operator."
  ElseIf "483" Then
    AddTextAreaText NetMsg, "Error[483]: You cant kill a server!"
  ElseIf "491" Then
    AddTextAreaText NetMsg, "Error[491]: No O-Lines from your host."
  ElseIf "501" Then
    AddTextAreaText NetMsg, "Error[501]: Uknown mode flag."
  ElseIf "502" Then
    AddTextAreaText NetMsg, "Error[502]: Can't change mode for other users."
  Else
;    AddTextAreaText NetMsg, tLine$ + Chr$(10)
  End If
;  AddTextAreaText NetMsg, tLine$ + Chr$(10)
End Function

Function OpenQuery.Query(Name$)
  wW = 400 : wH = 375 : iH = 20 : bW = 40
  Query.Query = New Query
  Query\Name$ = Name$
  Query\Window = CreateWindow("["+Server$+"] :: Query with " + Query\Name$, ClientWidth(Desktop())/4-wW/2+fA, ClientHeight(Desktop())/2-wH/2, wW, wH, NetWin, 1)
  Query\MsgBox = CreateTextArea(-1, 0, wW-3, wH-28-iH, Query\Window)
  Query\InpBox = CreateTextField(0, wH-26-iH, wW-6-bW, iH, Query\Window)
  Query\InpBut = CreateButton("Send", wW-6-bW, wH-26-iH, bW, iH, Query\Window)
  Return Query
End Function

Function NS$(jj$)
  Return Replace(Replace(Replace(Replace(jj$, "!", ""), "-", ""), "+", ""), "@", "")
End Function

Function ActualUserList(Channel.Channel)
  ClearGadgetItems Channel\UseBox
  For User.User = Each User
    If User\Channel = Channel Then
      If User\DeleteNow = 0 Then AddGadgetItem Channel\UseBox, User\Name$
    End If
  Next
;  AddGadgetItem Channel\UseBox, ""
;  AddGadgetItem Channel\UseBox, NickName$
End Function

Function GetContext$(msg$)
	Local break1%=Instr(msg,":")
	Local break2%=Instr(msg,":",break1+1)

	Return Mid(msg,break2+1)
End Function

Function GetFromNick$(msg$)
	Return Mid(msg,Instr(msg,":")+1,(Instr(msg,"!")-Instr(msg,":")-1))
End Function

Function GetPar$(msg$,par%=0,bChr$=" ")
	Local sbreak%=1,ebreak%
	
	For i=0 To par
		If Instr(msg,bChr,sbreak+1)
			sbreak=Instr(msg,bChr,sbreak+1)
		Else
			Exit
		EndIf
	Next
	
	ebreak=Instr(msg,bChr,sbreak+1)
	If ebreak
		Return Mid(msg,sbreak+1,ebreak-sbreak-1)
	Else
		Return Mid(msg,sbreak+1)
	EndIf
End Function

Function OpenIRCSession(stream,timeout%=2000)
	Local time=MilliSecs()
	
	While Not stream
		If (MilliSecs()-time)>timeout Return 0
	Wend
    Hostname$ = "BlitzPlus Irc"
	WriteLine stream,"USER "+NickName$+" "+Hostname$+" "+Server$+" :"+NickName$
	WriteLine stream,"NICK "+NickName$
	Return Stream
End Function
