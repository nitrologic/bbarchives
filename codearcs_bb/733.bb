; ID: 733
; Author: Klaas
; Date: 2003-07-03 15:12:30
; Title: Query an DNS Server
; Description: you wanna know an ip from a domainname ...

;To Do's
;
;buffer overrun while pointer is leaving the bank
;
;this code is far fom completed but its works real well
;still have problems with PTR
;if you have questions please write to rayzor-s-edge@gmx.net

Global DNS_queryid	;an unique id for dns querys (will be incred by each request)
Global DNS_stream	;the main DNS Stream (UDP port 53)
Global pointer	;the main pointer for parsing the streams

Dim splitresult$(100)

Type query
	Field id
	
	Field nserver
	Field qr
	Field opcode
	Field aa
	Field tc
	Field rd
	Field ra
	Field z
	Field rcode

	Field stream

	Field qname$
	Field qtype
	Field qclass
	Field qsize
End Type

;create a window with a text area to show the results
hwnd=CreateWindow("text",0,0,500,600)
Global tarea=CreateTextArea(0,0,ClientWidth(hwnd),ClientHeight(hwnd),hwnd)
font = LoadFont("courier",14)
SetTextAreaFont(tarea,font)
SetGadgetLayout(tarea,1,1,1,1)

DNS_init()	;init the stream
DNS_Query(12345,parseip("212.79.208.3"),"www.google.de","A")	;ask your question

timer = CreateTimer(50)
Repeat
	WaitTimer(timer)
	Select WaitEvent()
	Case $803
		End
	End Select
	bank = DNS_waitQuery()
	If bank Then DNS_readQuery(bank)
Forever

Function DNS_init()
	;init first to get the udp stream
	DNS_Stream = CreateUDPStream(53)
End Function

Function DNS_waitQuery()
	;this function waits for an answer with the given id
	;or the timeout
	;this function parses all incoming querys but if the
	;query is not the coresponding one it reactivates itself with
	;a lowered timeout value
	If Not RecvUDPMsg(DNS_stream) Then Return False
	avail = ReadAvail(DNS_stream)
	If Not avail Return False
	Print "receiving a query "+avail+" bytes long"
	;read the incomming stream
	bank = CreateBank(avail)
	ReadBytes(bank,DNS_stream,0,avail)

	Return bank
End Function

Function DNS_Query(id,nserver,url$,typ$,class=1,rec=1)
	;this function builds the query structure and
	;feeds this structure with paramters
	q.query = New query
	
	queryid = id
	q\id = queryid
	q\qname = url
	q\qclass = class
	q\nserver = nserver
	
	If rec Then q\rd = 1
	
	Select Upper(typ)
	Case "A"
		q\qtype = 1
	Case "PTR"
		q\qtype = 12
	Case "MX"
		q\qtype = 15
	End Select
	
	;here the query structure is converted to an sendable stream
	DNS_createquerystream(q)
	;now send it and flush the structure
	If Not DNS_sendquery(q)
		Delete q
		Return False
	Else
		Delete q
		Return True
	EndIf
End Function

Function DNS_createquerystream(q.query)
	;this function converts the query structure to an real query streambank
	q\stream = CreateBank(512)
	
	;will not be reversed to little endian for fast lookup
	PokeShort(q\stream,0,q\id)

	b = q\rd Shl 8
	
	lePokeShort(q\stream,2,b)

	lePokeShort(q\stream,4,1)
	lePokeShort(q\stream,6,0)
	lePokeShort(q\stream,8,0)
	lePokeShort(q\stream,10,0)
	
	off = 12
	
	Length = Len(q\qname)
	
	nr = split(".",q\qname$)
	For i=0 To nr-1
		n$ = splitresult(i)
		l = Len(n)
		PokeByte(q\stream,off,l)
		off = off + 1
		For i2=1 To l
			c$ = Mid(n,i2,1)
			PokeByte(q\stream,off,Asc(c$))
			off=off+1
		Next
	Next
	PokeByte(q\stream,off,0)
	off=off+1
	lePokeShort(q\stream,off,q\qType)
	off=off+2
	lePokeShort(q\stream,off,q\qclass)
	off=off+2

	q\qsize = off
End Function

Function DNS_sendquery(q.query)
	;this function sends the query streambank to the name server specified in tzhe query structure
	If Not DNS_stream Then Return False
	WriteBytes(q\stream,DNS_Stream,0,q\qsize)
	
	SendUDPMsg(DNS_Stream,q\nserver,53)
	Print "Query send to "+DottedIP(q\nserver)

	Return True
End Function

