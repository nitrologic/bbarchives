; ID: 1270
; Author: Vertex
; Date: 2005-01-28 17:47:01
; Title: Socket
; Description: Socketlib

Strict

Type TSockAddr
	Field shSinFamily:Short 
	Field shSinPort:Short 
	Field iSinAddr:Int 
	Field lSinZero:Long
End Type 

' Winsock only!
Type TFD_Set
	Field iFD_Count:Int
	Field iFD_Array:Int[64]
End Type

Type TTimeval
	Field iTV_Sec:Int  ' Seconds
	Field iTV_USec:Int ' Milliseconds
End Type

' Winsock only!
Type TWSAData
   Field shVersion:Short 
   Field shHighVersion:Short 
   Field sDescription:String
   Field sSystemStatus:String
   Field shMaxSockets:Short 
   Field shMaxUdpDg:Short 
   Field pbVendorInfo:Byte Ptr 
End Type 

Const IOCPARM_MASK              = $7F
Const IOC_VOID                  = $20000000
Const IOC_OUT                   = $40000000
Const IOC_IN                    = $80000000
Const IOC_INOUT                 = (IOC_IN | IOC_OUT)

Const IPPROTO_IP                = 0
Const IPPROTO_ICMP              = 1
Const IPPROTO_IGMP              = 2
Const IPPROTO_GGP               = 3
Const IPPROTO_TCP               = 6
Const IPPROTO_PUP               = 12
Const IPPROTO_UDP               = 17
Const IPPROTO_IDP               = 22
Const IPPROTO_ND                = 77
Const IPPROTO_RAW               = 255
Const IPPROTO_MAX               = 256
Const IPPORT_ECHO               = 7
Const IPPORT_DISCARD            = 9
Const IPPORT_SYSTAT             = 11
Const IPPORT_DAYTIME            = 13
Const IPPORT_NETSTAT            = 15
Const IPPORT_FTP                = 21
Const IPPORT_TELNET             = 23
Const IPPORT_SMTP               = 25
Const IPPORT_TIMESERVER         = 37
Const IPPORT_NAMESERVER         = 42
Const IPPORT_WHOIS              = 43
Const IPPORT_MTP                = 57
Const IPPORT_TFTP               = 69
Const IPPORT_RJE                = 77
Const IPPORT_FINGER             = 79
Const IPPORT_TTYLINK            = 87
Const IPPORT_SUPDUP             = 95
Const IPPORT_EXECSERVER         = 512
Const IPPORT_LOGINSERVER        = 513
Const IPPORT_CMDSERVER          = 514
Const IPPORT_EFSSERVER          = 520
Const IPPORT_BIFFUDP            = 512
Const IPPORT_WHOSERVER          = 513
Const IPPORT_ROUTESERVER        = 520
Const IPPORT_RESERVED           = 1024
Const IMPLINK_IP                = 155
Const IMPLINK_LOWEXPER          = 156
Const IMPLINK_HIGHEXPER         = 158

Const WSADESCRIPTION_LEN        = 256
Const WSASYS_STATUS_LEN         = 128

Const INADDR_ANY	              = 0
Const INADDR_LOOPBACK	        = $7F000001
Const INADDR_BROADCAST	        = $FFFFFFFF
Const INADDR_NONE	              = $FFFFFFFF
Const IP_OPTIONS                = 1
Const SO_DEBUG                  = 1
Const SO_ACCEPTCONN             = 2
Const SO_REUSEADDR              = 4
Const SO_KEEPALIVE              = 8
Const SO_DONTROUTE              = 16
Const SO_BROADCAST              = 32
Const SO_USELOOPBACK            = 64
Const SO_LINGER                 = 128
Const SO_OOBINLINE              = 256
Const SO_DONTLINGER             = ~SO_LINGER
Const SO_SNDBUF                 = $1001
Const SO_RCVBUF                 = $1002
Const SO_SNDLOWAT               = $1003
Const SO_RCVLOWAT               = $1004
Const SO_SNDTIMEO               = $1005
Const SO_RCVTIMEO               = $1006
Const SO_ERROR                  = $1007
Const SO_TYPE                   = $1008
      
Const IP_MULTICAST_IF           = 2
Const IP_MULTICAST_TTL          = 3
Const IP_MULTICAST_LOOP         = 4
Const IP_ADD_MEMBERSHIP         = 5
Const IP_DROP_MEMBERSHIP        = 6

