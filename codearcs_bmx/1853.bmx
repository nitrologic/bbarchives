; ID: 1853
; Author: Beaker
; Date: 2006-10-25 18:00:00
; Title: Media file 'Nibbler' [bmax]
; Description: Nibble-loads media files (images etc) so you can display a progress bar (or other feedback)

SuperStrict

Type Nibble
	Field FileStream	:TStream
	Field Bank		:TBank
	Field BankStream	:TBankStream
	Field chunkSize		:Int
	Field ready		:Int
	Field estimatedFileSize	:Int
	
	Function Create:Nibble(file$,chunkSize:Int=20,estimatedFileSize:Int=4000)
		Local nib:Nibble = New Nibble
		nib.FileStream = OpenStream(file, True,False)
		If nib.FileStream = Null Then Return Null
		nib.chunksize = chunkSize
		nib.estimatedFileSize = estimatedFileSize
		Return nib
	End Function
	
	Method New()
		Bank = CreateBank()
		BankStream = CreateBankStream(Bank)
	End Method	
	
	Method Delete()
		CloseStream(BankStream)
		CloseStream(FileStream)
		Bank=Null
	End Method
	
	Method result:TBankStream()
		SeekStream(BankStream, 0)
		Return BankStream
	End Method
		
	Method Nibble:Float()
		If Eof(FileStream) Return 1.0

		Local nibbleSize:Int = StreamSize(FileStream)*(Float(chunkSize)/100.0)
		If StreamPos(FileStream) = -1 Then nibbleSize = chunkSize
		For Local f:Int = 0 To nibbleSize
			If Eof(FileStream)
				ready = True
				Exit
			EndIf
			WriteByte BankStream,ReadByte(FileStream)
		Next
		If StreamPos(FileStream) = -1 Then
			Return Float(BankSize(Bank) Mod estimatedFileSize) / Float(estimatedFileSize)
		EndIf
		Return Float(StreamPos(FileStream))/Float(StreamSize(FileStream))
	End Method

	Method Progress:Float()
		Return Float(StreamPos(FileStream))/Float(StreamSize(FileStream))
	End Method

	Method Status:Int()
		Return ready
	End Method
End Type


Graphics 800,600,0

'Local mynibble:Nibble = Nibble.Create("zombie.jpg",5)
Local mynibble:Nibble = Nibble.Create("http::www.blitzbasic.com/img/brllogo-thin.png",50,4000)


Local progress:Float = 0
While mynibble.ready = False
	Cls
	DrawRect 50,50,progress*200.0,20
	DrawText Int(progress*100.0),5,5
	progress = mynibble.Nibble()	
	Flip
Wend

Global myImage:TImage = LoadImage(myNibble.result())

myNibble = Null

While Not KeyDown(KEY_ESCAPE)
	DrawImage myImage, MouseX(), MouseY()
	Flip
	Cls
Wend
End
