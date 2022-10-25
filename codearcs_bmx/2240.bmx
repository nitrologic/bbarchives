; ID: 2240
; Author: Plash
; Date: 2008-04-08 15:27:19
; Title: Image Hueing
; Description: playing around with image masks and hues

Function conv_UnCompRGBA(px:Int, r:Byte Var, g:Byte Var, b:Byte Var, a:Byte Var) 
	a = px Shr 24
	r = px Shr 16
	g = px Shr 8
	b = px
	
End Function

Function conv_CompRGBA:Int(r:Int, g:Int, b:Int, a:Int = 255) 
	Return (a Shl 24 | r Shl 16 | g Shl 8 | b) 
	
End Function

Function gfx_HueCoordArrayArea(pm:TPixmap Var, flurl:String, single:Int = 0) 
   Local flovl:TStream = ReadFile(flurl) 
    If Not flovl Return
	If Not pm Then Return
	
	Local pm_format:Int = PixmapFormat(pm) 
	 If (pm_format <> PF_RGBA8888) Then pm = ConvertPixmap(pm, PF_RGBA8888) 
	 
	Local CRGBA:Int
	Local sea:Int, ser:Int, seg:Int, seb:Int
	 Local nr:Byte, ng:Byte, nb:Byte, na:Byte
	  Local cl_list:TList = CreateList() 
	  
	   Local lnovl:String
		While Not Eof(flovl) 
		  lnovl = ReadLine(flovl) 
			
			If Left(lnovl, 6) = "colid:"
			
				CRGBA = Int(Right(lnovl, Len(lnovl) - 6)) 
				  Local px_x:Int, px_y:Int, ic:Int
				   Local px:Int, lr:Byte, lg:Byte, lb:Byte, la:Byte
				    
				    conv_UnCompRGBA CRGBA, nr, ng, nb, na
	  				 DebugLog nr + "," + ng + "," + nb + "," + na
				  		
					 cl_list.AddLast(String(CRGBA)) 
					While Not Eof(flovl) 
					 lnovl = ReadLine(flovl) 
					  ic = Instr(lnovl, ",") 
						
						If ic = 0 And single = 0
					   		CRGBA = Int(Right(lnovl, Len(lnovl) - 6)) 
							
							If cl_list.Contains(String(CRGBA)) = 0
								conv_UnCompRGBA CRGBA, nr, ng, nb, na
								cl_list.AddLast(String(CRGBA)) 
								
							EndIf
							
						ElseIf ic > 0
							px_x = Int(Left(lnovl, ic - 1)) 
							px_y = Int(Right(lnovl, Len(lnovl) - ic)) 
							
							px = ReadPixel(pm, px_x, px_y) 
							conv_UnCompRGBA px, lr, lg, lb, la
							
							'DebugLog lr + "," + lg + "," + lb + "," + la
							
							sea = (na + la) 
							 If sea < 0 Then sea = 0
							 If sea > 255 Then sea = 255
							ser = (nr + lr) 
							 If ser < 0 Then ser = 0
							 If ser > 255 Then ser = 255
							seg = (ng + lg) 
							 If seg < 0 Then seg = 0
							 If seg > 255 Then seg = 255
							seb = (nb + lb) 
							 If seb < 0 Then seb = 0
							 If seb > 255 Then seb = 255
							 
							WritePixel pm, px_x, px_y, Int(sea Shl 24 | ser Shl 16 | seg Shl 8 | seb) 
							
						EndIf
						
					Wend
					
				EndIf
			
		Wend
	  CloseFile flovl
	 cl_list.Clear
	 
	If (pm_format <> PF_RGBA8888) Then pm = ConvertPixmap(pm, pm_format) 
	
End Function

Function dat_CreateHueCoordArray(url:String, ner:Int, neg:Int, neb:Int) 
  Local pm:TPixmap = LoadPixmap(url) 
   If Not pm Then Return
   
	 If (PixmapFormat(pm) <> PF_RGBA8888) Then pm = ConvertPixmap(pm, PF_RGBA8888) 
	
	Local flovl:TStream
	Local fnm:String = Left(url, Len(url) - 3) + "ovl" ; DebugLog fnm
	
		If FileType(fnm) = FILETYPE_FILE
			flovl = OpenStream(fnm) 
			SeekStream(flovl, StreamSize(flovl)) 
			WriteLine flovl, "colid:" + (255 Shl 24 | ner Shl 16 | neg Shl 8 | neb) 
			
		Else
			flovl = WriteFile(fnm) 
			WriteLine flovl, "colid:" + (255 Shl 24 | ner Shl 16 | neg Shl 8 | neb) 
			
		EndIf
	
	Local x:Int, y:Int
	Local px:Int, olr:Byte, olg:Byte, olb:Byte
	
	For x = 0 To (pm.width - 1) 
		For y = 0 To (pm.Height - 1) 
		
			px = ReadPixel(pm, x, y) 
			olr = px Shr 16 ; olg = px Shr 8 ; olb = px
			'DebugLog olr + "," + olg + "," + olb + ";" + ner + "," + neg + "," + neb
				If olr = ner And olg = neg And olb = neb
					WriteLine flovl, x + "," + y
					
				EndIf
				
		Next
	Next
	
   'WriteLine flovl, "endcolid:" + cid
  CloseFile flovl
  
End Function

