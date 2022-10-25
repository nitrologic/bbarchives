; ID: 2559
; Author: JoshK
; Date: 2009-08-13 12:02:23
; Title: Pack file class
; Description: Easy to use file packer

SuperStrict

Framework brl.bankstream
Import brl.filesystem
Import brl.standardio
'Import "bank-utilities.bmx" 'http://blitzmax.com/codearcs/codearcs_bmx/2558.bmx

Local pak:TPackFile


'Simple example
pak=New TPackFile
pak.AddFile("packfile.bmx","testfile.txt")
pak.Save("data.pak")

pak=TPackFile.Load("data.pak")
Print pak.unpack()

Rem
'Advanced example (requires bank-utilities.bmx)
Const ENCRYPTIONKEY:String="BlitzMax is the best programming language in the world!"

pak=New TPackFile

'Pack the source code of this program
pak.AddFile("packfile.bmx")

Local bank:TBank=New TBank
Local bankstream:TBankStream=CreateBankStream(bank)
pak.save(bankstream)
bankstream.close()
bank=CompressBank(bank)
EncryptBank(bank,ENCRYPTIONKEY)
SaveBank(bank,"data.pak")

bank=LoadBank("data.pak")
DecryptBank(bank,ENCRYPTIONKEY)
bank=DecompressBank(bank)
bankstream=CreateBankStream(bank)
pak=TPackFile.Load(bankstream)
Print pak.unpack("unpack\test\stuff")
EndRem

SuperStrict

Import brl.bankstream
Import brl.filesystem

Type TPackFile
	
	Field filecount:Int
	Field filename:String[]
	Field filebank:TBank[]
	
	Method AddFile:Int(src:String,dst:String="")
		Local bank:TBank

		If Not dst dst=src
		dst=dst.Replace("\","/")
		If Chr(dst[0])+Chr(dst[1])="./"
			dst=dst[2..]
		EndIf
		
		bank=LoadBank(src)
		If Not bank
			DebugLog "Failed to load file ~q"+src+"~q."
			Return False
		EndIf
		filecount:+1
		filename=filename[..filecount]
		filename[filecount-1]=dst
		filebank=filebank[..filecount]
		filebank[filecount-1]=bank
		Return True
	EndMethod
	
	Method AddDir:Int(path:String="")
		Local n:Int
		Local files:String[]
		
		'Correct path string
		If path="" path="."
		path=path.Replace("\","/")
		If path
			If Chr(path[path.length-1])<>"/" path:+"/"
		EndIf
		
		files=LoadDir(path)
		If Not files
			DebugLog "Failed to load directory ~q"+path+"~q."
			Return False
		EndIf
		
		For n=0 To files.length-1
			
			Select FileType(path+files[n])
			Case 1
				DebugLog "Adding file ~q"+path+files[n]+"~q..."
				If Not AddFile(path+files[n])
					Return False
				EndIf
			Case 2
				DebugLog "Adding directory ~q"+path+files[n]+"~q..."
				If Not AddDir(path+files[n]) Return False
			EndSelect
		Next
		Return True
	EndMethod
	
	Function Load:TPackFile(url:Object)
		Local n:Int
		Local packfile:TPackFile
		Local size:Int
		Local stream:TStream
		
		stream=ReadStream(url)
		If Not stream Return Null
		packfile=New TPackFile
		packfile.filecount=stream.ReadInt()
		packfile.filename=packfile.filename[..packfile.filecount]
		packfile.filebank=packfile.filebank[..packfile.filecount]
		For n=0 To packfile.filecount-1
			packfile.filename[n]=stream.ReadLine()
			size=stream.ReadInt()
			packfile.filebank[n]=CreateBank(size)
			stream.readbytes(packfile.filebank[n].buf(),size)
		Next
		stream.close()
		Return packfile
	EndFunction
	
	Method Unpack:Int(path:String="",ShowProgress(progress:Float)=Null)
		Local n:Int
		Local stream:TStream
		Local size:Int
		Local totalsize:Int
		Local bytesunpacked:Int
		Local dir:String
		
		'Correct path string
		path=path.Replace("\","/")
		If path
			If Chr(path[path.length-1])<>"/" path:+"/"
		EndIf

		'Make sure unpack folder exists
		If path
			If FileType(path)<>2
				If Not CreateDir(path,1)
					DebugLog "Failed to create directory ~q"+path+"~q."
					Return False
				EndIf
			EndIf
		EndIf
		
		totalsize=GetDataSize()
		For n=0 To filecount-1
			dir=ExtractDir(path+filename[n])
			If FileType(dir)<>2
				If Not CreateDir(dir,1)
					DebugLog "Failed to create directory ~q"+dir+"~q."
					Return False
				EndIf
			EndIf
			stream=WriteFile(path+filename[n])
			If Not stream
				DebugLog "Failed to write file ~q"+path+filename[n]+"~q."
				Return False
			EndIf
			size=filebank[n].size()
			stream.writebytes(filebank[n].buf(),size)
			stream.close()
			bytesunpacked:+size
			If ShowProgress ShowProgress(Float(bytesunpacked)/Float(totalsize))
		Next
		
		Return True
	EndMethod
	
	Method GetDataSize:Int()
		Local size:Int,n:Int
		
		For n=0 To filecount-1
			size:+filebank[n].size()
		Next
		Return size
	EndMethod
	
	Method Save:Int(url:Object)
		Local n:Int
		Local size:Int
		Local ownstream:Int
		Local stream:TStream
		
		stream=TStream(url)
		If Not stream
			stream=WriteStream(url)
			ownstream=1
		EndIf
		If Not stream Return 0
		stream.WriteInt(filecount)
		For n=0 To filecount-1
			stream.WriteLine(filename[n])
			size=filebank[n].size()
			stream.WriteInt(size)
			stream.writebytes(filebank[n].buf(),size)
		Next
		If ownstream
			stream.close()
		EndIf
		Return 1
	EndMethod
	
EndType