Function DNS_ReadQuery(bank)
	;this is a lot of parsing
	
	If Not bank Then RuntimeError("invalid bank")
	Print "-- HEADER --"
	pointer = 12
	id = PeekShort(bank,0)
	Print "ID: "+id
	flag = lePeekShort(bank,2)
	Print "Flag: "+Right(Bin(flag),16)

	qr = (flag Shr 15) And 1
	opcode = (flag Shr 11) And 7
	aa = (flag Shr 10) And 1
	tc = (flag Shr 9) And 1
	rd = (flag Shr 8) And 1
	ra = (flag Shr 7) And 1
	z = (flag Shr 4) And 7
	rcode = (flag Shr 0) And 11

	Print "HEADER IN DETAILS"
	Print ""
	Write "Msg Type: "
	If qr
		Print "Answer"
	Else
		Print "Question"
	EndIf

	Write "OpCode: "
	Select opcode
	Case 0
		Print "standart"
	Case 1
		Print "inverse query"
	Case 2
		Print "a server status request"
	Case 3
		Print "reserved for future use"
	End Select

	Write "Authoritive: "
	If aa
		Print "Yes"
	Else
		Print "No"
	EndIf
	
	Write "Truncate: "
	If tc
		Print "Yes"
	Else
		Print "No"
	EndIf
	
	Write "Recursive desired: "
	If rd
		Print "Yes"
	Else
		Print "No"
	EndIf
	
	Write "Recursive available: "
	If ra
		Print "Yes"
	Else
		Print "No"
	EndIf

	Print "ZCode: "+z
		
	Write "RCode: "
	Select opcode
	Case 0
		Print "No error condition"
	Case 1
		Print "Format Error"
	Case 2
		Print "Server failure"
	Case 3
		Print "Name Error"
	Case 4
		Print "Not Implemented"
	Case 5
		Print "Refused"
	Case 6
		Print "Unkown"
	End Select

	qd = lePeekShort(bank,4)
	an = lePeekShort(bank,6)
	ns = lePeekShort(bank,8)
	ar = lePeekShort(bank,10)
	
	Print ""
	Print "DATA COUNT"
	Print ""
	Print "Questions : "+qd
	Print "Answers   : "+an
	Print "Athority  : "+ns
	Print "Additional: "+ar
	
	Print ""
	Print "-- DATA --"
	For i=1 To qd
		Print""
		Print "READING QUESTION "+i+"("+pointer+")"
		DNS_readqd(bank)
	Next
	For i=1 To an
		Print""
		Print "READING ANSWER "+i+"("+pointer+")"
		DNS_readresource(bank)
	Next
	For i=1 To ns
		Print""
		Print "READING AUTHORITY "+i+"("+pointer+")"
		DNS_readresource(bank)
	Next
	For i=1 To ar
		Print""
		Print "READING ADDITIONAL "+i+"("+pointer+")"
		DNS_readresource(bank)
	Next
End Function

Function DNS_readresource(bank)
	;this function parses the resource messages
	;resource messages are all appending message of a query
	;except the question messages
	
	name$ = DNS_decodeSubString(bank,pointer)
	
	Local b[10]
	For i=1 To 10
		b[i]=PeekByte(bank,pointer)
		pointer = pointer + 1
	Next
	
	ctype = (b[1] Shl 8) + b[2]
	class = (b[3] Shl 8) + b[4]
	ttl = (b[5] Shl 24) + (b[6] Shl 16) + (b[7] Shl 8) + b[8]
	rdlength = (b[9] Shl 8) + b[10]
	
	Print "NAME     :"+name$
	Print "TYPE     :"+ctype
	Print "CLASS    :"+class
	Print "TTL      :"+ttl+" sec"
	Print "RD Length:"+rdlength

	repointer = pointer

	Print ""
	Select ctype
	Case 1
		Print "Data is a host address"
		ip$ = decodeIPadress(bank,pointer)
		Print "RDATA    :"+ip
	Case 2
	Print "Data is an authoritative name server "
		txt$ = DNS_decodeSubString(bank,pointer)
		Print "RDATA    :"+txt
	Case 6
		Print "Data is a zone of authority"
		txt = DNS_decodeSubString(bank,pointer)
		Print "MNAME    :"+txt
		txt = DNS_decodeSubString(bank,pointer)
		Print "RNAME    :"+txt
		r1 = lePeekShort(bank,pointer)
		pointer=pointer+2
		r2 = lePeekShort(bank,pointer)
		pointer=pointer+2
		Print "SERIAL   :"+(r1 Shl 16)+r2

		r1 = lePeekShort(bank,pointer)
		pointer=pointer+2
		r2 = lePeekShort(bank,pointer)
		pointer=pointer+2
		Print "REFRESH  :"+(r1 Shl 16)+r2
		 
		r1 = lePeekShort(bank,pointer)
		pointer=pointer+2
		r2 = lePeekShort(bank,pointer)
		pointer=pointer+2
		Print "RETRY    :"+(r1 Shl 16)+r2

		r1 = lePeekShort(bank,pointer)
		pointer=pointer+2
		r2 = lePeekShort(bank,pointer)
		pointer=pointer+2
		Print "EXPIRE   :"+(r1 Shl 16)+r2

		r1 = lePeekShort(bank,pointer)
		pointer=pointer+2
		r2 = lePeekShort(bank,pointer)
		pointer=pointer+2
		Print "MINIMUM  :"+(r1 Shl 16)+r2
	Case 12
		Print "Data is a domain name pointer"
		txt = DNS_decodeSubString(bank,pointer)
		Print "RDATA    :"+txt
	Case 15
		Print "Data is a mail exchange"
		pre = lePeekShort(bank,pointer)
		pointer = pointer + 2
		Print "RDATA    :"+pre
		domain$ = DNS_decodeSubString(bank,pointer)
		Print "RDATA    :"+domain
	Default
		Print "Data is a unknown type"
	End Select
	pointer = repointer + rdlength
