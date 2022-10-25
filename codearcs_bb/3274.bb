; ID: 3274
; Author: Bobysait
; Date: 2016-06-13 10:15:34
; Title: Unreversible Password Encription
; Description: Encrypt a password with a key

SuperStrict

FrameWork BRL.Random
Import BRL.LinkedList
Import BRL.Map
Import BRL.Math

Import BRL.StandardIO


Type TPassSerializer
	
	Field m_K:String;
	Field m_K1024:String;
	Field m_KL:Int;
	Field m_CaseSensitive:Byte;
	
	Function ResizeStr:String(s:String, l:Int, pCaseSensitive:Byte=True)
		If (pCaseSensitive=False) Then s=s.ToLower();
		local n:Int = s.Length;
		If n>l Then Return s[..l];
		Local nb:Int = Ceil(Float(l)/n);
		Local o:String="";
		Local i:Int = 0;
		For i = 0 Until nb
			o :+ s
		Next
		Return o[..l];
	End Function
	
	Method GenerateKey:String()
		Local l:TList = New TList, a:Int;
		Local pass:Int = 3, i:Int=0;
		For i = 0 Until pass
			For a = 0 To 25; l.AddLast(Chr("a"[0]+a)); l.AddLast(Chr("A"[0]+a)); Next
			For a = 0 To 9; l.AddLast(Chr("0"[0]+a)); Next;
		Next
		Self.m_k = ""
		While l.Count()>0
			Local i:Int = Rand(0,l.Count()-1);
			Local ar:Object[] = l.ToArray();
			Self.m_k :+ String(ar[i]);
			l.Remove(ar[i]);
		Wend
		Self.m_KL = Self.m_k.Length;
		Return Self.m_k;
	End Method
	
	Method Create:TPassSerializer(k:String="", pCaseSensitive:Byte=True)
		Self.m_K = k; If k=""  Then Self.GenerateKey();
		Self.m_KL = Self.m_k.Length;
		Self.m_K1024 = TPassSerializer.ResizeStr(Self.m_K,1024);
		Self.m_CaseSensitive = pCaseSensitive;
		Return Self;
	End Method
	
	Method Serialize:String(s$,ln:Int=-1)
		If s="" Then Return ""
		If ln<0 Then ln=s.Length;
		s = ResizeStr(s,ln, Self.m_CaseSensitive);
		Local i:Int = 0, o:String = "";
		For i = 0 Until ln
			o :+ Chr(Self.m_K1024[ s[i] + Self.m_K1024[i] ])
		Next
		Return o;
	End Method
	
	Method Serialize2:String(s$,p$,ln:Int=-1)
		If s="" Then Return ""
		If p="" Then Return ""
		If ln<0 Then ln=Max(s.Length,p.Length);
		s = ResizeStr(s,ln, Self.m_CaseSensitive);
		p = ResizeStr(p,ln, Self.m_CaseSensitive);
		Local i:Int = 0, o:String = "";
		For i = 0 Until ln
			o :+ Chr(Self.m_K1024[ s[i] + p[i] + Self.m_K1024[i] ])
		Next
		Return o;
	End Method
	
	Method Serialize3:String(s$,p$,m$,ln:Int=-1)
		If s="" Then Return ""
		If p="" Then Return ""
		If m="" Then Return ""
		If ln<0 Then ln=Max(Max(s.Length,p.Length),m.Length);
		s = ResizeStr(s,ln, Self.m_CaseSensitive);
		p = ResizeStr(p,ln, Self.m_CaseSensitive);
		m = ResizeStr(m,ln, Self.m_CaseSensitive);
		Local i:Int = 0, o:String = "";
		For i = 0 Until ln
			o :+ Chr(Self.m_K1024[ s[i] + p[i] + m[i] + Self.m_K1024[i] ])
		Next
		Return o;
	End Method
	
	Method GetKey:String()
		Return Self.m_K;
	End Method
	
End Type


' this one is not case-sensitive, it can allow to log without taking care of the capitalization.
' to use a case-sensitive mode, just set the second argument to True.
Local sr:TPassSerializer = New TPassSerializer.Create(,False)
Print "key="+sr.GetKey()
Print "serialize : '"+sr.Serialize2("My Pass-Word", "MyNameIs", 64)+"'"
Print "serialize : '"+sr.Serialize3("My Pass-Word", "MyNameIs", "MyMailAdress@Server.COM", 64)+"'"
Print "serialize : '"+sr.Serialize3("my PASS-WORD", "mynameis", "MyMailAdress@server.com", 64)+"'"
