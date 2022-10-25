; ID: 3217
; Author: Spencer
; Date: 2015-08-01 03:09:46
; Title: XMLHttpRequest
; Description: Send Synchronous GET or POST Requests

Rem
    FILE: XMLHttpRequest.bmx
    
    INFO: Contains a static Type that provides
          GET and POST functions to access web sites.
          Results are returned as strings 

    EX:   XMLHttpRequest.GET("http://www.google.com")
    
          XMLHttpRequest.GET("http://www.blitzbasic.co.nz/Community/topics.php", ["forum","112"])
    
          XMLHttpRequest.POST("http://www.spencerjobe.com/test/test_post.php",["test","Results"])

End Rem

Type XMLHttpRequest

    Function GET:String(Url:String, Args:String[]=Null)
    
        Local Host:String = XMLHttpRequest.GetHost(Url)
        Local IPAddress:Int = HostIp(Host)
        Local Port:Int = XMLHttpRequest.GetPort(Url)
        Local Path:String = XMLHttpRequest.GetPath(Url)
        Local ArgsString:String = XMLHttpRequest.GetArgs(Args)
        Local TCPSocket:TSocket = CreateTCPSocket()
        ConnectSocket(TCPSocket,IPAddress,Port)
        Local Stream:TSocketStream = CreateSocketStream(TCPSocket,True)
        Local RequestHeader:String
        RequestHeader = "GET " + Path + ArgsString + " HTTP/1.0~n"
        RequestHeader:+ "HOST: " + Host + "~n"
        RequestHeader:+ "Connection: close~n"
        'RequestHeader:+ "Connection: keep-alive~n"
        RequestHeader:+ "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8~n"
        RequestHeader:+ "Upgrade-Insecure-Requests: 1~n"
        RequestHeader:+ "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.125 Safari/537.36~n"
        'RequestHeader:+ "Referer: http://www.blitzbasic.co.nz/Community/_index_.php~n"
        'RequestHeader:+ "Accept-Encoding: gzip, deflate, sdch~n"
        RequestHeader:+ "Accept-Language: en-US,en;q=0.8~n"
        Print "REQUEST"
        Print RequestHeader
        Print ""
        WriteLine(Stream,RequestHeader)
        
        Local Result:String = ReadLine(Stream)
        Local NextLine:String = ""
        Local StartPos:Int = 0
        Local ContentLength:Int = 0
        Local ResponseText:String = ""
        Local BlankLineCount:Int = 0

        Repeat
            'Print "[RESPONSE]:" + Result
            If Result <> "" Then
                BlankLineCount = 0
            Else
                BlankLineCount:+1
            EndIf
            ResponseText:+ Result + "~n"
            If BlankLineCount  > 1024 Then
                Exit
            Else
                Result = ""
                If Eof(Stream) Then
                    Exit
                EndIf
                Result = ReadLine(Stream)
            EndIf
        Forever
        ResponseText = Left(ResponseText, Len(ResponseText)-BlankLineCount)
        CloseStream(Stream)
        CloseSocket(TCPSocket)
        Return ResponseText
        
    End Function

