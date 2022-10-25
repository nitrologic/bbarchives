; ID: 1545
; Author: Sonari Eclipsi Onimari
; Date: 2005-11-28 16:38:44
; Title: Conversion Log Date
; Description: Counts the number of days since 1-1-00

Function cld()
	year=Right$(CurrentDate(),4)
	ny=year-2000;this means that it will work until 3000 A.D. (The world will collapse before then according to the signs..)
	;2005 now = 5
	ny=ny-1
	;compensate for the following year
	;5 now = 4
		;2004=leapyear
		
			kip#=Float(ny/4)
			If kip# > ny And kip# < kip#+1
				;not leap year
				ly=False
				kip#=Left(kip#,1)
				;now, add # of years
					dcld=365*ny
						dcld=dcld+kip#
						;now, how many days this year
							month$=Mid(CurrentDate(),4,3)
								Select month$
									Case "Jan"	
										dcld=dcld+day
									Case "Feb"
										dcld=dcld+(31+day)
									Case "Mar"
										dcld=dcld+(31+28+day)
									Case "Apr"
										dcld=dcld+(31+28+31+day)
									Case "May"
										dcld=dcld+(31+28+31+30+day)
									Case "Jun"
										dcld=dcld+(31+28+31+30+31+day)
									Case "Jul"
										dcld=dcld+(31+28+31+30+31+30+day)
									Case "Aug"
										dcld=dcld+(31+28+31+30+31+30+31+day)
									Case "Sep"
										dcld=dcld+(31+28+31+30+31+30+31+31+day)
									Case "Oct"
										dcld=dcld+(31+28+31+30+31+30+31+31+30+day)
									Case "Nov"
										dcld=dcld+(31+28+31+30+31+30+31+31+30+31+day)
									Case "Dec"
										dcld=dcld+(31+28+31+30+31+30+31+31+30+31+30+day)
								End Select
								
						
			Else
				;yup, its leap year
				ly=True 
				kip#=Left(kip#,1)
				;now, add # of years
					dcld=365*ny
						dcld=dcld+kip#
						;now, how many days this year so far
						month$=Mid(CurrentDate(),4,3)
								Select month$
									Case "Jan"	
										dcld=dcld+day
									Case "Feb"
										dcld=dcld+(31+day)
									Case "Mar"
										dcld=dcld+(31+29+day)
									Case "Apr"
										dcld=dcld+(31+29+31+day)
									Case "May"
										dcld=dcld+(31+29+31+30+day)
									Case "Jun"
										dcld=dcld+(31+29+31+30+31+day)
									Case "Jul"
										dcld=dcld+(31+29+31+30+31+30+day)
									Case "Aug"
										dcld=dcld+(31+29+31+30+31+30+31+day)
									Case "Sep"
										dcld=dcld+(31+29+31+30+31+30+31+31+day)
									Case "Oct"
										dcld=dcld+(31+29+31+30+31+30+31+31+30+day)
									Case "Nov"
										dcld=dcld+(31+29+31+30+31+30+31+31+30+31+day)
									Case "Dec"
										dcld=dcld+(31+29+31+30+31+30+31+31+30+31+30+day)
								End Select
				EndIf 
			Return dcld
End Function
