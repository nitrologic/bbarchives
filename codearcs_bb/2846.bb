; ID: 2846
; Author: Jimmy
; Date: 2011-05-04 12:11:52
; Title: Rotated ellipse
; Description: Rotated ellipse

Graphics  800,600,16,2:SetBuffer BackBuffer()
Repeat:Cls:xx=MouseX():yy=MouseY()
Color 0,255,0:ellipse(512,384,1+yy/2.0,200,xx/200.0)
Color 255,255,255:Plot 512,384:Plot 512+100,384:Plot 512-100,384:Plot 512,384+200:Plot 512,384-200
Flip
Until MouseDown(2)

Function ellipse (iXC#,iYC#,A#,B#,angle#)
;
; General ellipse
;
; Known bugs:
; jumpy movement sometimes, as it uses huge numbers, too large To fit storage
; It has few lines with trigonometry, these could very possibly be replaced with integer table or 
; tables of degrees Or similiar. It wraps the angle into an acceptable range, and sets xflag accordingly.
;
R2D# = 180.0/Pi:D2R# = Pi/180.0
If angle# < Pi/2.0
xflag#=1
ElseIf angle#>Pi/2 And angle#<Pi
temp#=angle#-Pi/2.0:angle#=Pi/2.0-temp#:xflag#=-1
ElseIf angle#>=Pi And angle#<Pi*1.5
angle#=angle#-Pi:xflag#=1
ElseIf angle#>=Pi*1.5 And angle#<Pi*2.0
angle#=angle#-Pi:temp#=angle#-Pi/2.0:angle#=Pi/2.0-temp#:xflag#=-1
EndIf

; ...To output these 4 integer points. (and the said xflag), These are where major and minor axis of ellipse ends (determines shape as in aligned axis algorithms).
ixa#=Int(Cos(angle#*R2D#)*A#)
iya#=Int(Sin(angle#*R2D#)*A#)
ixb#=Int(Cos((angle#+(Pi/2.0))*R2D#)*B#)
iyb#=Int(Sin((angle#+(Pi/2.0))*R2D#)*B#)

; rest of code uses only variables:
; ixa,iya,ixb,iyb
; ixc, iyc, xflag
; these are as every one else from now on, integers

Plot ixc#-ixa#,iyc#-iya#
Plot ixc#-ixb#,iyc#-iyb#
Plot ixc#-ixb#,iyc#-iya#
Plot ixc#-ixa#,iyc#-iyb#

; From now on, integers only, it uses multiplication much, and needs very large integers (in large resolutions even more than 64bits)
; therefor single precision are used to alloud the space, but theres no fixed point Or floating point involved.

   ixa2#=ixa#*ixa#
   iya2#=iya#*iya#
   ixb2#=ixb#*ixb#
   iyb2#=iyb#*iyb#
   ixaya#=ixa#*iya#
   ixbyb#=ixb#*iyb#
   ila2#=ixa2#+iya2#
   ila4#=ila2#*ila2#
   ilb2#=ixb2#+iyb2#
   ilb4#=ilb2#*ilb2#
   ia#=ixa2#*ilb4#+ixb2#*ila4#
   ib#=ixaya#*ilb4#+ixbyb#*ila4#
   ic#=iya2#*ilb4#+iyb2#*ila4#
   id#=ila4#*ilb4#

   If iYA# <= iXA#
       ; Start AT (-xA,-yA) 
       iX# = -iXA#:iY# = -iYA#:iDx# = -(iB#*iXA#+iC#*iYA#):iDy# = iA#*iXA#+iB#*iYA# 

       ; Arc FROM (-xA,-yA) TO point (x0,y0) where dx/dy = 0 
       While iDx# <= 0
           Plot iXC#+iX#*xflag#,iYC#+iY#
           Plot iXC#-iX#*xflag#,iYC#-iY#
           iY#=iY#+1
           iSigma# = iA#*iX#*iX#+2*iB#*iX#*iY#+iC#*iY#*iY#-iD# 
           If iSigma# < 0 Then iDx#=iDx#-iB#:iDy#=iDy#+iA#:iX#=iX#-1
           iDx#=iDx# + iC# 
           iDy#=iDy# - iB# 
       Wend 

       ; Arc FROM (x0,y0) TO point (x1,y1) where dy/dx = 1 
       While iDx# <= iDy# 
           Plot iXC#+iX#*xflag#,iYC#+iY# 
           Plot iXC#-iX#*xflag#,iYC#-iY# 
           iY#=iY#+1
           iXp1# = iX#+1 
           iSigma# = iA#*iXp1#*iXp1#+2*iB#*iXp1#*iY#+iC#*iY#*iY#-iD# 
           If iSigma# >= 0 Then iDx#=iDx# + iB#:iDy#=iDy# - iA#:iX# = iXp1# 
           iDx#=iDx# + iC# 
           iDy#=iDy# - iB# 
       Wend 

       ; Arc FROM (x1,y1) TO point (x2,y2) where dy/dx = 0 
       While iDy# >= 0
           Plot iXC#+iX#*xflag#,iYC#+iY#
           Plot iXC#-iX#*xflag#,iYC#-iY#
           iX=iX+1 
           iSigma# = iA#*iX#*iX#+2*iB#*iX#*iY#+iC#*iY#*iY#-iD# 
           If iSigma# < 0 Then iDx#=iDx# + iC# : iDy#=iDy# - iB# : iY#=iY#+1 
           iDx#=iDx# + iB# 
           iDy#=iDy# - iA# 
       Wend 

       ; Arc FROM (x2,y2) TO point (x3,y3) where dy/dx = -1 
       While iDy# >= -iDx# 
           Plot iXC#+iX#*xflag#,iYC#+iY#
           Plot iXC#-iX#*xflag#,iYC#-iY#
           iX#=iX#+1 
           iYm1# = iY#-1 
           iSigma# = iA#*iX#*iX#+2*iB#*iX#*iYm1#+iC#*iYm1#*iYm1#-iD# 
           If iSigma# >= 0 Then iDx#=iDx# - iC#:iDy#=iDy# + iB#:iY# = iYm1# 
           iDx#=iDx# + iB# 
           iDy#=iDy# - iA# 
       Wend 

       ; Arc FROM (x3,y3) TO (xa,ya) 
       While iY# >= iYA# 
           Plot iXC#+iX#*xflag#,iYC#+iY#
           Plot iXC#-iX#*xflag#,iYC#-iY#
           iY#=iY#-1
           iSigma# = iA#*iX#*iX#+2*iB#*iX#*iY#+iC#*iY#*iY#-iD# 
           If iSigma# < 0 Then iDx#=iDx# + iB#:iDy#=iDy# - iA#:iX#=iX#+1 
           iDx#=iDx# - iC# 
           iDy#=iDy# + iB# 
       Wend 

   Else 
       ; Start AT (-xa,-ya) 
       iX# = -iXA#:iY# = -iYA#:iDx# = -(iB#*iXA#+iC#*iYA#):iDy# = iA#*iXA#+iB#*iYA#

       ; Arc FROM (-xa,-ya) TO point (x0,y0) where dy/dx = -1 
       While -iDx# >= iDy# 
           Plot iXC#+iX#*xflag#,iYC#+iY#
           Plot iXC#-iX#*xflag#,iYC#-iY# 
           iX#=iX#-1
           iYp1# = iY#+1 
           iSigma# = iA#*iX#*iX#+2*iB#*iX#*iYp1#+iC#*iYp1#*iYp1#-iD# 
           If iSigma# >= 0 Then iDx#=iDx# + iC# : iDy#=iDy# - iB#:iY# = iYp1# 
           iDx#=IDx# - iB# 
           iDy#=Idy# +iA# 
       Wend 

       ; Arc FROM (x0,y0) TO point (x1,y1) where dx/dy = 0 
       While iDx# <= 0
           Plot iXC#+iX#*xflag#,iYC#+iY#
           Plot iXC#-iX#*xflag#,iYC#-iY#
           iY#=iY#+1
           iSigma# = iA#*iX#*iX#+2*iB#*iX#*iY#+iC#*iY#*iY#-iD# 
           If iSigma# < 0 Then iDx#=iDx# - iB#:iDy#=iDy# + iA#:iX#=iX#-1
           iDx#=iDx# + iC# 
           iDy#=iDy# - iB# 
       Wend 

       ; Arc FROM (x1,y1) TO point (x2,y2) where dy/dx = 1 
       While iDx# <= iDy# 
           Plot iXC#+iX#*xflag#,iYC#+iY#
           Plot iXC#-iX#*xflag#,iYC#-iY#
           iY#=iY#+1
           iXp1# = iX#+1 
           iSigma# = iA#*iXp1#*iXp1#+2*iB#*iXp1#*iY#+iC#*iY#*iY#-iD# 
           If iSigma# >= 0 Then iDx#=IDx# + iB# :iDy#=Idy# - iA# : iX# = iXp1# 
           iDx#=iDx# + iC# 
           iDy#=iDy# - iB# 
       Wend 

       ; Arc FROM (x2,y2) TO point (x3,y3) where dy/dx = 0 
       While iDy# >= 0
           Plot iXC#+iX#*xflag#,iYC#+iY#
           Plot iXC#-iX#*xflag#,iYC#-iY# 
           iX#=iX#+1 
           iSigma# = iA#*iX#*iX#+2*iB#*iX#*iY#+iC#*iY#*iY#-iD# 
           If iSigma# < 0 Then iDx#=iDx# + iC# : iDy#=iDy# - iB# : iY#=iY#+1 
           iDx#=iDx# + iB# 
           iDy#=iDy# - iA# 
       Wend 

       ; Arc FROM (x3,y3) TO (xa,ya) 
       While iX# <= iXA#
           Plot iXC#+iX#*xflag#,iYC#+iY# 
           Plot iXC#-iX#*xflag#,iYC#-iY# 
           iX#=iX#+1 
           iYm1# = iY#-1 
           iSigma# = iA#*iX#*iX#+2*iB#*iX#*iYm1#+iC#*iYm1#*iYm1#-iD# 
           If iSigma# >= 0 Then iDx#=iDx# - iC# : iDy#=iDy# + iB# :iY# = iYm1# 
           iDx#=iDx# + iB# 
           iDy#=iDy# - iA# 
       Wend 

   EndIf
End Function