Function POST:String(Url:String, Args:String[])

        Local Host:String = XMLHttpRequest.GetHost(Url)
        Local IPAddress:Int = HostIp(Host)
        Local Port:Int = XMLHttpRequest.GetPort(Url)
        Local Path:String = XMLHttpRequest.GetPath(Url)
        Local ArgsString:String = XMLHttpRequest.GetArgs(Args,True)
        Local TCPSocket:TSocket = CreateTCPSocket()
        ConnectSocket(TCPSocket,IPAddress,Port)
        Local Stream:TSocketStream = CreateSocketStream(TCPSocket,True)
        Local PostRequest:String
        PostRequest= "POST " + Path + " HTTP/1.0~n"
        PostRequest:+ "HOST: " + Host + "~n"
        PostRequest:+ "Connection: close~n"
        PostRequest:+ "Cache-Control: Max-age=0~n"
        PostRequest:+ "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8~n"
        PostRequest:+ "Origin: http://" + Host + "~n"
        PostRequest:+ "Upgrade-Insecure-Requests: 1~n"
        PostRequest:+ "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.125 Safari/537.36~n"
        PostRequest:+ "Content-Type: application/x-www-form-urlencoded~n"
        PostRequest:+ "Accept-Language: en-US,en;q=0.8~n"
        PostRequest:+ "Content-Length:" + Len(ArgsString) + "~n"
        
        WriteLine(Stream,PostRequest)
        WriteLine(Stream,ArgsString)
        WriteLine(Stream,"")
                
        Local Result:String = ReadLine(Stream)
        Local NextLine:String = ""
        Local StartPos:Int = 0
        Local ContentLength:Int = 0
        Local ResponseText:String = ""
        Local BlankLineCount:Int = 0

        Repeat
            If Result <> "" Then
                BlankLineCount = 0
            Else
                BlankLineCount:+1
            EndIf
            ResponseText:+ Result + "~n"
            If BlankLineCount  > 1024 Then
                Exit
            Else
                Result = ""
                If Eof(Stream) Then
                    Exit
                EndIf
                Result = ReadLine(Stream)
            EndIf
        Forever
        ResponseText = Left(ResponseText, Len(ResponseText)-BlankLineCount)
        CloseStream(Stream)
        CloseSocket(TCPSocket)
        Return ResponseText
        
    End Function


    Function RemoveHttpPrefix:String(Url:String)
        Local StartPos:Int = Instr(Url,"://")
        If StartPos > 0 Then
            Url = Mid(Url,StartPos+3)
        EndIf
        If Instr(Url,"/") = 0 And Instr(Url,"?") = 0 Then
            Url = Url + "/"
        EndIf
        Return Url
    End Function

    Function GetHost:String(Url:String)
        Local FirstSlash:Int = 0
        Local FirstColon:Int = 0
        Local FirstQMark:Int = 0
        Local EndPos:Int = 0
        Url = RemoveHttpPrefix(Url)
        FirstSlash = Instr(Url,"/")
        FirstColon = Instr(Url,":")
        FirstQMark = Instr(Url,"?")
        If FirstColon > 0 And FirstColon < FirstSlash Then
            EndPos = FirstColon - 1
        ElseIf FirstQMark > 0 And FirstQMark < FirstSlash Then
            EndPos = FirstQMark - 1
        Else
            EndPos = FirstSlash - 1
        EndIf
        Return Left(Url,EndPos)
    End Function
        
    Function GetPort:Int(Url:String)
        Local FirstSlash:Int = 0
        Local FirstQMark:Int = 0
        Local FirstColon:Int = 0
        Local PortString:String = ""
        Url = RemoveHttpPrefix(Url)
        FirstColon = Instr(Url,":")
        If FirstColon > 0 Then
            Url= Mid(Url,FirstColon+1)
            FirstQMark = Instr(Url,"?")
            FirstSlash = Instr(Url,"/")
            If FirstQMark > 0 And FirstQMark < FirstSlash Then
                PortString = Left(Url,FirstQMark-1)
            Else
                PortString = Left(Url,FirstSlash-1)
            EndIf
        Else
            PortString = "80"
        EndIf
        Return Int(PortString)
    End Function

    Function GetPath:String(Url:String)
        Local Path:String = ""
        Local FirstSlash:Int = 0
        Local FirstQMark:Int = 0
        Url = RemoveHttpPrefix(Url)
        FirstSlash = Instr(Url,"/")
        FirstQMark = Instr(Url,"?")
        If FirstQMark > 0 And FirstQMark < FirstSlash Then
            Path = "/"
        ElseIf FirstSlash > 0 Then
            Path = Mid(Url,FirstSlash, FirstQMark-FirstSlash)
        Else
            Path = "/"
        EndIf
        Return Path
    End Function
    
    Function GetArgs:String(Args:String[]=Null, IsPostData:Int = False) 
        If Args = Null Then
            Return ""
        EndIf
        Local Index:Int = 0
        Local Name:String = ""
        Local Value:String = ""
        Local ArgsString:String = ""
        For Index = 0 To Args.Length-1 Step 2
            Name = XMLHttpRequest.EscapeString(Args[Index])
            Value = XMLHttpRequest.EscapeString(Args[Index+1])
            ArgsString:+ "&" + Name + "=" + Value
        Next
        ArgsString = Mid(ArgsString,2)
        If IsPostData Then
            Return ArgsString
        Else
            Return "?" + ArgsString
        EndIf
    End Function
    
    
    Function EscapeString:String(Value:String)
    
        Local EscapedValue:String = ""
        Local CharIndex:Int = 0
        
        For CharIndex = 1 To Value.Length
            EscapedValue :+ GetEscapeChar(Mid(Value,CharIndex,1))
        Next
        
        Return EscapedValue
        
    End Function
    
    
    Function GetEscapeChar:String(Char:String)
        Select Char
            Case "~t" ; Return "%09"
            Case "~n" ; Return "%0A"
            Case "~r" ; Return "%0D"
            Case " "  ; Return "%20"
            Case "!"  ; Return "%21"
            Case "~q" ; Return "%22"
            Case "#"  ; Return "%23"
            Case "$"  ; Return "%24"
            Case "%"  ; Return "%25"
            Case "&"  ; Return "%26"
            Case "'"  ; Return "%27"
            Case "("  ; Return "%28"
            Case ")"  ; Return "%29"
            Case "*"  ; Return "%2A"
            Case "+"  ; Return "%2B"
            Case ","  ; Return "%2C"
            Case "-"  ; Return "%2D"
            Case "."  ; Return "%2E"
            Case "/"  ; Return "%2F"
            Rem 
            Case "0"  ; Return "%30"
            Case "1"  ; Return "%31"
            Case "2"  ; Return "%32"
            Case "3"  ; Return "%33"
            Case "4"  ; Return "%34"
            Case "5"  ; Return "%35"
            Case "6"  ; Return "%36"
            Case "7"  ; Return "%37"
            Case "8"  ; Return "%38"
            Case "9"  ; Return "%39"
            End Rem
            Case ":"  ; Return "%3A"
            Case ";"  ; Return "%3B"
            Case "<"  ; Return "%3C"
            Case "="  ; Return "%3D"
            Case ">"  ; Return "%3E"
            Case "?"  ; Return "%3F"
            Case "@"  ; Return "%40"
            Rem 
            Case "A"  ; Return "%41"
            Case "B"  ; Return "%42"
            Case "C"  ; Return "%43"
            Case "D"  ; Return "%44"
            Case "E"  ; Return "%45"
            Case "F"  ; Return "%46"
            Case "G"  ; Return "%47"
            Case "H"  ; Return "%48"
            Case "I"  ; Return "%49"
            Case "J"  ; Return "%4A"
            Case "K"  ; Return "%4B"
            Case "L"  ; Return "%4C"
            Case "M"  ; Return "%4D"
            Case "N"  ; Return "%4E"
            Case "O"  ; Return "%4F"
            Case "P"  ; Return "%50"
            Case "Q"  ; Return "%51"
            Case "R"  ; Return "%52"
            Case "S"  ; Return "%53"
            Case "T"  ; Return "%54"
            Case "U"  ; Return "%55"
            Case "V"  ; Return "%56"
            Case "W"  ; Return "%57"
            Case "X"  ; Return "%58"
            Case "Y"  ; Return "%59"
            Case "Z"  ; Return "%5A"
            End Rem
            Case "["  ; Return "%5B"
            Case "\"  ; Return "%5C"
            Case "]"  ; Return "%5D"
            Case "^"  ; Return "%5E"
            Case "_"  ; Return "%5F"
            Case "`"  ; Return "%60"
            Rem 
            Case "a"  ; Return "%61"
            Case "b"  ; Return "%62"
            Case "c"  ; Return "%63"
            Case "d"  ; Return "%64"
            Case "e"  ; Return "%65"
            Case "f"  ; Return "%66"
            Case "g"  ; Return "%67"
            Case "h"  ; Return "%68"
            Case "i"  ; Return "%69"
            Case "j"  ; Return "%6A"
            Case "k"  ; Return "%6B"
            Case "l"  ; Return "%6C"
            Case "m"  ; Return "%6D"
            Case "n"  ; Return "%6E"
            Case "o"  ; Return "%6F"
            Case "p"  ; Return "%70"
            Case "q"  ; Return "%71"
            Case "r"  ; Return "%72"
            Case "s"  ; Return "%73"
            Case "t"  ; Return "%74"
            Case "u"  ; Return "%75"
            Case "v"  ; Return "%76"
            Case "w"  ; Return "%77"
            Case "x"  ; Return "%78"
            Case "y"  ; Return "%79"
            Case "z"  ; Return "%7A"
            End Rem
            Case "{"  ; Return "%7B"
            Case "|"  ; Return "%7C"
            Case "}"  ; Return "%7D"
            Case "~~" ; Return "%7E"
            Case "¢"  ; Return "%A2"
            Case "£"  ; Return "%A3"
            Case "¥"  ; Return "%A5"
            Case "|"  ; Return "%A6"
            Case "§"  ; Return "%A7"
            Case "«"  ; Return "%AB"
            Case "¬"  ; Return "%AC"
            Case "¯"  ; Return "%AD"
            Case "º"  ; Return "%B0"
            Case "±"  ; Return "%B1"
            Case "ª"  ; Return "%B2"
            Case ","  ; Return "%B4"
            Case "µ"  ; Return "%B5"
            Case "»"  ; Return "%BB"
            Case "¼"  ; Return "%BC"
            Case "½"  ; Return "%BD"
            Case "¿"  ; Return "%BF"
            Case "À"  ; Return "%C0"
            Case "Á"  ; Return "%C1"
            Case "Â"  ; Return "%C2"
            Case "Ã"  ; Return "%C3"
            Case "Ä"  ; Return "%C4"
            Case "Å"  ; Return "%C5"
            Case "Æ"  ; Return "%C6"
            Case "Ç"  ; Return "%C7"
            Case "È"  ; Return "%C8"
            Case "É"  ; Return "%C9"
            Case "Ê"  ; Return "%CA"
            Case "Ë"  ; Return "%CB"
            Case "Ì"  ; Return "%CC"
            Case "Í"  ; Return "%CD"
            Case "Î"  ; Return "%CE"
            Case "Ï"  ; Return "%CF"
            Case "Ð"  ; Return "%D0"
            Case "Ñ"  ; Return "%D1"
            Case "Ò"  ; Return "%D2"
            Case "Ó"  ; Return "%D3"
            Case "Ô"  ; Return "%D4"
            Case "Õ"  ; Return "%D5"
            Case "Ö"  ; Return "%D6"
            Case "Ø"  ; Return "%D8"
            Case "Ù"  ; Return "%D9"
            Case "Ú"  ; Return "%DA"
            Case "Û"  ; Return "%DB"
            Case "Ü"  ; Return "%DC"
            Case "Ý"  ; Return "%DD"
            Case "Þ"  ; Return "%DE"
            Case "ß"  ; Return "%DF"
            Case "à"  ; Return "%E0"
            Case "á"  ; Return "%E1"
            Case "â"  ; Return "%E2"
            Case "ã"  ; Return "%E3"
            Case "ä"  ; Return "%E4"
            Case "å"  ; Return "%E5"
            Case "æ"  ; Return "%E6"
            Case "ç"  ; Return "%E7"
            Case "è"  ; Return "%E8"
            Case "é"  ; Return "%E9"
            Case "ê"  ; Return "%EA"
            Case "ë"  ; Return "%EB"
            Case "ì"  ; Return "%EC"
            Case "í"  ; Return "%ED"
            Case "î"  ; Return "%EE"
            Case "ï"  ; Return "%EF"
            Case "ð"  ; Return "%F0"
            Case "ñ"  ; Return "%F1"
            Case "ò"  ; Return "%F2"
            Case "ó"  ; Return "%F3"
            Case "ô"  ; Return "%F4"
            Case "õ"  ; Return "%F5"
            Case "ö"  ; Return "%F6"
            Case "÷"  ; Return "%F7"
            Case "ø"  ; Return "%F8"
            Case "ù"  ; Return "%F9"
            Case "ú"  ; Return "%FA"
            Case "û"  ; Return "%FB"
            Case "ü"  ; Return "%FC"
            Case "ý"  ; Return "%FD"
            Case "þ"  ; Return "%FE"
            Case "ÿ"  ; Return "%FF"
            Default   ;
                If Asc(Char) = 8 Then
                    Return "%08"
                Else
                    Return Char
                EndIf
        End Select
    
    End Function

