; ID: 3098
; Author: BlitzSupport
; Date: 2014-01-14 16:32:33
; Title: Workgroup/domain name (Windows only)
; Description: Shows workgroup or domain name for the local Windows PC on a network

' -----------------------------------------------------------------------------
' Paste at top of code, should only be called once...
' -----------------------------------------------------------------------------

Const NETSETUPUNKNOWNSTATUS:Int	= 0
Const NETSETUPUNJOINED:Int	= 1
Const NETSETUPWORKGROUPNAME:Int	= 2
Const NETSETUPDOMAINNAME:Int	= 3

Global NetGetJoinInformation (lpServer:Byte Ptr, lpNameBuffer:Byte Ptr, BufferType:Byte Ptr)
Global NetApiBufferFree (Buffer:Byte Ptr)

Local netapi32:Int = LoadLibraryA ("netapi32.dll")
If Not netapi32 Then RuntimeError "NetAPI32 not available"

NetGetJoinInformation	= GetProcAddress (netapi32, "NetGetJoinInformation")
NetApiBufferFree	= GetProcAddress (netapi32, "NetApiBufferFree")

' -----------------------------------------------------------------------------
' Function to return domain/workgroup name...
' -----------------------------------------------------------------------------

Function GetNetworkType:Long ()

	Local domainbuffer:Short Ptr	= Null
	Local result:Long Ptr		= Null
	
	If NetGetJoinInformation (Null, Varptr domainbuffer, Varptr result) = 0
		NetApiBufferFree domainbuffer
	EndIf
	
	Return Long (result)
	
End Function

Function GetNetworkName:String ()

	Local domain:String

	Local domainbuffer:Short Ptr	= Null
	Local result:Long Ptr		= Null
	
	If NetGetJoinInformation (Null, Varptr domainbuffer, Varptr result) = 0
		domain = String.FromWString (domainbuffer)
		NetApiBufferFree domainbuffer
	EndIf
	
	Return domain
	
End Function

' -----------------------------------------------------------------------------
' D E M O . . .
' -----------------------------------------------------------------------------

Print ""

Select GetNetworkType ()

	Case NETSETUPUNKNOWNSTATUS
		Print "Unknown connection status"

	Case NETSETUPUNJOINED
		Print "Not connected to a workgroup or domain"

	Case NETSETUPWORKGROUPNAME
		Print "Connected to a workgroup"

	Case NETSETUPDOMAINNAME
		Print "Connected to a domain"

End Select

Print GetNetworkName ()
