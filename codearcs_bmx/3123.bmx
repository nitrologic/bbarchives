; ID: 3123
; Author: munch
; Date: 2014-05-07 00:04:52
; Title: Online doc links
; Description: Adds links to docs/html for all functions in brl and pub modules, links are to online doc

' Online doc links.bmx
'
' Adds links to docs/html for all functions in brl and pub modules, links are to online doc

SuperStrict

Local p:AddDocLinks = New AddDocLinks

p.FindFiles(p.FindDocs())

End

Type AddDocLinks
	Field mdir$=FindDocs()
	
	Method FindDocs$()
		Local wdir$=CurrentDir()+"/" ' find the docs
		Local ldir$=wdir.Tolower() ' lowercase
		Local bms%=ldir.Find("blitzmax",0) ' bmax start
		If bms=-1 RuntimeError "Failed to find blitzmax directory"
		
		Local bme%=ldir.Find("/",bms) ' bmax end
		Return wdir[..bme]+"/docs/html/Modules" ' modules dir
	End Method
	
	Method FindFiles(dir$)
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=2 Then FindFiles2(cfile) ' directory
		Forever
	
		CloseDir dh 
	End Method

	Method FindFiles2(dir$)
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory l2"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=2 Then FindFiles3(cfile) ' directory
		Forever
		
		CloseDir dh
	End Method

	Method FindFiles3(dir$)
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory l3"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=1 ' file, html
				If ExtractExt(cfile)="html" Then OpenHtml(cfile)
			EndIf
		Forever
		
		CloseDir dh
	End Method

	Method OpenHtml(path$)
	
		Local html$,file:TStream=OpenFile(path)
		If Not file RuntimeError "could not open file"
	
		While Not Eof(file)
			html=ReadString(file,FileSize(path))
			Local html2$,link$,lpart$
			Local fr%=html.Find("Function reference",0)
			Local fts%=fr,fbs%,fen%,fte%,fbe%
			
			If fr>0 ' file has functions
			html2=html
			Print "Adding Links: "+path[mdir.length+1..path.Find("index.html",mdir.length)-1]
			
				While fts>-1 ' while functions in file
					fts=html.Find("colspan=2>Function ",fts+1)
					If fts>0
						fts:+19 ' to function name
						fbs=html.Find("(",fts) ' bracket start
						fte=html.Find("</td>",fts) ' table end
						fen=html[fts..fbs].Find(":",0) ' type (end of func name)
						If fen>0 Then fbs=fts+fen
						fen=html[fts..fbs].Find("[",0) ' array
						If fen>0 Then fbs=fts+fen
						fen=html[fts..fbs].Find("$",0) ' string
						If fen>0 Then fbs=fts+fen
						fen=html[fts..fbs].Find("#",0) ' float
						If fen>0 Then fbs=fts+fen
						fen=html[fts..fbs].Find("!",0) ' double
						If fen>0 Then fbs=fts+fen
						fen=html[fts..fbs].Find("_",0)
						
						If fen=-1 ' not a lua_func
							fen=html[fts..fbs].Find(" ",0) ' trim spaces
							If fen>0 Then fbs=fts+fen
							Local func$=html[fts..fbs] ' get func name
							fbe=html[fts..fte].Find("NoDebug",0) ' bracket end or nodebug
							If fbe=-1 Then fbe=html[fts..fte].FindLast(")",0) Else fbe:+6
							fbe:+fts+1
							link=html[fbe..fte] ' leave existing links alone
							lpart=" [<a href="+Chr(34)+"http://www.blitzmax.com/bmdocs/command.php?name="
							If fte-fbe=0
								link=lpart+func+"&ref=goto"+Chr(34)+">Online doc</a>]" ' if no link add one
							EndIf
							html2=html[..fbe]+link+html[fte..]
						EndIf
					EndIf
					html=html2 ' update html
				Wend
				
			EndIf
		Wend
		
		SeekStream file,0 ' overwrite file
		WriteString file,html
		CloseStream file
	
	End Method

End Type