End Function

Function DNS_decodeSubString$(bank,off)
	;this is the main subroutine for parsing
	;it supports the query compression sheme
	;for DNS querys
	
	l = True
	While l
		l = PeekByte(bank,off)
		off = off + 1
		
		If l=0
			pointer = off
			Return Left(txt$,Len(txt)-1)
		ElseIf (l And 192)=192
			b1 = l
			b2 = PeekByte(bank,off)
			off = off + 1
			
			
			off2 = (b1 Shl 8) + b2
			off2 = (off2 And 16383)
;			Print "(jumping to "+off2+")"
			txt = txt + DNS_decodeSubString(bank,off2)
			pointer = off
			Return txt$
		Else
			For i = 1 To l
				b = PeekByte(bank,off)
				off = off + 1
				txt$ = txt$ + Chr(b)
			Next
			txt$ = txt$ + "."
		EndIf
	Wend
End Function

Function DNS_Readqd(bank)
	;this function parses question messages
	
	txt$ = DNS_decodeSubString(bank,pointer)
	
	Local b[4]
	For i=1 To 4
		b[i]=PeekByte(bank,pointer)
		pointer = pointer + 1
	Next

	Print "QNAME :"+txt
	Print "QTYPE :"+(b[1] Shl 8) + b[2]
	Print "QCLASS:"+(b[3] Shl 8) + b[4]
End Function

Function decodeIPadress$(bank,off)
	For i = 1 To 4
		txt$ = txt + dot$ + PeekByte(bank,off)
		dot$ = "."
		off = off + 1
	Next
	Return txt
End Function

;------------------------------------------------------------- HELP FUNCTIONS

Function split(seperator$,txt$)
	;splits an string with the given seperator and returns the nr of pieces
	;the pieces can be found in the "splitresult" Array
	pos=Instr(txt$,seperator$,1)	
	While (pos)
		splitresult(count)=Left(txt$,pos-Len(seperator))
		
		txt$=Right(txt$,Len(txt$)-pos-Len(seperator)+1)
		pos=Instr(txt$,seperator$,1)
		count=count+1
	Wend
	splitresult(count)=txt$
	
	count=count+1
	Return count
End Function

Function parseip(txt$)
	;converts an dotted ip like "192.0.0.1" to an integervalue
	nr = split(".",txt)
	If nr = 4
		b1 = Int(splitresult(0)) Shl 24
		b2 = Int(splitresult(1)) Shl 16
		b3 = Int(splitresult(2)) Shl 8
		b4 = Int(splitresult(3)) Shl 0
	EndIf
	
	Return b1+b2+b3+b4
End Function

Function lePokeShort(bank,offset,short)
	;because networkbytes are stored differently to x86 PC's (in network its little-endian)
	;this function swaps byte 1 and byte 2 in an short and inserts it in the given bank
	b1 = short And $ff
	b2 = (short Shr 8) And $ff
	PokeByte(bank,offset,b2)
	PokeByte(bank,offset+1,b1)
End Function

Function lePeekShort(bank,offset)
	;because networkbytes are stored differently to x86 PC's (in network its little-endian)
	;this function read a short from a bank and swaps byte 1 and byte 2 then returns the new value

	short = PeekShort(bank,offset)
	b2 = (short And $ff) Shl 8
	b1 = (short And $ff00) Shr 8
	Return (b2 + b1)
End Function

Function Print(txt$)
	AddTextAreaText(tarea,txt+Chr(10))
End Function

Function Write(txt$)
	AddTextAreaText(tarea,txt)
End Function
