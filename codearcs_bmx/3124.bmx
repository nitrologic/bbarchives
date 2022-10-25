; ID: 3124
; Author: munch
; Date: 2014-05-10 00:06:44
; Title: Get online doc examples
; Description: Gets code examples from the online manual and adds them to the docs

' Get online doc examples.bmx

SuperStrict

TGetHttpCode.FindAll()

End

' Gets code examples from the online manual and adds them to the docs
Type TGetHttpCode

	Global filei%,files$[1000],mdir$,aboutlimit%=500
	
	Function FindAll()
	
		mdir=FindDocs()
		
		If FileType("Modules")=0 ' no modules folder
			Print "Creating Modules folder"
			HttpFindFiles(mdir) ' get online examples
		Else
			Print "Modules folder already exists"
		EndIf
		
		CodeFindFiles("Modules") ' copy files
		
		CodeSaveFuncs()
		
		HtmlFindFiles(mdir) ' add to html files
		
	End Function
	
	Function FindDocs$()
	
		Local wdir$=CurrentDir()+"/" ' find the docs
		Local ldir$=wdir.Tolower() ' lowercase
		Local bms%=ldir.Find("blitzmax",0) ' bmax start
		If bms=-1 RuntimeError "Failed to find blitzmax directory e1"
		
		Local bme%=ldir.Find("/",bms) ' bmax end
		Return wdir[..bme]+"/docs/html/Modules" ' modules dir
		
	End Function
	
	' part 1: get code from http files
	
	Function HttpFindFiles(dir$)
	
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e2"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=2 Then HttpFindFiles2(cfile) ' directory
		Forever
	
		CloseDir dh
		
	End Function

	Function HttpFindFiles2(dir$)
	
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e3"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=2 Then HttpFindFiles3(cfile) ' directory
		Forever
		
		CloseDir dh
		
	End Function

	Function HttpFindFiles3(dir$)
	
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e4"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=1 ' file, html
				If ExtractExt(cfile)="html" Then HttpOpenHtml(cfile)
			EndIf
		Forever
		
		CloseDir dh
		
	End Function

	Function HttpOpenHtml(path$)
	
		Local file:TStream=OpenFile(path)
		If Not file RuntimeError "could not open html file e5"

		While Not Eof(file)
		
			Local html$=ReadString(file,FileSize(path))
			Local html2$,link$,code$
			Local fr%=html.Find("Function reference",0)
			Local fts%=fr,fbs%,fen%,fte%,fbe%
			
			If fr>0 ' file has functions
			Print "Reading: "+path[mdir.length+1..path.Find("index.html",mdir.length)-1]
			
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
							link="http::www.blitzmax.com/bmdocs/command.php?name="+func+"&ref=goto"
							code=HttpOpen(link,func)
							If code.length>0 Then HttpSaveCode(path,func,code)
						EndIf
						
					EndIf
					
				Wend
				
			EndIf
			
		Wend
		
		CloseStream file
		
	End Function

	Function HttpOpen$(url$,func$)
	
		Local in:TStream=ReadStream(url)
		If Not in RuntimeError "Failed to open a ReadStream to http file e6"
		
		Local http$
		While Not Eof(in)
			http:+ReadLine(in)+"~n"
		Wend
		
		Local user$,code$,about$
		Local tmp%,cs%,ps%,ai%,opf%,pe%,ui%,tmp2%,gt%,cu%,cc%,nc%
		Local paf%[1000],ua%[1000],pa%[1000]
		
		Print func
		ps=0
		While ps>-1
			ps=http.Find("<pre class=code>",ps+1)
			If ps>0 Then  pa[ai]=ps ; ai:+1 ' pre array
		Wend
		
		ps=0
		While ps>-1
			ps=http.Find("<textarea class=codebox ",ps+1)
			If ps>0 Then pa[ai]=ps ; ai:+1
		Wend
		
		ps=0
		While ps>-1
			ps=http.Find("<td class="+Chr(34)+"posthead"+Chr(34)+">",ps+1)
			If ps>0 Then  ua[ui]=ps ; ui:+1 ' username array
		Wend
		
		For tmp=0 To ai-1
			If pa[tmp]<cs Then opf=pa[tmp] ; Exit ' official pre found
		Next
		
		If opf ' official pre
			pe=http.Find("</pre>",opf)
			code=http[opf+16..pe].Trim() ' get the code
			
			CloseStream in
			Return code
		EndIf
		
		ua[ui]=-1 ' last user
		For tmp=0 To ai-1
			For tmp2=0 To ui-1
				If tmp2=ui-1 Then gt=pa[tmp]+1 Else gt=ua[tmp2+1] ' greater
				If pa[tmp]>ua[tmp2] And pa[tmp]<gt ' pre between these values
					cu=ua[tmp2] ' user
					If paf[tmp2]=0 Then cc=pa[tmp] ; paf[tmp2]=cc ' last user with first code example
				EndIf
			Next
		Next
		
		If cu=0 And cc=0 ' no code examples
			ps=0
			While ps>-1
				ps=http.Find("<td class="+Chr(34)+"posttext"+Chr(34)+">",ps+1) ' last comment
				If ps>0 Then nc=ps
			Wend
			
			tmp=http.Find("</td></tr>",nc+21)
			If tmp>0 And nc>0 ' found comment
				code="Rem~n"+http[nc+21..tmp]+"~nEndRem~n"
				code:+"' "+func.Tolower()+".bmx ()"
				If code.length<20 Or code.length>aboutlimit Then code="" ' ignore
			EndIf
			
			CloseStream in
			Return code
		EndIf
		
		ps=http.Find("<td >",cu) ' get username
		tmp=http[ps..ps+9].Find("<a href=",0) ' check for link
		If tmp>-1 Then ps=http.Find("</a>",tmp)
		tmp2=http.Find("</td>",ps)
		user=http[ps+5..tmp2]
		
		tmp=http.Find("<td class="+Chr(34)+"posttext"+Chr(34)+">",cu) ' get about text
		tmp2=http.Find(">",tmp)+1
		about="Rem~n"+http[tmp2..cc]+"~nEndRem~n"
		If about.length<20 Or about.length>aboutlimit Then about="" ' ignore
		
		tmp=http[cc..cc+9].Find("<pre ",0) ' get example
		If tmp=-1 Then pe=http.Find("</textarea>",cc) Else pe=http.Find("</pre>",cc)
		tmp2=http.Find(">",cc)+1
		code=http[cc..pe].Trim()
		tmp=code.Find("~n",0)
		If tmp2>-1 Then code=code[tmp..].Trim()
		tmp2=code.Find(".bmx",0)
		code=about+"' "+func.Tolower()+".bmx ("+user+")~n~n"+code
		
		CloseStream in
		Return code
		
	End Function

	Function HttpSaveCode(path$,func$,code$)
	
		Local success%,fs%,fs2%,file:TStream
		Local lpath$,ldir2$,ldir3$,ldir$
		
		ldir="Modules"
		If FileType(ldir)=0
			success=CreateDir(ldir)
			If Not success RuntimeError "error creating directory e7"
		EndIf
		
		fs=path.Find("/",mdir.length+1) ' directory level 2
		ldir2=ldir+path[mdir.length..fs]
		If FileType(ldir2)=0
			success=CreateDir(ldir2)
			If Not success RuntimeError "error creating directory e8"
		EndIf
		
		fs2=path.Find("/",fs+1) ' directory level 3
		ldir3=ldir+path[mdir.length..fs]+path[fs..fs2]
		If FileType(ldir3)=0
			success=CreateDir(ldir3)
			If Not success RuntimeError "error creating directory e9"
		EndIf
		
		lpath=ldir+path[mdir.length..fs]+path[fs..fs2]+"/"+func.Tolower()+".bmx" ' local file path
		file=WriteFile(lpath)
		If Not file RuntimeError "failed to open file e10" 
		WriteLine file,code
		CloseStream file
		
	End Function

	' part 2: copy local files to docs/html
	
	Function CodeFindFiles(dir$)
	
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e11"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=2 Then CodeFindFiles2(cfile) ' directory
		Forever
	
		CloseDir dh
		
	End Function

	Function CodeFindFiles2(dir$)
	
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e12"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=2 Then CodeFindFiles3(cfile) ' directory
		Forever
		
		CloseDir dh
		
	End Function

	Function CodeFindFiles3(dir$)
	
		Local ddir$,temp$,code$,file:TStream
		Local dh%,fp%,cf%,re%
		
		dh=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e13"
		
		Repeat
			temp=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			fp=cfile.Find("/",0)
			ddir=mdir+cfile[fp..]
			
			If FileSize(ddir)=-1 ' file, bmx
				files[filei]=cfile ' store local file path
				filei:+1
				
				code=HtmlReadCode(cfile)
				cf=code.Find(".bmx (",0) ' if comment file
				If cf>0 ' strip rem
					re=code.Find("EndRem",0)
					If re>0 And re<cf
						code=code[re+8..]
					EndIf
				EndIf
				
				cf=code.Find(".bmx ()",0) ' blank user=comment but no code
				If cf=-1
					file=WriteFile(ddir) ' copy from local to docs/html
					If Not file RuntimeError "failed to open file e14" 
					WriteLine file,code
					CloseStream file
				EndIf
			EndIf
			
		Forever
		
		CloseDir dh
		
	End Function

	Function CodeSaveFuncs()
	
		Local fi%,file:TStream
		
		If FileSize("Modules/log.txt")=-1 ' file doesn't exist
			file=WriteFile("Modules/log.txt")
			If Not file RuntimeError "failed to open file e15"
			
			For fi=0 To filei-1 ' file index
				WriteLine file,files[fi]
			Next
			CloseStream file
		EndIf
		
	End Function
	
	' part 3: add code examples to docs/html
	
	Function HtmlFindFiles(dir$)
	
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e16"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=2 Then HtmlFindFiles2(cfile) ' directory
		Forever
	
		CloseDir dh
		
	End Function

	Function HtmlFindFiles2(dir$)
	
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e17"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=2 Then HtmlFindFiles3(cfile) ' directory
		Forever
		
		CloseDir dh
		
	End Function

	Function HtmlFindFiles3(dir$)
	
		Local dh%=ReadDir(dir)
		If Not dh RuntimeError "failed to read current directory e18"
	
		Repeat
			Local temp$=NextFile(dh)
			If temp="" Then Exit ' done
			If temp="." Or temp=".." Continue ' skip
			Local cfile$=dir+"/"+temp ' current file
			If FileType(cfile)=1 ' file, html
				If ExtractExt(cfile)="html" Then HtmlOpenHtml(cfile)
			EndIf
		Forever
		
		CloseDir dh
		
	End Function

	Function HtmlOpenHtml(path$)
	
		Local html$,html2$,code$,row$,about$,info$
		Local file:TStream=OpenFile(path)
		If Not file RuntimeError "could not open html file e19"
		
		While Not Eof(file)
		
			html=ReadString(file,FileSize(path))
			Local fr%=html.Find("Function reference",0)
			Local fts%=fr
			Local fbs%,fen%,fte%,fls%,fep%,fer%,fet%,fri%,flr%,fhe%,fni%,fne%,fnt%,lnt%,fnf%,tmp%,tmp2%
			
			If fr>0 ' file has functions
			html2=html
			
				While fts>-1 ' while functions in file
					fts=html.Find("colspan=2>Function ",fts+1) ' table start
					
					If fts>0
					
						fts:+19 ' to function name
						fbs=html.Find("(",fts) ' bracket start
						fte=html.Find("</td>",fts) ' table end
						fen=html[fts..fbs].Find(":",0) ' type (end name)
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
							
							For fni=0 To filei-1 ' new index
							
								fls=files[fni].FindLast("/",0)+1 ' last slash
								fep=files[fni].Find(".bmx",fls) ' extension pos
								
								If files[fni][fls..fep]=func.Tolower()
								
									fne=html.Find("<table class=doc ",fbs) ' next entry
									If fne=-1 Then fne=html.Find("</body>",fbs)
									fet=html.Find("</table>",fbs) ' end table
									
									fnt=fbs
									For fri=0 To 3 ' last nested table
										lnt=fnt
										fnt=html.Find("<table>",fnt+1) ' nested table
										If fnt<fne And fnt>0 Then fnt=lnt ; Exit ' is nested
									Next
									
									fnf=0
									If fnt<fne And fnt>0 ' found nested table
										fet=html.Find("</table>",fnt)
										fnf=html.Find("</tr>",fet)
										fet=html.Find("</table>",fet)
									EndIf
									
									fer=fbs ' end row
									For fri=0 To 19 ' row index
										flr=fer ' last row
										fer=html.Find("</tr>",fer+1)
										
										If fer>fet Or fer=-1
											flr:+5
											If fnf Then flr=fnf+5
											Exit
										EndIf
									Next
									
									fhe=html.Find("Example</a>",fbs) ' has example	
									If fhe=-1 Or fhe>fet ' no example found
										code=HtmlReadCode(files[fni])
										Print "Adding Examples: "+files[fni]
										
										tmp=code.Find("EndRem",0) ' about text
										info="" ; about=""
										If tmp>-1
											about=code[4..tmp]
											tmp2=about.Find("<br>",about.length-6)
											If tmp2>-1 Then about=about[..tmp2] ' rm last br
											
											tmp2=0
											While tmp2>-1 ' rm blank link targets
												tmp2=about.Find(" target="+Chr(34)+"_blank"+Chr(34),tmp2+1)
												If tmp2>0 Then about=about[..tmp2]+about[tmp2+15..]
											Wend
											
											code=code[tmp+8..]
											If about.length>10
												info="<tr><td class=docleft width=1%>Comment</td>"
												info:+"<td class=docright>"+about+"</td></tr>"
											EndIf
										EndIf
										
										row=info
										tmp=code.Find(".bmx ()",0) ' blank user=comment but no code
										If tmp=-1
											row:+"~n<tr><td class=docleft width=1%><a href="
											row:+Chr(34)+func.Tolower()+".bmx"+Chr(34)
											row:+">Example</a></td><td class=docright><pre>"
											row:+code+"</pre>~n</td></tr>"
										EndIf
										html2=html[..flr]+row+html[flr..]
									EndIf
									
								EndIf
								
							Next
							
						EndIf
						
					EndIf
					html=html2 ' update html
				Wend
				
			EndIf
		Wend
		
		SeekStream file,0 ' overwrite file
		WriteString file,html
		CloseStream file
		
	End Function

	Function HtmlReadCode$(path$)
	
		Local file:TStream=ReadFile(path)
		If Not file RuntimeError "could not open bmx file e20"
		
		Local code$=ReadString(file,FileSize(path))
		
		CloseStream file
		Return code
		
	End Function

End Type
