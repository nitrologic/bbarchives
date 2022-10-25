; ID: 2769
; Author: virtualjesus
; Date: 2010-09-16 17:39:44
; Title: CONVERTIR A HH:mm:ss (Time hour minute seg)
; Description: CONVERTIR A HH:mm:ss (Time hour minute seg)

Function UnidadTiempo$(s)
	Local d=0,h=0,m=0
	If s>0
		If s>86399 Then d=Floor(s/86400):s=s-(d*86400)
		If s>59 Then m=Floor(s/60):s=s-(m*60)
		If m>59 Then h=Floor(m/60):m=m-(h*60)
		If d>0
			Return d+"d : "+h+"h : "+m+"m : "+s+"s"
		Else
			If h>0
				Return h+"h : "+m+"m : "+s+"s"
			Else
				If m>0 Then Return m+"m : "+s+"s" Else Return s+"s"
			EndIf
		EndIf
	EndIf
End Function
