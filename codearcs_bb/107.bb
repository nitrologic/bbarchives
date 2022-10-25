; ID: 107
; Author: Unknown
; Date: 2001-10-23 17:13:16
; Title: Tid packer/unpacker
; Description: Store media/etc in one .Tid file.

;T.I.D.Y packer/unpacker Lib
;
;Coded by Ashcroft
;
;V0.1
;
;
;**********************CHANGES*********************
;
;After a couple hours of work the basics are workign fine.
;
;**************************************************
;
;Features > - Packs an unlimited amount of files into one .tid file. Can also unpack/search tids
;No compression yet.

;Method consts


;-------
Global L_Used$ = "" ;Last accessed Tid file. So you only have to enter the fid file to access once.
Type File
	Field File$,Size,Name$
End Type
	Global Tid.File ;List
	Global His.File ;History(Stores files created from within, so you can delete them with one function)
;--------------
Const Vn = 1 ;Version number.
;--------------
SeedRnd MilliSecs()*MilliSecs()

Function Tid_AddList(File$,Name$ = "") 
;Usage
;   Tid_Add(FileName$,Name$) 
;	Adds a file(Of any type) to the File list
;   You can name a tid for later retrival
;---------------------------------------------
	Tid.File = New File
	Tid\File$ = File$
	Tid\Size = FileSize(File$) 
	Tid\Name$ = Name$
End Function

Function Tid_Create(File$) 
;Usage
;	Tid_Create(Filename$,Method of file inclusion)
;	Builds a Tid file from file list.
;returns file size
;------------------------------------------
	Stream = WriteFile(File$+".Tid")
	If Stream = 0 Then Return False
	WriteByte Stream,Vn
	;Gen/write Tid Table
	For Tid.File = Each File
		WriteByte Stream,2
		WriteString Stream,Tid\Name$
		WriteInt Stream,Tid\Size
		WriteInt Stream,F_O
		F_O = F_O + Tid\Size	
	Next
	WriteByte Stream,1
	F_O = FilePos(Stream) + 1
	For Tid.File = Each File
		S2 = OpenFile(Tid\File$)
		If S2 = 0 Then End
		Tmp = CreateBank(Tid\Size)
		ReadBytes(Tmp,S2,0,Tid\Size)
		WriteBytes(Tmp,Stream,0,Tid\Size)
		CloseFile S2
		FreeBank Tmp
	Next	
	CloseFile Stream
	Return FileSize(File$)
End Function

Function Tid_FetchFile$(File$ = "",Name$)
;Usage 
;	Tid_FetchFile(FileName$,Name of data within to find)
;	Fetchs a file from the .Tid using the name you gave it with AddList
;	It places it within the Windows temp dir and then adds that file to the
;	History list. (For later clearing)
;Returns file location
;-----------------------------------------
	If File$ = "" Then File$ = L_Used$ Else L_Used$ = File$
	Stream = ReadFile(File$+".Tid")
	If Stream = 0 Then Return False
	If Not ReadByte(Stream) = Vn Then Return False
	While ReadByte(Stream) = 2
		If ReadString$(Stream) = Name$
			Size = ReadInt(Stream)
			F_O = ReadInt(Stream)
			Tmp$ = SystemProperty("tempdir") + Rand(5000)
			S2 = WriteFile(Tmp$)
			Tb = CreateBank(Size)
			
		Else
			A = ReadInt(Stream)+ReadInt(Stream) ;Skip data
		EndIf
	Wend
	SeekFile(Stream,F_O+FilePos(Stream))
	If Tmp$ = "" Then Return False
	ReadBytes(Tb,Stream,0,Size)
	WriteBytes(Tb,S2,0,Size)
	CloseFile S2
	CloseFile Stream
	FreeBank Tb
	His.File = New File
	His\Name$ = Name$
	His\File$ = Tmp$
	His\Size = Size
	Return Tmp$
End Function

Function Tid_CleanUp() ;Deletes all temporary files 'Tid' may have created, (Other you'll quickly waste away a man's hd)
	For His.File = Each File
		DeleteFile His\File$
		Delete His
	Next
End Function


Function Tid_FreeHist() ;Frees the history list.(I can't see a use for this to though)
	For His.File = Each File
		Delete His
	Next
End Function

Function Tid_FreeList() ;Frees the current Tid info held in memory
	For Tid.File = Each File
		Delete Tid
	Next
End Function
