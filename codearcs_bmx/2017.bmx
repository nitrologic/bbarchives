; ID: 2017
; Author: Dabz
; Date: 2007-05-21 10:16:56
; Title: Question and Answer Type
; Description: A short type that handles multiple choice questions (inc Q&A File)...

'Question and Answer Type
'Written by Michael Denathorn 2007
'BlitzMax version 1.24

SuperStrict 

SeedRnd MilliSecs()

Type TQuestion
	Field mQuestion:String
	Field mAnswerBank:String[4]
	Field mAnswer:Byte
	
	Method SetQuestion()
		Local filein:TStream = ReadFile("Incbin::QBANK.txt")
		Local randloop:Int = Rand(0,1749),loop:Int ,loopAnswers:Int 
		Local question:String = "",char:String 
		
		'Get a random question and its data
		For loop = 0 To randloop
			question = ReadLine$(filein)
		Next
		
		'Extract the actual question
		For loop = 0 To Len(question)
			char = Mid$(question,loop,1)
			If char = "/"
				Exit
			Else
				mQuestion = mQuestion + char
			End If
		Next
		
		'Extract the four answers
		For loopAnswers = 0 To 3
			For loop = (loop+1) To Len(question)
				char = Mid$(question,loop,1)
			If char = "/"
				Exit
			Else
				mAnswerBank[loopAnswers] = mAnswerBank[loopAnswers] + char
			End If

			Next 
		Next
		
		'Extract the actual answer
		mAnswer = Int(Mid$(question,(loop+1),1))
		
		CloseFile filein
	End Method 
End Type 

Incbin "QBANK.txt"

Local question:TQuestion = New TQuestion 
Local loop:Int
question.SetQuestion()
Print question.mQuestion
For loop = 0 To 3
	Print (loop+1)+") "+question.mAnswerBank[loop]
Next
Print "Answer is number: "+question.mAnswer
End
