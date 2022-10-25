; ID: 2329
; Author: dmaz
; Date: 2008-10-06 02:03:15
; Title: String Format module
; Description: kind of like perl format or printf

SuperStrict

Import BRL.StandardIO


Rem
bbdoc: SFormat
End Rem
Module dmaz.SFormat

ModuleInfo "Version: 1.0"
ModuleInfo "Author: David Maziarka"
ModuleInfo "License: Public Domain"

ModuleInfo "History: 1.00 Release"


Const CLIPERROR:Int = -1
Global sformatPrecisionChar:String = "."

Rem
bbdoc: Print a string by combining @str and @values according to specified formats in @str
about: See #SFormat
End Rem
Function PrintSF( str:String, values:String[], clip:Int=CLIPERROR )
	Print SFormat(str,values,clip)
End Function


Function PrintSFD( str:String, values:String[], clip:Int=CLIPERROR )
	Print str + " [" + ", ".join(values) + "]"
	Print SFormat(str,values,clip)
	Print
End Function

Rem
bbdoc: Parse string for specified formats and combine it with one or more values.
returns: Returns a string produced according to the supplied string.
about:
#SFormat parses @str for the following directives.  It then interprets those directives to format a value which
is then inserted into the return string.  @values is an array of strings that will be processed
one by one with format.  @clip determines the output of a specific format if the value is
too large to fit within that format.
<br>
@str is composed of normal text, copied directly to the result and zero or more formats each of which
coincide with a value from @values.  Null values are ok and will still result in the specified format.
<br>
A format consists of "@" followed by any of >,<,|,.,0.  For example: "@>>>>>" will result in a right justified 
text field 6 (@ is included) characters wide.  "@>>>0.00" will result in a right justified field with 2 decimals
of precision
	<ul>
	<li><b>@</b> : By itself, will print out the next value unchanged, otherwise it marks the start of 
				a format consisting of some of the following directives.</li>
	<li><b>></b> : Right justify.</li>
	<li><b><</b> : Left justify.</li>
	<li><b>|</b> : Center.</li>
	<li><b>.</b> : Indicates how and to what precision a floating point number should be format.  Set the global
				@sformatPrecisionChar to use a different character to indicate precision in the output.</li>
	<li><b>0</b> : Used to left or right pad numbers.</li>
	</ul>
<br>
@clip can be one of the 3 options...
	<ul>
	<li><b>True</b> : Any value too big for the format will be clipped.  The format type will determine which
	side of the value will be cut off.</li>
	<li><b>False</b> : Don't do any clipping.  Although precision will still be clipped.</li>
	<li><b>CLIPERROR</b> : Fill the format with #'s instead of the value, this is the default</li>
	</ul>
End Rem
Function SFormat:String( str:String, values:String[], clip:Int=CLIPERROR )
	Local i:Int = 0
	While i < str.length
		Local b0:String = Chr(str[i])
		If str[i] = "@"[0]
			If i = str.length-1
				ReplaceF(str,ShiftValue(values),i,i,clip)
			Else
				For Local i2:Int = i+1 Until str.length
					Local c:Int = str[i2]
					Local b:String = Chr(c)
					If Not(c="0"[0] Or c="<"[0] Or c=">"[0] Or c="|"[0] Or c="."[0])
						i :+ ReplaceF(str,ShiftValue(values),i,i2-1,clip)
						Exit
					Else If i2+1 = str.length
						ReplaceF(str,ShiftValue(values),i,i2,clip)
					EndIf
				Next
			EndIf
		EndIf
		i :+ 1
	Wend
	Return str


	Function ReplaceF:Int( str:String Var, value:String, startx:Int, endx:Int, clip:Int )
		Local format:String = str[startx..endx+1]
		
		If format.length = 1
			' no format, insert the whole value.
			str = str[..startx] + value + str[endx+1..]
		Else
			' process format determined by the first < | > . 0 after the @
			Select str[startx+1]
				Case	">"[0], "0"[0], "."[0]
					Local fdotx:Int = format.FindLast(".")
					If fdotx >= 0
						Local vdotx:Int = value.FindLast(".")
						If vdotx < 0 Then vdotx = value.length
						Local ends:String = value[vdotx+1..]
						ends = ends[..Len(format)-fdotx-1]
						value = value[..vdotx] + sformatPrecisionChar + ends
					EndIf

					If value.length < format.length
						value = value[Len(value)-format.length..]
					ElseIf Clip = True And value.length > format.length
						If fdotx >= 0
							value = value[..Len(format)]
						Else
							value = value[Len(value)-Len(format)..]
						EndIf
					EndIf					

				Case	"|"[0]
					While value.length < format.length
						If value.length & 1 Then value = value+" " Else value = " "+value
					Wend
					If clip = True And value.length > format.length
						While value.length > format.length
							If value.length & 1 Then value = value[1..] Else value = value[..Len(value)-1]
						Wend			
					EndIf
				
				Default ' <
					If value.length < format.length
						value = value[..format.length]
					ElseIf clip = True And value.length > format.length
						value = value[..format.length]
					EndIf
					
			End Select
			
			If clip = CLIPERROR And value.length > format.length
				value = ""[..format.length].Replace(" ","#")
			Else
				For Local i:Int = 0 Until format.length
					If format[i] = "0"[0] And value[i] = " "[0]
						value = value[..i] + "0" + value[i+1..]
					EndIf
				Next

			EndIf
			
			str = str[..startx] + value + str[endx+1..]
		EndIf
		
		Return value.length - 1
	End Function

	
	Function ShiftValue:String( values:String[] Var )
		Local first:String = ""
		If values.length
			first = values[0]
			values = values[1..]
		EndIf
		Return first
	End Function

End Function
