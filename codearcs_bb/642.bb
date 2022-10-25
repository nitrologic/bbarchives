; ID: 642
; Author: Jim Brown
; Date: 2003-04-05 11:44:18
; Title: Drive Volume Information
; Description: Returns lots of handy drive info

; Volume Information

; !!   place these in 'Kernel32.decls'   !!
; *****************************************
; GetDriveType%(drivename$):"GetDriveTypeA"
; GetVolumeInformation%(Path$,VolNameBuff*,VolLen%,Serial*,MaxComponentLen*,fsFlags*,fsNameBuff*,fsNameLen%):"GetVolumeInformationA"
; GetLogicalDriveStrings%(bufflen%,buffer*):"GetLogicalDriveStringsA"
; *****************************************



; volume details are stored here
Type volumeinfo
	Field driveletter$     ; Drives letter                "A:\"  "C:\"   "F:\"
	Field drivename$       ; Name of device               "My Computer"
	Field drivetype$       ; What type                    "Floppy" "CD-Rom"
	Field serial%          ; Serial number                1234567890
	Field maxcomponentlen% ; long/short name support      8.3  255
	Field flags%           ; associated flags             012345
	Field filesystem$      ; file system used             FAT32 NTFS CDFS
End Type
Global vol.volumeinfo

GetVolumeInfo ; fill 'volumeinfo' type with available volumes information

Print "Vol     Name                Type          Serial       Flags    FileSystem"
Print "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
For vol=Each volumeinfo
	r$=vol\driveletter$+"     "+Left$(vol\drivename$+String$(" ",19),19)
	r$=r$+" "+Left$(vol\drivetype$+String$(" ",12),12)
	r$=r$+" "+Right$(String$(" ",10)+Str$(vol\serial),11)+"   "
	r$=r$+Right$("      "+Str$(vol\flags),6)+"   "+vol\filesystem
	Print r$
Next

Print
a$=Input$("Done ... RETURN to end")
End



Function GetVolumeInfo()
	; first, get a list of available volumes  .. A:\B:\C:\F:\ ...
	vlist=CreateBank(256)
	GetLogicalDriveStrings 255,vlist
	drivelist$=PeekString$(vlist,256)
	FreeBank vlist
	; run through list of voulumes
	For x=0 To Len(drivelist$)/3-1
		vol=New volumeinfo
		vol\driveletter$=Mid$(drivelist$,x*3+1,3)
		vol\drivename$="(not available)"
		Select GetDriveType(vol\driveletter$)
			Case 2 : vol\drivetype$= "Removable"
			Case 3 : vol\drivetype$= "Drive Fixed"
			Case 4 : vol\drivetype$= "Remote"
			Case 5 : vol\drivetype$= "Cd-Rom"
			Case 6 : vol\drivetype$= "Ram disk"
			Default : vol\drivetype$= "Unrecognized"
		End Select
		vn=CreateBank(256) : sn=CreateBank(4)
		mcl=CreateBank(4) : flags=CreateBank(4) : fs=CreateBank(256)
		GetVolumeInformation vol\driveletter$,vn,255,sn,mcl,flags,fs,255
		vol\drivename$=PeekString$(vn,256)
		If vol\drivename$="" Then vol\drivename$="(not available)"
		vol\serial=PeekInt(sn,0)
		vol\maxcomponentlen=PeekInt(mcl,0)
		vol\flags=PeekInt(flags,0)
		vol\filesystem=PeekString$(fs,256)
		; free the banks
		FreeBank sn : FreeBank mcl : FreeBank flags
		FreeBank vn : FreeBank fs
	Next
End Function

; build and return a string of characters from a bank
Function PeekString$(bank,numbytes)
	Local a$=""
	For pos=0 To numbytes-1
		byte=PeekByte(bank,pos)
		If byte<>0 Then a$=a$+Chr$(byte)
	Next
	Return a$
End Function
