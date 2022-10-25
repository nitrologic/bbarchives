; ID: 3037
; Author: Shagwana
; Date: 2013-03-08 01:36:49
; Title: XOR string encrypt
; Description: Simple encryption function for strings using xor and a phrase

SuperStrict

Local sMsg:String="stephen greener is a cool cat"
Local sPhrase:string="www.sublimegames.com"

print "-----"
print "(phrase) :"+sPhrase
print "original :"+sMsg
local c:string=sXorEncode(sMsg,sPhrase)
print "encode   :"+c
local d:string=sXorEncode(c,sPhrase)
print "decode   :"+d
print "-----"
End


Function sXorEncode:string(sMessage:String,sPhrase:String)
	If sPhrase.length=0 then return sMessage 	'No encoding as no phrase
	if sMessage.length=0 then return ""   	'No message so no encoding

	Local sBuffer:String=""
	Local iPhrasePos:Int=0

	For local i:int=0 to sMessage.length-1

		Local iPhrase:Int=sPhrase[iPhrasePos]
		Local iCurrent:Int=sMessage[i]

		sBuffer:+Chr(Byte(iPhrase~iCurrent)) 	'Simple XOR encrypt

		iPhrasePos:+1  	'Next char in the phrase, wrap around
		if iPhrasePos>=sPhrase.length then iPhrasePos=0

	Next

	Return sBuffer	
EndFunction
