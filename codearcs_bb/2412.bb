; ID: 2412
; Author: RifRaf
; Date: 2009-02-20 01:47:31
; Title: Bank IO commands
; Description: Write files to a bank before hard disk

;;//// BANK IO COMMANDS BY Jeff Frazier / Rifraf


;;;-------------Example---------------;;....................
;                                                          .
;                                                          .
;SeedRnd(MilliSecs())  
;f%=Bank_Openfile()  
;For i=1 To 5                                               
;	Bank_writeint(f%,Rand(1,32))
;Next                       
;bank_writestring(f%,"Hello there")
;For i=1 To 5                     
;	Bank_writefloat(f%,Rnd(1,32))     
;Next                             
;bank_writestring(f%,"Hello again")
;For i=1 To 5                     
;	Bank_writebyte(f%,Rnd(1,32))      
;Next                             
;bank_seekfile(f%,bank_filepos(f)-4)
;bank_writestring(f%,"Final hello ")
;                                 
;;; write data to a file using normal file functions       .
;                                
;m$="testme.dat"
;                                
;bank_dump(f,m$)
;                                
;;; cleanup the bank             
;                                
;Bank_CLOSEFILE(f)                
;                                
;;;---------------------------------------------------------
;F=OpenFile("TESTME.DAT")
;For I=1 To 5
;	DebugLog ReadInt(f)
;	Next
;DebugLog ReadString$(f)
;For I=1 To 5
;	DebugLog ReadFloat(f)
;	Next
;DebugLog ReadString$(f)
;For I=1 To 1
;	DebugLog ReadByte(f)
;	Next
;DebugLog ReadString$(f)
;While Not Eof(f)
;DebugLog ReadByte(f)
;Wend
;CloseFile f
;WaitKey
;End

Type Bankoffset
 Field Name$
 Field offset
 Field bankid
End Type

Function BANK_OPENFILE(name$="Bank",sizeoverride=0)
 bo.bankoffset=New bankoffset
 If sizeoverride<>0 Then 
	 bo\bankid=CreateBank(size)
 Else
	 bo\bankid=CreateBank(1)
 EndIf
 bo\name$=name$
 Return Handle(bo) 
 
End Function 


Function BANK_OPENFILE_exists(originalbank,name$="banker")
 bo.bankoffset=New bankoffset
 bo\bankid=originalbank
 bo\name$=name$
 Return Handle(bo)
End Function 

Function BANK_CLOSEFILE(bid%)
Thisbank.bankoffset=Object.bankoffset(bid%)
FreeBank THISBANK\BANKID
Delete thisbank
End Function 

Function Bank_EOF(bid%)
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank\offset=>BankSize(bid%) Then Return True
Return False
End Function  

Function Bank_SeekFile(bid%,offset)
Thisbank.bankoffset=Object.bankoffset(bid%)
thisbank\offset=offset
End Function 

Function Bank_FILEPOS(bid%)
Thisbank.bankoffset=Object.bankoffset(bid%)
Return thisbank\offset
End Function 

Function Bank_FILELEN(bid%)
Return BankSize(bid%)
End Function 
;;;;;;;;;;;;;;;;;;;WRITE FUNCTIONS;;;;;;;;;;;;;;;;;;;;;;;;

Function Bank_Writeint(bid%,intdata%)
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_WriteInt"
If thisbank\offset+4>(BankSize(thisbank\bankid)) Then 
   neededsize=4-((BankSize(thisbank\bankid))-thisbank\offset)
   Bankresize(thisbank.bankoffset,neededsize)
EndIf
PokeInt(thisbank\bankid,thisbank\offset,intdata)
thisbank\offset=thisbank\offset+4
End Function 