Const IP_DEFAULT_MULTICAST_TTL  = 1
Const IP_DEFAULT_MULTICAST_LOOP = 1
Const IP_MAX_MEMBERSHIPS        = 20

Const INVALID_SOCKET            = (-1)
Const SOCKET_ERROR              = -1
Const SOCK_STREAM               = 1
Const SOCK_DGRAM                = 2
Const SOCK_RAW                  = 3
Const SOCK_RDM                  = 4
Const SOCK_SEQPACKET            = 5
Const TCP_NODELAY               = $0001
Const AF_UNSPEC                 = 0
Const AF_UNIX                   = 1
Const AF_INET                   = 2
Const AF_IMPLINK                = 3
Const AF_PUP                    = 4
Const AF_CHAOS                  = 5
Const AF_IPX                    = 6
Const AF_NS                     = 6
Const AF_ISO                    = 7
Const AF_OSI                    = AF_ISO
Const AF_ECMA                   = 8
Const AF_DATAKIT                = 9
Const AF_CCITT                  = 10
Const AF_SNA                    = 11
Const AF_DECnet                 = 12
Const AF_DLI                    = 13
Const AF_LAT                    = 14
Const AF_HYLINK                 = 15
Const AF_APPLETALK              = 16
Const AF_NETBIOS                = 17
Const AF_VOICEVIEW              = 18
Const AF_FIREFOX                = 19
Const AF_UNKNOWN1               = 20
Const AF_BAN                    = 21
Const AF_ATM                    = 22
Const AF_INET6                  = 23
Const AF_MAX                    = 24

Const PF_UNSPEC                 = AF_UNSPEC
Const PF_UNIX                   = AF_UNIX
Const PF_INET                   = AF_INET
Const PF_IMPLINK                = AF_IMPLINK
Const PF_PUP                    = AF_PUP
Const PF_CHAOS                  = AF_CHAOS
Const PF_NS                     = AF_NS
Const PF_IPX                    = AF_IPX
Const PF_ISO                    = AF_ISO
Const PF_OSI                    = AF_OSI
Const PF_ECMA                   = AF_ECMA
Const PF_DATAKIT                = AF_DATAKIT
Const PF_CCITT                  = AF_CCITT
Const PF_SNA                    = AF_SNA
Const PF_DECnet                 = AF_DECnet
Const PF_DLI                    = AF_DLI
Const PF_LAT                    = AF_LAT
Const PF_HYLINK                 = AF_HYLINK
Const PF_APPLETALK              = AF_APPLETALK
Const PF_VOICEVIEW              = AF_VOICEVIEW
Const PF_FIREFOX                = AF_FIREFOX
Const PF_UNKNOWN1               = AF_UNKNOWN1
Const PF_BAN                    = AF_BAN
Const PF_ATM                    = AF_ATM
Const PF_INET6                  = AF_INET6
Const PF_MAX                    = AF_MAX
Const SOL_SOCKET                = $FFFF
Const SOMAXCONN                 = 5

Const MSG_OOB                   = 1
Const MSG_PEEK                  = 2
Const MSG_DONTROUTE             = 4

Const MSG_MAXIOVLEN             = 16
Const MSG_PARTIAL               = $8000
Const MAXGETHOSTSTRUCT          = 1024
Const FD_READ                   = 1
Const FD_WRITE                  = 2
Const FD_OOB                    = 4
Const FD_ACCEPT                 = 8
Const FD_CONNECT                = 16
Const FD_CLOSE                  = 32