Function gfx_HuePixmapTintA:TPixmap(pm:TPixmap Var, rer:Int, reg:Int, reb:Int, ner:Int, neg:Int, neb:Int, ula:Int = Null, rtp:Int = 0) 

   If Not pm Then Return Null
	
	Local pm_format:Int = PixmapFormat(pm) 
	 If (pm_format <> PF_RGBA8888) Then pm = ConvertPixmap(pm, PF_RGBA8888) 
	
	Local x:Int, y:Int
	Local sea:Int, ser:Int, seg:Int, seb:Int
	Local px:Int, olr:Byte, olg:Byte, olb:Byte, ola:Byte
	
	If ula <> Null Then ola = Byte(ula) 
	
	For x = 0 To (pm.width - 1) 
		For y = 0 To (pm.Height - 1) 
		  px = ReadPixel(pm, x, y) 
		   If ula = Null Then ola = px Shr 24
		   olr = px Shr 16 ; olg = px Shr 8 ; olb = px
			
			If rtp = 0 ' BY COLOR
				If olr = rer And olg = reg And olb = reb
				    sea = (ola + Byte(ula)) 
					 If sea < 0 Then sea = 0
					 If sea > 255 Then sea = 255
					ser = (ner + olr) 
					 If ser < 0 Then ser = 0
					 If ser > 255 Then ser = 255
					seg = (neg + olg) 
					 If seg < 0 Then seg = 0
					 If seg > 255 Then seg = 255
					seb = (neb + olb) 
					 If seb < 0 Then seb = 0
					 If seb > 255 Then seb = 255
					 
					WritePixel pm, x, y, Int(sea Shl 24 | ser Shl 16 | seg Shl 8 | seb) 
					
				EndIf
				
			ElseIf rtp = 1 ' BY MASK
				If olr <> rer And olg <> reg And olb <> reb
				    sea = (ola + Byte(ula)) 
					 If sea < 0 Then sea = 0
					 If sea > 255 Then sea = 255
					ser = (ner + olr) 
					 If ser < 0 Then ser = 0
					 If ser > 255 Then ser = 255
					seg = (neg + olg) 
					 If seg < 0 Then seg = 0
					 If seg > 255 Then seg = 255
					seb = (neb + olb) 
					 If seb < 0 Then seb = 0
					 If seb > 255 Then seb = 255
					 
					WritePixel pm, x, y, Int(sea Shl 24 | ser Shl 16 | seg Shl 8 | seb) 
					
				EndIf
				
			EndIf
			
		Next
	Next
	
   If (pm_format <> PF_RGBA8888) Then pm = ConvertPixmap(pm, pm_format) 
   
  Return pm
   
End Function

Function gfx_HuePixmapTC:TPixmap(pm:TPixmap Var, rer:Int, reg:Int, reb:Int, ner:Int, neg:Int, neb:Int, ula:Int = Null) 

   If Not pm Then Return Null
	
	Local pm_format:Int = PixmapFormat(pm)
	 If (pm_format <> PF_RGBA8888) Then pm = ConvertPixmap(pm, PF_RGBA8888) 
	
	Local x:Int, y:Int
	Local px:Int, olr:Byte, olg:Byte, olb:Byte, ola:Byte
	
	If ula <> Null Then ola = Byte(ula) 
	'DebugLog ula + "," + ola + "," + ola Shl 24
	
	For x = 0 To (pm.width - 1) 
		For y = 0 To (pm.Height - 1) 
		
			px = ReadPixel(pm, x, y) 
			If ula = Null Then ola = px Shr 24
			olr = px Shr 16 ; olg = px Shr 8 ; olb = px
		'DebugLog ola
			If olr = rer And olg = reg And olb = reb
			   Local sea:Int, ser:Int, seg:Int, seb:Int
			    sea = (ola + Byte(ula)) 
				 If sea < 0 Then sea = ola
				ser = (ner + olr) 
				 If ser < 0 Then ser = olr
				 If ser > 255 Then ser = 255
				seg = (neg + olg) 
				 If seg < 0 Then seg = olg
				 If seg > 255 Then seg = 255
				seb = (neb + olb) 
				 If seb < 0 Then seb = olb
				 If seb > 255 Then seb = 255
				 
				WritePixel pm, x, y, Int(ola Shl 24 | ner Shl 16 | neg Shl 8 | neb) 
				
			EndIf
			
		Next
	Next
	
   If (pm_format <> PF_RGBA8888) Then pm = ConvertPixmap(pm, pm_format) 
   
  Return pm
   
End Function

Function gfx_HuePixmapRC:TPixmap(pm:TPixmap Var, rer:Int, reg:Int, reb:Int, ner:Int, neg:Int, neb:Int, ula:Int = Null) 

   If Not pm Then Return Null
	
	Local pm_format:Int = PixmapFormat(pm)
	 If (pm_format <> PF_RGBA8888) Then pm = ConvertPixmap(pm, PF_RGBA8888) 
	
	Local x:Int, y:Int
	Local px:Int, olr:Byte, olg:Byte, olb:Byte, ola:Byte
	
	If ula <> Null Then ola = Byte(ula) 
	'DebugLog ula + "," + ola + "," + ola Shl 24
	
	For x = 0 To (pm.width - 1) 
		For y = 0 To (pm.Height - 1) 
		
			px = ReadPixel(pm, x, y) 
			If ula = Null Then ola = px Shr 24
			olr = px Shr 16 ; olg = px Shr 8 ; olb = px
		'DebugLog ola
			If olr = rer And olg = reg And olb = reb
				WritePixel pm, x, y, Int(ola Shl 24 | ner Shl 16 | neg Shl 8 | neb) 
				
			EndIf
			
		Next
	Next
	
   If (pm_format <> PF_RGBA8888) Then pm = ConvertPixmap(pm, pm_format) 
   
  Return pm
   
End Function
