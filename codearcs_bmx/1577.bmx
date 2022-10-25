; ID: 1577
; Author: CS_TBL
; Date: 2005-12-26 19:41:52
; Title: Parser
; Description: simple, minimalistic, but handy parser

'Strict
Type Parser

	Rem
	
A very basic command/value parser, but often sufficient.

A single string holds the script, and that script can be as ugly as uglyness can be, it could
come from textareas, files etc. The idea is that everything can be parsed, without bugging the
user with (runtime-)errors, caused by a bad script. If you create a texture-generator, the
result would be that the texture would look wrong, but never (runtime-)errors, or annoying popups
you need to click away.

The syntax for the script is always:
<command><value> <command><value> <command><value> .. ..

<Command>
- A single char, anyone of these: QWERTYUIOPASDFGHJKLZXCVBNM`!@#$%^&*()_+[]:;'\|,/<>?"
- Exluded are the tilde ~ and both braces { }
- Any other character (including spaces) is wiped out.
- az will become uppercase

<Value>
- is any double.
- ugly written values will be souped-up
- if no value is given, value=0


an example:

a0b100c-3d9.9

These are 4 commands (a b c and d), and 4 values (1 100 -3 and 9.9).
(values intentially rounded for readablity)

The same script could also've been written as:

ab1 00c -----3... d9.9----

Groundrules for Double souping-up:
- the most right dot will be the decimal dot
- the amount of minus-signs defines whether it's positive or negative

So, 10000.00 could be written as 10.000.00, and 3-3 -..-..- 3-3 means -33.33

{} are used for commenting .. but very basic again. When the parser sees a { it'll ignore
the next characters until there's a } again.. no support for nesting. Advantage is that with no
further use of commenting in a large script, a single { comments the rest without the need of a
closing }.

usage:

	MyParser = CreateParser()

	MyParser.Parse(MyScript$)
	AmountOfCommands:Int = MyParser.Commands()
	MyCommand$ = MyParser.GetCommand(commandnumber)
	MyValue:Double = MyParser.GetValue(commandnumber)





made by:

	CS_TBL, 2nd xmas-day 2005 :P

		
	EndRem

	Field bank:TBank
	
	Method Prepare$(s$)
		Local n$
		Local c$
		Local t:Short
		Local comment:Byte
		
		' allow ASCIIs 33..125
		For t=0 To Len(s$)-1
			c$=Mid$(s$,t+1,1)
			If Asc(c$)>32 And Asc(c$)<126 n$=n$+Upper$(c$)
		Next
		
		s$=n$
		n$=""
		t=0
		For t=0 To Len(s$)-1
			c$=Mid$(s$,1+t,1)
			If c$="{" comment=True
			If c$="}" comment=False
			
			If Not comment
				If c$<>"}" n$=n$+c$
			EndIf
		Next
		
		Return n$
	End Method
	
	Method Text2Double:Double(a$)
		If a$="" Return 0
		Local l:Short
		Local az$
		Local m:Short
		Local negative:Byte
		Local found:Byte
		Local foundpos:Short
		Local t:Short
	
		l=Len(a$)
	
		' do we have an odd amount of '-' ?
		az$=Replace$(a$,"-","")
		m=l-Len(az$)
	
		' yes? it's a negative number!
		If m Mod 2 negative=True
		
	
		' scan the value (without - ) For dots
		For t=Len(az$)-1 To 0 Step -1
			If Not found
				If Mid$(az$,t+1,1)="."
					found=True
					foundpos=(Len(az$)-1)-t
				EndIf
			EndIf
		Next
		' so we found the most-Right dot, If any..
	
	
		' get rid of all the dots Then
		az$=Replace$(az$,".","")
	
		l=Len(az$)
	
		If found ' place 1 dot back
			az$=Left$(az$,l-foundpos)+"."+Right$(az$,foundpos)
		EndIf
	
		If negative
			az$="-"+az$
		EndIf
	
		Return az$.todouble()
		
	End Method
	
	Method Parse(a$)
		Local t:Short
		Local valuefound:Byte
		Local number$
		Local ch$
		Local parselen:Short
		Local bs:Int
		Local char$
		
		a$=Prepare$(a$)
		If a$="" Return
		
		ResizeBank bank,0
		
		parselen=Len(a$)

		Repeat
			char$=Mid$(a$,t+1,1)
			
			If Instr("QWERTYUIOPASDFGHJKLZXCVBNM`!@#$%^&*()_+[]:;'\|,/<>?"+Chr$(34),char$)
				t:+1
				valuefound=0
				number$=""
				Repeat
					ch$=Mid$(a$,t+1,1)
					If Instr("1234567890-.",ch$)
						number$=number$+ch$
						t:+1
					Else
						valuefound=True
						t:-1
					EndIf
					If t>=parselen valuefound=True
				Until valuefound
				
				bs=BankSize(bank)
				ResizeBank bank,bs+9
				bs:+9

				
				PokeByte bank,bs-9,Asc(char$)
				PokeDouble bank,bs-8,Text2Double(number$)
				
			EndIf
			
			t:+1
			
		Until t>=parselen
		
		
	End Method
	
	Method Commands:Int()
		Return BankSize(bank)/9
	End Method
	
	Method GetCommand$(which:Short)
		Local t:Short
		If which<0 which=0
		t=Commands()
		If which=>t which=t-1
		Return Chr$(PeekByte(bank,which*9))
	End Method

	Method GetValue:Double(which:Short)
		Local t:Short
		If which<0 which=0
		t=Commands()
		If which=>t which=t-1
		Return PeekDouble(bank,which*9+1)
	End Method
	
End Type

Function CreateParser:Parser()
	Local a:Parser=New Parser
	a.bank=CreateBank(0)
	Return a
End Function


Rem ' example..

Local a:Parser=CreateParser()


a.Parse " a   34.1 1       b    9 4 c4d5.3   e-99.    3f     30.0 00.  76"

For Local t:Byte=0 Until a.Commands()
	DebugLog a.GetCommand(t)
	DebugLog "    "+a.GetValue(t)
Next

Notify "^_^"



' and again..

a.Parse "3{1373}}}Blitz{GUI} rules { b()()bs!"

For Local t:Byte=0 Until a.Commands()
	DebugLog a.GetCommand(t)
	DebugLog "    "+a.GetValue(t)
Next

Notify "^_^"

End

EndRem
