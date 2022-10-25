; ID: 2893
; Author: ThePict
; Date: 2011-10-13 16:06:46
; Title: Search Dictionary
; Description: Check if word is in dictionary or not

;You'll need to download the Dictionary data file. http://www.lexicod.co.uk/Dictionary/dictionary.dat
;and store it locally  - Dictionary\dictionary.dat
;It only contains words from 2 up to 13 letters long (as that's all I needed it to do), but,
;there are over 144000 words in there!! 

;Search the dictionary file in millisecs

;How to use it. 
;result=SearchDictionary(YourWord$)
;returns True if it's in the dictionary, False if it isn't.
;
;Why to use it.... need more word progs.

;Graphics 800,400,16,2
Print "Entering nothing will exit program"
Repeat
Print
w$=Input$("Enter a word: ")
time=MilliSecs()
result=SearchDictionary(Lower(w$)); Note: no Proper Nouns in my dictionary
If result=False Then r$=" is not" Else r$=" IS"
elapsed=MilliSecs()-time:Print Lower(w$)+r$+" in the dictionary. In "+elapsed+" ms."


Until w$=""



Function SearchDictionary(word$)
word$=" "+word$+" "
fsize=FileSize("Dictionary\dictionary.dat")
wbank=CreateBank(32)
mf=OpenFile("Dictionary\dictionary.dat")
offset=fsize/2
offadj=offset/2
For n=1 To 22
SeekFile(mf,offset)
a32$=""
	For m=0 To 31
	a32$=a32$+Chr$(ReadByte(mf))
	Next
mword$=FindMiddleWord$(a32$)
If RSet(word$,15)=RSet(mword$,15) Then Exit 
offadj=Abs(offadj)
If RSet(word$,15)<RSet(mword$,15) Then offadj=(offadj*(-1))
offset=offset+offadj
offadj=offadj/2
Next
CloseFile mf
result=False
If n<22 Then result=True
Return result
End Function


Function FindMiddleWord$(a32$)
;Function to find the middle word in a string 32 chars long
sp2=Instr(a32$," ",16)
For n=(sp2-1) To 1 Step -1
If Mid$(a32$,n,1)=" " Then Exit
Next
mword$=Mid$(a32$,n,sp2-n+1)
Return mword$
End Function


FlushKeys
WaitKey()
End