' Winsock only!
Const WSABASEERR                = 10000
Const WSAEINTR                  = (WSABASEERR+4)
Const WSAEBADF                  = (WSABASEERR+9)
Const WSAEACCES                 = (WSABASEERR+13)
Const WSAEFAULT                 = (WSABASEERR+14)
Const WSAEINVAL                 = (WSABASEERR+22)
Const WSAEMFILE                 = (WSABASEERR+24)
Const WSAEWOULDBLOCK            = (WSABASEERR+35)
Const WSAEINPROGRESS            = (WSABASEERR+36)
Const WSAEALREADY               = (WSABASEERR+37)
Const WSAENOTSOCK               = (WSABASEERR+38)
Const WSAEDESTADDRREQ           = (WSABASEERR+39)
Const WSAEMSGSIZE               = (WSABASEERR+40)
Const WSAEPROTOTYPE             = (WSABASEERR+41)
Const WSAENOPROTOOPT            = (WSABASEERR+42)
Const WSAEPROTONOSUPPORT        = (WSABASEERR+43)
Const WSAESOCKTNOSUPPORT        = (WSABASEERR+44)
Const WSAEOPNOTSUPP             = (WSABASEERR+45)
Const WSAEPFNOSUPPORT           = (WSABASEERR+46)
Const WSAEAFNOSUPPORT           = (WSABASEERR+47)
Const WSAEADDRINUSE             = (WSABASEERR+48)
Const WSAEADDRNOTAVAIL          = (WSABASEERR+49)
Const WSAENETDOWN               = (WSABASEERR+50)
Const WSAENETUNREACH            = (WSABASEERR+51)
Const WSAENETRESET              = (WSABASEERR+52)
Const WSAECONNABORTED           = (WSABASEERR+53)
Const WSAECONNRESET             = (WSABASEERR+54)
Const WSAENOBUFS                = (WSABASEERR+55)
Const WSAEISCONN                = (WSABASEERR+56)
Const WSAENOTCONN               = (WSABASEERR+57)
Const WSAESHUTDOWN              = (WSABASEERR+58)
Const WSAETOOMANYREFS           = (WSABASEERR+59)
Const WSAETIMEDOUT              = (WSABASEERR+60)
Const WSAECONNREFUSED           = (WSABASEERR+61)
Const WSAELOOP                  = (WSABASEERR+62)
Const WSAENAMETOOLONG           = (WSABASEERR+63)
Const WSAEHOSTDOWN              = (WSABASEERR+64)
Const WSAEHOSTUNREACH           = (WSABASEERR+65)
Const WSAENOTEMPTY              = (WSABASEERR+66)
Const WSAEPROCLIM               = (WSABASEERR+67)
Const WSAEUSERS                 = (WSABASEERR+68)
Const WSAEDQUOT                 = (WSABASEERR+69)
Const WSAESTALE                 = (WSABASEERR+70)
Const WSAEREMOTE                = (WSABASEERR+71)
Const WSAEDISCON                = (WSABASEERR+101)
Const WSASYSNOTREADY            = (WSABASEERR+91)
Const WSAVERNOTSUPPORTED        = (WSABASEERR+92)
Const WSANOTINITIALISED         = (WSABASEERR+93)
Const WSAHOST_NOT_FOUND         = (WSABASEERR+1001)
Const WSATRY_AGAIN              = (WSABASEERR+1002)
Const WSANO_RECOVERY            = (WSABASEERR+1003)
Const WSANO_DATA                = (WSABASEERR+1004)