Function Bank_WriteFloat(bid%,floatData#)
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_WriteFloat"
If thisbank\offset+4>(BankSize(thisbank\bankid)) Then 
   neededsize=4-((BankSize(thisbank\bankid))-thisbank\offset)
   Bankresize(thisbank.bankoffset,neededsize)
EndIf
PokeFloat(thisbank\bankid,thisbank\offset,floatData#)
thisbank\offset=thisbank\offset+4
End Function 

Function Bank_WriteBytes(bid%,bytebank)
Local s=BankSize (bytebank)
Local i
For i=1 To s
 bank_writebyte(bid%,PeekFloat(bytebank,i))
Next
End Function 


Function Bank_WriteByte(bid%,bytedata)
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_WriteByte"

If thisbank\offset+1>(BankSize(thisbank\bankid)) Then 
   neededsize=1-((BankSize(thisbank\bankid))-thisbank\offset)
   Bankresize(thisbank.bankoffset,neededsize)
EndIf

PokeByte(thisbank\bankid,thisbank\offset,byteData)
thisbank\offset=thisbank\offset+1
End Function 

Function Bank_WriteString(bid%,stringdata$)
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_WriteString"
Length=Len(Stringdata$)
Bank_Writeint(bid,length)
For I=1 To Length
	bank_Writebyte(bid,Asc(Mid$(stringdata$,I,1)))
Next
End Function

Function Bank_WriteLine(bid%,stringdata$)
stringdata$=stringdata$+Chr$(13)+Chr$(10)
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_WriteString"
Length=Len(Stringdata$)
;Bank_Writeint(thisbank\bankid,length)
For I=1 To Length
	bank_Writebyte(bid,Asc(Mid$(stringdata$,I,1)))
Next
End Function 



;;;;;;;;;;;;;;;;;;;READ FUNCTIONS;;;;;;;;;;;;;;;;;;;;;;;;

Function Bank_ReadInt(bid%)
Local THISV
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_ReadInt"
If thisbank\offset+4>(BankSize(thisbank\bankid)) Then RuntimeError "Bank io read, past eof: Bank_ReadInt"
THISV = PeekInt(thisbank\bankid,thisbank\offset)
thisbank\offset=thisbank\offset+4
Return THISV
End Function 

Function Bank_ReadFloat#(bid%)
Local THISV#
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_Readfloat"
If thisbank\offset+4>(BankSize(thisbank\bankid)) Then  RuntimeError "Bank io read, past eof: Bank_ReadFloat"
THISV#= PeekFloat(thisbank\bankid,thisbank\offset)
thisbank\offset=thisbank\offset+4
Return THISV#
End Function 

;ank
Function Bank_ReadByte(bid%)
Local THISV
Thisbank.bankoffset=Object.bankoffset(bid%)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_ReadByte"
If thisbank\offset+1>(BankSize(thisbank\bankid)) Then  RuntimeError "Bank io read, past eof: Bank_ReadByte"
THISV= PeekByte(thisbank\bankid,thisbank\offset)
thisbank\offset=thisbank\offset+1
Return THISV
End Function 

Function Bank_ReadString$(bid%)
Local StringV$=""
Local THISV
THISV=Bank_ReadInt(bid%)
For I=1 To thisv
	Stringv$=stringv$+Chr$(bank_readbyte(bid%))
Next
Return Stringv$
End Function 

Function Bank_ReadLine$(bid%)
Local StringV$=""
Local THISV
While Not Bank_EOF(bid%)
    Temp$=Chr$(bank_readbyte(bid%))
 	 If temp$=Chr$(13)  Then 
	     bank_readbyte(bid%) 
    	 Return stringv$
    EndIf 
	Stringv$=stringv$+temp$
Wend
Return Stringv$
End Function 



;;;;;;;;;;;;;;;;;;;MISC;;;;;;;;;;;;;;;;;;;;;;;;
Function Findbank.bankoffset(b_id)
For bo.bankoffset=Each bankoffset
 If bo\bankid=b_id Then Return bo.bankoffset
Next
Return Null
End Function 

Function Findbankname.bankoffset(name$)
For bo.bankoffset=Each bankoffset
 If bo\name$=name$ Then Return bo.bankoffset
Next
Return Null
End Function 

; BankSize, ResizeBank, CopyBank Example 
Function BankResize(bo.bankoffset,size)
	This=BankSize(bo\bankid)
	ResizeBank bo\bankid,(this+size)
End Function 

Function BANK_DUMP(b_id,fn$)
Thisbank.bankoffset=Object.bankoffset(b_id)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_Dump"
sz=BankSize(thisbank\bankid)
f=WriteFile(fn$)
WriteBytes (thisbank\bankid,f,0,sz)
CloseFile f
End Function 

Function BANK_DUMPbyID(b_id,fn$)
Thisbank.bankoffset=FINDBANK(b_id)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_Dump"
sz=BankSize(thisbank\bankid)
f=WriteFile(fn$)
WriteBytes (thisbank\bankid,f,0,sz)
CloseFile f
End Function 


Function BANK_DUMPbyName(name$,fn$)
Thisbank.bankoffset=Findbankname(name$)
If thisbank.bankoffset=Null Then RuntimeError "I/O bank does not exist : Bank_Dump"
sz=BankSize(thisbank\bankid)
f=WriteFile(fn$)
WriteBytes (thisbank\bankid,f,0,sz)
CloseFile f
End Function
