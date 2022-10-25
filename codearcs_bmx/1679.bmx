; ID: 1679
; Author: AntMan - Banned in the line of duty.
; Date: 2006-04-18 08:36:08
; Title: EasyHtml
; Description: Helper funcs to easily generate html pages with images/headers etc

Type THml

	Global as:TStream
	
	Function SetActiveStream( in:TStream )
		as = in
	End Function
	
	Function Header(title:String)
		
		WriteLine as,"<html>"
		WriteLine as,"<head>"
		WriteLine as,"<title>"+title+"</title>"
		WriteLine as,"</head>"	
	
	End Function
	
	Function BodyHead()
		WriteLine as,"<body>"
	End Function
	
	Function HeadHead(size:Int,align:String="")
		If align = ""
			WriteLine as,"<h"+String(Size)+">"
		Else
			WriteLine as,"<h"+String(size)+" align='"+align+"'>"
		EndIf
		
	End Function
	
	Function Break()
		WriteLine as,"<br>"
	End Function
	
	Function StrongHead()
		WriteLine as,"<strong>"
	End Function
	
	Function StrongFoot()
		WriteLine as,"</strong>"
	End Function
	
	Function BigHead()
		WriteLine as,"<big>"
	End Function
	
	Function BigFoot()
		WriteLine as,"</big>"
	End Function
	
	Function EmpHead()	
		WriteLine as,"<em>"
	End Function
	
	Function EmpFoot()
		WriteLine as,"</em>"
	End Function
	
	Function ItalicHead()
		WriteLine as,"<i>"
	End Function
	
	Function ItalicFoot()
		WriteLine as,"</i>"
	End Function
	
	Function SmallHead()
		WriteLine as,"<small>"
	End Function
	
	Function SmallFoot()
		WriteLine as,"</small>"
	End Function
	
	Function SubHead()
		WriteLine as,"<sub>"
	End Function
	
	Function SubFoot()
		WriteLine as,"</sub>"
	End Function
	
	Function SupHead()
		WriteLine as,"<sup>"
	End Function
	
	Function SupFoot()
		WriteLine as,"</sup>"
	End Function
	
	Function PreHead()
		WriteLine as,"<pre>"
	End Function
	
	Function PreFoot()
		WriteLine as,"</pre>"
	End Function
	
	Function varHead()
		WriteLine as,"<var>"
	End Function
	
	Function VarFoot()
		WriteLine as,"</var>"
	End Function
	
	Function acronHead(ac:String)
		WriteLine as,"<acronym title='"+ac+"'>"
	End Function
	
	Function AcronFoot()
		WriteLine as,"</acronym>"
	End Function
	
	Function QuoteHead()
		WriteLine as,"<blockquote>"
	End Function
	
	Function QuoteFoot()
		WriteLine as,"</blockquote>"
	End Function
	
	Function LinkHeadTar(html:String,tar:String)
		WriteLine as,"<a href='"+html+"' target='"+tar+"'>"
	End Function
	
	Function LinkHead(html:String)
		WriteLine as,"<a href='"+html+"'>"
	End Function
	
	Function SectionHead(name:String)
		WriteLine as,"<a name='"+name+"'>"	
	End Function
	
	Function FrameSetRow( size:Int[],src:String[],nam:String[])
		
		Local cols = size.length
		Local cs:String
		For Local j=0 Until cols
			If j>0 cs:+","
			cs:+String( size[j] )+"%"
		Next
		
		WriteLine as,"<frameset rows='"+cs+"'>"
		
		For Local j=0 Until cols
			Local txt:String
			txt = "<frame src='"+src[j]+"'"
			If nam[j]<>""
				txt:+" name='"+nam[j]+"'"
			EndIf
			txt:+">"
			WriteLine as,txt
		Next
		WriteLine as,"</frameset>"
			
	End Function


	Function ListHead()
		WriteLine as,"<ul>"
	End Function
	
	Function ListFoot()
		WriteLine as,"</ul>"
	End Function
	
	Function ItemHead()
		WriteLine as,"<li>"
	End Function
	
	Function ItemFoot()
		WriteLine as,"</li>"
	End Function
	
	
	
	Function FrameSetCol( size:Int[],src:String[],nam:String[])
		
		Local cols = size.length
		Local cs:String
		For Local j=0 Until cols
			If j>0 cs:+","
			cs:+String( size[j] )+"%"
		Next
		
		WriteLine as,"<frameset cols='"+cs+"'>"
		
		For Local j=0 Until cols
			Local txt:String
			txt = "<frame src='"+src[j]+"'"
			If nam[j]<>""
				txt:+" name='"+nam[j]+"'"
			EndIf
			txt:+">"
			WriteLine as,txt
		Next
		WriteLine as,"</frameset>"
		
	
	End Function
	
	Function LinkFoot()
		WriteLine as,"</a>"
	End Function
	
	Function Image(border=0,src:String,width,height,align="")
		If align = ""
			WriteLine as,"img border='"+String(border)+"' src='"+src+"' width='"+String(width)+"' height='"+String(height)+"'>"
		Else
			WriteLine as,"img border='"+String(border)+"' src='"+src+"' align='"+align+"' width='"+String(width)+"' height='"+String(height)+"'>"
		EndIf
	End Function
	
		
	
	Function HRule()
		WriteLine as,"<hr>"
	End Function
	
	Function HeadFoot(size:Int)
		WriteLine as,"</h"+String(size)+">"
	End Function
	
	Function ParaHead()
		WriteLine as,"<p>"
	End Function 
	
	Function ParaFoot()
		WriteLine as,"</p>"
	End Function
	
	
	Function Text( txt:String )
		WriteLine as,txt
	End Function
	
	Function BoldText(txt:String)
		WriteLine as,"<b>"+txt+"</b>"
	End Function
	
	Function BodyFoot()
		WriteLine as,"</body>"
	End Function
	
	Function Footer()
		WriteLine as,"</html>"
	End Function
	
	
	

End Type