Extern "Os"
	Function s_accept:Int(iSocket:Int, tAddr:Byte Ptr, ipAddrLen:Int Ptr) = "accept@12" 
	Function s_bind:Int(iSocket:Int, tName:Byte Ptr, iNameLen:Int) = "bind@12" 
	Function s_closesocket:Int(iSocket:Int) = "closesocket@4" 
	Function s_connect:Int(iSocket:Int, tName:Byte Ptr, iNameLen:Int) = "connect@12" 
	Function s_getpeername:Int(iSocket:Int, tName:Byte Ptr, ipNameLen:Int Ptr) = "getpeername@12" 
	Function s_getsockname:Int(iSocket:Int, tName:Byte Ptr, ipNameLen:Int Ptr) = "getsockname@12" 
	Function s_getsockopt:Int(iSocket:Int, iLevel:Int, iOptname:Int, pOptval:Byte Ptr, ipOptlen:Int Ptr) = "getsockopt@20" 
	Function s_htonl:Int(iHost:Int) = "htonl@4" 
	Function s_htons:Short(shHost:Short) = "htons@4" 
	Function w_inet_addr:Int(sDottedIP:Byte Ptr) = "inet_addr@4" 
	Function w_inet_ntoa:Byte Ptr(iAddr:Int) = "inet_ntoa@4" 
	Function s_ioctlsocket:Int(iSocket:Int, iCmd:Int, pArqp:Byte Ptr) = "ioctlsocket@12" 
	Function s_listen:Int(iSocket:Int, iBacklog:Int) = "listen@8" 
	Function s_ntohl:Int(iNet:Int) = "ntohl@4" 
	Function s_ntohs:Int(shNet:Short) = "ntohs@4" 
	Function s_recv:Int(iSocket:Int, pBuf:Byte Ptr, iLen:Int, iFlags:Int) = "recv@16" 
	Function s_recvfrom:Int(iSocket:Int, pBuf:Byte Ptr, iLen:Int, iFlags:Int, tFrom:Byte Ptr, ipFromLen:Int Ptr) = "recvfrom@24" 
	Function w_select:Int(iNFDS:Int, tReadFds:Byte Ptr, tWriteFds:Byte Ptr, tExceptFds:Byte Ptr, tTimeout:Byte Ptr) = "select@20" 
	Function s_send:Int(iSocket:Int, pBuf:Byte Ptr, iLen:Int, iFlags:Int) = "send@16" 
	Function s_sendto:Int(iSocket:Int, pBuf:Byte Ptr, iLen:Int, iFlags:Int, tTo:Byte Ptr, iToLen:Int) = "sendto@24" 
	Function s_setsockopt:Int(iSocket:Int, iLevel:Int, iOptname:Int, pOptval:Byte Ptr, iOptlen:Int) = "setsockopt@20" 
	Function s_shutdown:Int(iSocket:Int, iHow:Int) = "shutdown@8" 
	Function s_socket:Int(iDomain:Int, iType:Int, iProtocol:Int) = "socket@12" 

	Function w_gethostbyaddr:Byte Ptr(ipAddr:Byte Ptr, iLen:Int, iType:Int) = "gethostbyaddr@12"
	Function w_gethostbyname:Byte Ptr(pName:Byte Ptr) = "gethostbyname@4"
	Function w_gethostname:Int(pName:Byte Ptr, iNameLen:Int) = "gethostname@8"
	Function w_getprotobynumber:Byte Ptr(iNumber:Int) = "getprotobynumber@4"
	Function w_getprotobyname:Byte Ptr(pName:Byte Ptr) = "getprotobyname@4"
	Function w_getservbyname:Byte Ptr(pName:Byte Ptr, pProto:Byte Ptr = Null) = "getservbyname@8"
	Function w_getservbyport:Byte Ptr(iPort:Int, pProto:Byte Ptr) = "getservbyport@8"

	 ' Winsock only! 
	Function w_WSAStartup:Int(shVersion:Short, tWSA:Byte Ptr) = "WSAStartup@8" 
	Function s_WSAGetLastError:Int() = "WSAGetLastError@0" 
	Function s_WSACleanup:Int() = "WSACleanup@0"
	Function w_WSAFDIsSet:Int(iSocket:Int,pSet:Byte Ptr) = "__WSAFDIsSet@8"
	
	Function MemCopy2(dest:byte Ptr, src:Int, size:INt) = "bbMemCopy"
End Extern

Function MAKESHORT:Short(bA:Byte, bB:Byte)
	Return bA | (bB Shl 8) 
End Function

Function MAKEINT:Int(shA:Short, shB:Short)
	Return shA | (shB Shl 16) 
End Function

' FD Structure - Winsock only!
Function s_FD_CLR(iSocket, tSet:TFD_SET)
	Local bIndex:Byte, bFound

	For bIndex = 1 To tSet.iFD_Count
		If tSet.iFD_Array[bIndex-1] = iSocket Then
			bFound = bIndex
			Exit
		EndIf
	Next
	
	For bIndex = bFound To tSet.iFD_Count-1
		tSet.iFD_Array[bIndex-1] = tSet.iFD_Array[bIndex+1]
	Next
	
	tSet.iFD_Count = tSet.iFD_Count-1
End Function

Function s_FD_SET(iSocket, tSet:TFD_SET)
	Local bIndex:Byte, bFound:Byte
	
	For bIndex = 1 To tSet.iFD_Count
		If tSet.iFD_Array[bIndex-1] = iSocket Then
			bFound = bIndex
			Exit
		EndIf
	Next
	
	If bFound = 0 And tSet.iFD_Count < 65 Then
		tSet.iFD_Array[tSet.iFD_Count] = iSocket
		tSet.iFD_Count = tSet.iFD_Count+1
	EndIf
End Function

Function s_FD_ZERO(tSet:TFD_SET)
	tSet.iFD_Count = 0
End Function

