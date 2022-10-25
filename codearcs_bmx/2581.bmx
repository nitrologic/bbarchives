; ID: 2581
; Author: Shortwind
; Date: 2009-09-10 14:53:18
; Title: Word Wrap - Version #1
; Description: Simple Word Wrap Routine

SuperStrict

Local a:String="This string is very long and we (x+y=34*sqr(z)/cos(r)*pi+(x*y)/87) will test various length texts."+..
	"  To test these functions for proper goodness is the stuff of dreams.  And "+..
	"the dreams are (x+y=34*sqr(z)/cos(r)*pi+(x*y)/87) here and not there.  But what if the dreams and here"+..
	" and not there?  Well, if the first and second part are not in the third part then the third part is "+..
	"not the circle of the square.  But sometimes things are not as they seem.  Even though the seem is there,"+..
	"it may not be there when we look into the seemingly endlessness of space and time...  Oh, but there is"+..
	"no time like the present even though there is no present in the time of the timeness that is time."

Local boxwidth:Int=20

DoMyWrap(a, boxwidth)
End

'------------------Start of Function ----------------------------
Function DoMyWrap(a:String, width:Int)
	Local s:Int, e:Int
	
	s=1
	e=width
	
	While s<Len(a)
		If Len(a)<e Then
			Print a
			Exit
		ElseIf s+e>=Len(a) Then
			Print Mid(a,s)
			Exit
		End If
		
		While Mid(a,s+e,1)<>" " And e>0
			e=e-1
		Wend
		
		If e=0 Then
			Print Mid(a,s,width)
			s=s+width
			e=width
		Else
			Print Mid(a,s,e)
			s=s+e+1
			e=width
		End If
	Wend

End Function
