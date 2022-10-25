; ID: 129
; Author: Ratchet
; Date: 2001-12-27 08:40:29
; Title: WinXP styled menus in BlitzBasic!
; Description: This include allows to create Windows XP© styled menus in your BB application

;Save this file and use the include command
;------------------------------------------------BlitzMenu V 1.2.2 by Ratchet--------------------------------------------------------;
;                                                                                                                                    ;
;                                                                                                                                    ;
;How to use the function:                                                                                                            ;
;BlitzMenu(id, caption$, x, y, itemcount, itemtxt$, [itemwidth], [opensnd], [clicksnd], [itemimage])                                 ;
;                                                                                                                                    ;
;id        - The identification number of the menu !Lowest Value is 1!                                                               ;
;caption   - Caption of the menu (File, Edit...)                                                                                     ;
;x, y      - Position of menu                                                                                                        ;
;itemcount - Count of menuitems                                                                                                      ;
;itemtxt   - Text of menuitems (the texts are separated by semicolons)                                                               ;
;itemwidth - Width of items (default is 100)                                                                                         ;
;opensnd   - Sound on open up the menu (0 means no sound)                                                                            ;
;clicksnd  - Sound on item click (0 means no sound)                                                                                  ;
;itemimage - image for the items (use LoadAnimImage; image height should be 18 pix; std. maskcolor is 255, 0, 255)                   ;
;                                                                                                                                    ;
;Example:                                                                                                                            ;
;                                                                                                                                    ;
;BlitzMenu(1, "File", 30, 40, 4, "New;Open;Save;Quit", 160, snd1, 0, itempic)                                                        ;
;                                                                                                                                    ;
;This draw a menu with caption file and four items named                                                                             ;
;New, Open, Save and Quit                                                                                                            ;
;                                                                                                                                    ;
;Next Steps:                                                                                                                         ;
;  - Shadows                                                                                                                         ;
;  - Dividing lines                                                                                                                  ;
;  - alphablend-in menu                                                                                                              ;
;  - submenus                                                                                                                        ;
;  - popup menus                                                                                                                     ;
;  - keyboard control                                                                                                                ;
;------------------------------------------------------------------------------------------------------------------------------------;
Dim menuid(10) ;Maximum of Menus

Function BlitzMenu(id, caption$, x, y,itemcount, itemtxt$, itemwidth = 100, opensnd = 0, clicksnd = 0, itemimage = 0)
Local result
  If Not itemimage = 0 Then MaskImage itemimage, 255, 0, 255
  SetBuffer BackBuffer()
  Color 219, 216, 209
  Rect x, y, StringWidth(caption) + 10, 18
  ;Dark border
  If (MouseX() > x) And (MouseX() < x + (StringWidth(caption) + 10)) And (MouseY() > y) And (MouseY() < y + 18) Then
    If (menuid(id - 1) = True) Or (menuid(id + 1) = True) Then
      If Not opensnd = 0 Then PlaySound(opensnd)
      menuid(id)     = True
      menuid(id - 1) = False
      menuid(id + 1) = False      
    End If
    If MouseHit(1) Then
      menuid(id) = Not menuid(id)
      If Not opensnd = 0 Then PlaySound(opensnd)
    End If
    If Not menuid(id) Then 
      Color 182, 189, 210
    Else
      Color 219, 216, 209
    End If
    Rect x, y, StringWidth(caption) + 10, 19
    Color 10, 36, 106
    Line x, y, x, y + 18
    Line x, y, x + StringWidth(caption) + 10, y
    Line x + StringWidth(caption) + 10, y, x + StringWidth(caption) + 10, y + 18
    Line x, y + 18, x + StringWidth(caption) + 10,  y + 18
  End If
  Color 0, 0, 0
  If menuid(id) Then
    Text x + (StringWidth(caption) / 2) + 6, y + 10, caption, True, True
    Color 102, 102, 102
    Line x, y, x, y + 18
    Line x, y, x + StringWidth(caption) + 10, y
    Line x + StringWidth(caption) + 10, y, x + StringWidth(caption) + 10, y + 18
    ;Background of items
    Color 102, 102, 102
    Rect x, y + 18, itemwidth + 2, (22 * itemcount) + 4
    Color 255, 255, 255
    Rect x + 1, y + 19, itemwidth, (22 * itemcount) + 2
    Color 219, 216, 209
    Line x + 1, y + 18, x + (StringWidth(caption) + 10) - 1, y + 18
    Color 219, 216, 209
    Rect x + 2, y + 20, 25, (22 * itemcount)
    ;Items
    If (MouseX() > x + 2) And (MouseX() < x + (itemwidth - 2)) Then
      For i = y + 20 To ((itemcount * 22) + y) Step 22
        If (MouseY() > i) And (MouseY() < i + 22) Then
          If MouseHit(1) Then
            If Not clicksnd = 0 Then PlaySound(clicksnd)
            result =  (i - y - 20) / 22 + 1
            menuid(id) = False
            Exit
          End If
          Color 10, 36, 106
          Rect x + 2, i, itemwidth - 2, 22
          Color 182, 189, 210
          Rect x + 3, i + 1, itemwidth - 4, 20
        End If
        ;Draw itempictures
        If Not itemimage = 0 Then DrawImage itemimage, x + 4, i + 2, (i - y - 20) / 22
      Next
    End If
    FlushMouse()
    ;Item text
    For i = y + 20 To ((itemcount * 22) + y) Step 22
      Color 0, 0, 0
      Text x + 32, i + 11, MenuItemText(itemtxt, (i - y - 20) / 22 + 1), False, True
      If Not itemimage = 0 Then DrawImage itemimage, x + 4, i + 2, (i - y - 20) / 22
    Next
  Else
    Text x + (StringWidth(caption) / 2) + 5, y + 9, caption, True, True
  End If
  Return result
End Function

Function MenuItemText$(txt$, number)
Local startpos, endpos, counter
  While Not counter = number - 1
    startpos = Instr(txt, ";", startpos + 1)
    counter = counter + 1
  Wend
  endpos = Instr(txt, ";", startpos + 1)
  Return Mid(txt, startpos + 1, endpos - startpos - 1)
End Function