Function s_FD_ISSET(iSocket:Int, tSet:TFD_SET)
	Local bIndex:Byte, tTemp:TBank, iReturn:Int
	
	tTemp = CreateBank(4+4*64)
	PokeInt tTemp, 0, tSet.iFD_Count
	For bIndex = 0 To 63
		PokeInt tTemp, 4+bIndex*4, tSet.iFD_Array[bIndex]
	Next
	
	iReturn = w_WSAFDIsSet(iSocket, BankBuf(tTemp))
	'Release tTemp
	
	Return iReturn
End Function

Function s_inet_addr:Int(sDottedIP:String)
	Return w_inet_addr(sDottedIP.ToCString())
End Function

Function s_inet_ntoa:String(iAddr:Int)
	Return "".FromCString(w_inet_ntoa(iAddr))
End Function

Function s_select(iSocket:Int, tReadFDS:TFD_SET, tWriteFDS:TFD_SET, tExceptFDS:TFD_SET, tTimeout:TTimeval)
	Local tTempRead:TBank, tTempWrite:TBank, tTempExcept:TBank, bIndex:Byte
	Local pReadFDS:Byte Ptr
	Local pWriteFDS:Byte Ptr
	Local pExceptFDS:Byte Ptr
	Local iReturn:Int
	
	If tReadFDS Then
		tTempRead = CreateBank(4+4*64)
		pReadFDS = BankBuf(tTempRead)
		PokeInt tTempRead, 0, tReadFDS.iFD_Count
		For bIndex = 0 To 63
			PokeInt tTempRead, 4+bIndex*4, tReadFDS.iFD_Array[bIndex]
		Next	
	Else
		pReadFDS = Null
	End If
	
	If tWriteFDS Then
		tTempWrite = CreateBank(4+4*64)
		pWriteFDS = BankBuf(tTempWrite)
		PokeInt tTempWrite, 0, tWriteFDS.iFD_Count
		For bIndex = 0 To 63
			PokeInt tTempWrite, 4+bIndex*4, tWriteFDS.iFD_Array[bIndex]
		Next	
	Else
		pWriteFDS = Null
	End If
	
	If tExceptFDS Then
		tTempExcept = CreateBank(4+4*64)
		pExceptFDS = BankBuf(tTempExcept)
		PokeInt tTempExcept, 0, tExceptFDS.iFD_Count
		For bIndex = 0 To 63
			PokeInt tTempExcept, 4+bIndex*4, tExceptFDS.iFD_Array[bIndex]
		Next	
	Else
		pExceptFDS = Null
	End If
	
	iReturn = w_select(iSocket, pReadFDS, pWriteFDS, pExceptFDS, tTimeout)
	
	If tReadFDS Then
		tReadFDS.iFD_Count = PeekInt(tTempRead, 0)
		For bIndex = 0 To 63
			tReadFDS.iFD_Array[bIndex] = PeekInt(tTempRead, 4+bIndex*4)
		Next
		' Release tTempRead
	End If
	
	If tWriteFDS Then
		tWriteFDS.iFD_Count = PeekInt(tTempWrite, 0)
		For bIndex = 0 To 63
			tWriteFDS.iFD_Array[bIndex] = PeekInt(tTempWrite, 4+bIndex*4)
		Next
		' Release tTempWrite
	End If
	
	If tExceptFDS Then
		tExceptFDS.iFD_Count = PeekInt(tTempExcept, 0)
		For bIndex = 0 To 63
			tExceptFDS.iFD_Array[bIndex] = PeekInt(tTempExcept, 4+bIndex*4)
		Next
		' Release tTempExcept
	End If
	
	Return iReturn
End Function

Function s_gethostbyaddr:String(iAddr:Int)
	Local pHostent:Byte Ptr, tHostent:TBank, bName:Byte[255]
	
	If iAddr = 0 Then Return ""
	pHostent = w_gethostbyaddr(Varptr iAddr, 4, PF_INET)
	If Not pHostent Then Return ""
	tHostent = CreateBank(16)
	MemCopy(BankBuf(tHostent), pHostent, 16)
	MemCopy2(bName, PeekInt(tHostent, 0), 255)
	'Release tHostent
	
	Return "".FromCString(bName)
End Function

