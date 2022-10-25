; ID: 238
; Author: skn3[ac]
; Date: 2002-02-13 07:17:22
; Title: Save a type to file
; Description: Simple example of how to save a type to a text file

Graphics 640,480,32,2

Type example
Field x
Field y
End Type

;-=-=-=-[Saving to a file]-=-=-=-

;create 10 instances of type with handle obj
For i=1 To 10 Step 1
	obj.example = New example
		obj\x=Rand(1,640)
		obj\y=Rand(1,480)
Next

;draw them all to screen to show they have been created

SetBuffer BackBuffer()
For obj.example = Each example
	Color Rand(50,255),Rand(50,255),Rand(50,255)
	Rect obj\x,obj\y,20,20
Next
Flip
Print "PRESS ANYKEY TO SAVE THEM"
WaitKey()

;Save The type to a file

;open the file
file=WriteFile("type-example.txt")
For obj.example = Each example
	WriteLine ( file,"X" + obj\x + "Y" + obj\y + ":" ) 
	;I have used the ":" so that it is easier to determin the end of a line 
Next
;close the file
CloseFile file



;-=-=[Clear everything]=-=-
For obj.example = Each example
Delete obj.example
Next
Cls
Print "PRESS ANY KEY TO LOAD THEM NOW..."
WaitKey()



;-=-=-=-[Loading from a file]-=-=-=-

file=ReadFile("type-example.txt")
While Eof(file)=False
	;Grab a line from the text file
	grab$=ReadLine(file)
	;get the two bits of data
	grabx = Mid ( grab$,Instr(grab$,"X") + 1,Instr(grab$,"Y") - 1 )
	graby = Mid ( grab$,Instr(grab$,"Y") + 1,Instr(grab$,":") - 1 )
	
	;CREATE the new instance using these new grabbed bits of data
	obj.example = New example
	obj\x = grabx
	obj\y = graby
Wend

;draw them all to screen to show they have been LOADED

SetBuffer BackBuffer()
For obj.example = Each example
	Color Rand(50,255),Rand(50,255),Rand(50,255)
	Rect obj\x,obj\y,20,20
Next
Flip
;close file
closefile file

RuntimeError "There finished, Check the folder this was saved in and you should see a text file"
