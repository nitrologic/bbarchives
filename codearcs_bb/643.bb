; ID: 643
; Author: ShadowTurtle
; Date: 2003-04-06 08:16:36
; Title: WebSearch Plus 1.0
; Description: Multiple Web-search :)

Type Searcher
  Field Name$
  Field NameBig$
  Field URLSearch$
  Field PageURL$
End Type

Type Search_Window
  Field Win
  Field HtmlSite
  Field HtmlLink$
  Field SearchWith.Searcher
End Type

Google.Searcher = New Searcher
Google\Name$ = "Google (ger)"
Google\NameBig$ = "Google (ger)"
Google\URLSearch$ = "http://www.google.de/search?q=%searchtext%&ie=UTF-8&oe=UTF-8&hl=de&meta="

Freenet.Searcher = New Searcher
Freenet\Name$ = "Freenet (ger)"
Freenet\NameBig$ = "Freenet (ger)"
Freenet\URLSearch$ = "http://suche.freenet.de/suche?query=%searchtext%&page=1&ink=385950&target=freenet&js=on"

PageSeeker.Searcher = New Searcher
PageSeeker\Name$ = "PageSeeker (eng)"
PageSeeker\NameBig$ = "PageSeeker (eng)"
PageSeeker\URLSearch$ = "http://www.pageseeker.com/results.htm?start=0&shareid=1&domainid=&directory=&ppsid=&search=%searchtext%"

Global SmallTitle$ = "Welcome to WebSearch Plus. You can search with WebSearchPlus to all websites. ... (sorry for my bad english)"

Global SmallTitle_Pos = 0, SmallTitle_Length = 30, SmallTitle_PixLength = 200, FH = 47
Global FLength = 120, NL = 5

Global Win = CreateWindow("WebSearch Plus", 0, 0, ClientWidth(Desktop()), FH+5, Desktop(), 1)
Global obja = CreateLabel("...", 5, 5, SmallTitle_PixLength, FH-5, Win)

Global fr_a_a = CreateLabel("Search: ", NL + SmallTitle_PixLength, 6, FLength/2, FH-5-22, Win) : NL = NL + (FLength/2) + 3
Global fr_a_b = CreateTextField(NL + SmallTitle_PixLength, 4, FLength*2, FH-30, Win) : NL = NL + (FLength*2) + 5

Global fr_b_a = CreateLabel("Search Engine: ", NL + SmallTitle_PixLength, 6, FLength/1.5, FH-8, Win) : NL = NL + (FLength/1.5) + 3
Global fr_b_b = CreateComboBox(NL + SmallTitle_PixLength, 4, FLength/1.3, FH-30, Win) : NL = NL + (FLength/1.3) + 5

Global fr_c_a = CreateButton("Find", NL + SmallTitle_PixLength, 6, FLength/1.5, FH-8-22, Win) : NL = NL + (FLength/1.5) + 3

Global MyPosY = 0
Global MyPosX = 0

AddGadgetItem fr_b_b, "All"
For Searcher.Searcher = Each Searcher
  AddGadgetItem fr_b_b, Searcher\Name$
Next

SelectGadgetItem fr_b_b, 0

Global LiblTimer = CreateTimer(10)

While Not KeyHit(1)
  vka = WaitEvent(LiblTimer)
  
  If LiblTimer Then
    If SmallTitle_Pos > Len(SmallTitle$) + SmallTitle_Length + 5 Then SmallTitle_Pos = 0
    SmallTitle_Pos = SmallTitle_Pos + 1
    SetGadgetText obja, Mid$(SmallTitle$, SmallTitle_Pos, SmallTitle_Length)
  End If

  If EventSource() = fr_a_b Then
    S$ = TextFieldText(fr_a_b)
    S$ = Replace(S$, " ", "+")
    SetGadgetText fr_a_b, S$
  End If

  If (EventSource() = fr_c_a) Or (EventData()=13 And EventSource() = fr_a_b) Then
    If SelectedGadgetItem(fr_b_b)>0 Then
      tmp = 0
      For Searcher.Searcher = Each Searcher
        If SelectedGadgetItem(fr_b_b) = tmp+1 Then NewSW(Searcher, TextFieldText(fr_a_b))
        tmp=tmp+1
      Next
    Else
      For Searcher.Searcher = Each Searcher
        NewSW(Searcher, TextFieldText(fr_a_b))
      Next
    End If
    SetGadgetText fr_a_b, ""
  End If

  If EventID() = $803 Then
    For Search_Window.Search_Window = Each Search_Window
      If EventSource() = Search_Window\Win Then
        FreeGadget Search_Window\Win
        Delete Search_Window
      End If
    Next

    If EventSource() = Win Then
      For Search_Window.Search_Window = Each Search_Window
        If EventSource() = Search_Window\Win Then
          FreeGadget Search_Window\Win
          Delete Search_Window
        End If
      Next

      End
    End If
  End If
Wend

Function NewSW(Searcher.Searcher, searchnow$)
  Local groX=500,groY=400

  Search_Window.Search_Window = New Search_Window
  Search_Window\HtmlLink$ = Replace(Searcher\URLSearch$, "%searchtext%", searchnow$)
  Search_Window\SearchWith = Searcher

  Search_Window\Win = CreateWindow("Search "+Chr$(34)+searchnow$+Chr$(34)+" with "+Searcher\NameBig$, 10+MyPosX, FH+20+MyPosY, groX, groY, Win, 50-32)
  Search_Window\HtmlSite = CreateHtmlView(0,0,groX-8,groY-25,Search_Window\Win)
  HtmlViewGo Search_Window\HtmlSite, Search_Window\HtmlLink$

  SetGadgetLayout Search_Window\HtmlSite, 1,1,1,1

  MyPosY = MyPosY + 40 : MyPosX = MyPosX + 40
  If MyPosY > 400 Then MyPosY = 0
  If MyPosX > 400 Then MyPosX = 0
End Function