Function s_gethostbyname:Int(sURL:String)
	Local iIP:Int, tpHostent:Byte Ptr, tHostent:TBank, ipIP:Int Ptr
	
	If sURL = "" Then Return 0
	iIP = s_inet_addr(sURL)
	If iIP <> INADDR_NONE Then
		Return iIP
	Else
		tpHostent = w_gethostbyname(sURL.ToCString())
		If Not tpHostent Then Return 0
		tHostent = CreateBank(16)
		MemCopy(BankBuf(tHostent), tpHostent, 16)       ' tHostent = *tpHostent
		MemCopy2(Varptr ipIP, PeekInt(tHostent, 12), 4) ' iiIP = tHostent.piAddrList[]
		'Release tHostent
		Return Var ipIP
	End If
End Function

Function s_gethostname:Int(sName:String Var)
	Local tBuffer:TBank, iResult:Int
	
	tBuffer = CreateBank(255)
	iResult = w_gethostname(BankBuf(tBuffer), 255)
	If iResult = SOCKET_ERROR Then
		'Release tBuffer
		Return SOCKET_ERROR
	Else
		sName = "".FromCString(BankBuf(tBuffer))
		'Release tBuffer
		Return iResult
	EndIf
End Function

Function s_getprotobynumber:String(iNumber:Int)
	Local pProtoent:Byte Ptr, tProtoent:TBank, bName:Byte[255]
	
	pProtoent = w_getprotobynumber(iNumber)
	If Not pProtoent Then Return ""
	tProtoent = CreateBank(10)
	MemCopy(BankBuf(tProtoent), pProtoent, 10)
	MemCopy2(bName, PeekInt(tProtoent, 0), 255)
	'Release tProtoent
	Return "".FromCString(bName)
End Function


Function s_getprotobyname:Short(sName:String)
	Local pProtoent:Byte Ptr, tProtoent:TBank, shPort:Short
	
	pProtoent = w_getprotobyname(sName.ToCString())
	If Not pProtoent Then Return 0
	tProtoent = CreateBank(10)
	MemCopy(BankBuf(tProtoent), pProtoent, 10)
	shPort = PeekShort(tProtoent, 8)
	'Release tProtoent
	Return shPort
End Function

Function s_getservbyname:String(sName:String, sProto:String)
	Local pServent:Byte Ptr, tServent:TBank, bName:Byte[255]
	
	pServent = w_getservbyname(sName.ToCString(), sProto.ToCString())
	If Not pServent Then Return ""
	tServent = CreateBank(14)
	MemCopy(BankBuf(tServent), pServent, 14)
	MemCopy2(bName, PeekInt(tServent, 0), 255)
	'Release tServent
	Return "".FromCString(bName)
End Function

Function s_getservbyport:String(iPort:Int, sProto:String)
	Local pServent:Byte Ptr, tServent:TBank, bName:Byte[255]
	
	pServent = w_getservbyport(iPort, sProto.ToCString())
	If Not pServent Then Return
	tServent = CreateBank(14)
	MemCopy(BankBuf(tServent), pServent, 14)
	MemCopy2(bName, PeekInt(tServent, 0), 255)
	'Release tServent
	Return "".FromCString(bName)
End Function

Function s_WSAStartup(shVersion:Short, tWSA:TWSAData Var)
	Local bTemp:TBank, iReturn:Int, bChar:Byte, shIndex:Short
	Local sDescription:String, sSystemStatus:String
	
	bTemp = CreateBank(400)
	
	iReturn = w_WSAStartup(shVersion, BankBuf(bTemp))
	If iReturn <> 0 Then
		Return iReturn
	Else
		For shIndex = 4 To 260
			bChar = PeekByte(bTemp, shIndex)
			If bChar = 0 Then Exit
			sDescription = sDescription+Chr(bChar)
		Next
		
		For shIndex = 261 To 359
			bChar = PeekByte(bTemp, shIndex)
			If bChar = 0 Then Exit
			sSystemStatus = sSystemStatus+Chr(bChar)
		Next
		
		'MemCopy2(Varptr tWSA.pbVendorInfo , PeekInt(bTemp, 396), 4) ' no one need this :)
	
		tWSA.shVersion     = PeekShort(bTemp, 0)
		tWSA.shHighVersion = PeekShort(bTemp, 2)
		tWSA.sDescription  = sDescription
		tWSA.sSystemStatus = sSystemStatus
		tWSA.shMaxSockets  = PeekShort(bTemp, 390)
		tWSA.shMaxUdpDg    = PeekShort(bTemp, 392)

		Return iReturn
	EndIf
End Function