End Type
    


Print "-------------------------------------------------------------------------------------------"
Print "RESPONSE From https://www.google.com?gws_rd=ssl"
Print "-------------------------------------------------------------------------------------------"
Print XMLHttpRequest.GET("https://www.google.com",["gws_rd","ssl"])
Print "-------------------------------------------------------------------------------------------"
Print "~n~n~n"


Print "-------------------------------------------------------------------------------------------"
Print "RESPONSE From http://www.google.com"
Print "-------------------------------------------------------------------------------------------"
Print XMLHttpRequest.GET("http://www.google.com")
Print "-------------------------------------------------------------------------------------------"
Print "~n~n~n"

Print "-------------------------------------------------------------------------------------------"
Print "RESPONSE From http://www.blitzbasic.co.nz/Community/topics.php?forum=112"
Print "-------------------------------------------------------------------------------------------"
Print XMLHttpRequest.GET("http://www.blitzbasic.co.nz/Community/topics.php", ["forum","112"])
Print "-------------------------------------------------------------------------------------------"
Print "~n~n~n"

Print "-------------------------------------------------------------------------------------------"
Print "RESPONSE From http://www.spencerjobe.com/test/test_post.php" 
Print "              [x-wwww-form-urlencoded] POST_DATA[~qtest=Results~q]"
Print "-------------------------------------------------------------------------------------------"
Print XMLHttpRequest.POST("http://www.spencerjobe.com/test/test_post.php",["test","Results"])
Print "-------------------------------------------------------------------------------------------"
Print "~n~n~n"
