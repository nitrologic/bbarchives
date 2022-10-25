; ID: 55
; Author: Unknown
; Date: 2001-09-26 13:57:10
; Title: 2d perlin noise
; Description: needs to be optimized

;Tomis first Blitz progi
;Thanks to Hugo Elias for his nice and very, very informative webpage
;http://freespace.virgin.net/hugo.elias/
;Thanks to Mark for Blitz 
;look at Hugo Elias webpage it's very nice and his pseudo code is nearly cut&paste to blitz !
;http://freespace.virgin.net/hugo.elias/models/m_perlin.htm
;thanks to Bernd Hey he helps in the UAE project i'am proud to know him  

;main
;2 dimensional perlin noise
;very slow couse after searching to long for a bug (uncomen behavior of blitz int() function ) i'am to lazy to optimize
;At the moment each octave uses the same random generator thats poor but if someone likes to change



xsize=800 ;change graphics here
ysize=600




Graphics xsize,ysize,16,2
bias#=240

For gx# =1 To xsize    
	For gy# =1 To ysize  


	au# = perlinNoise_2d#(gx#,gy#)
	If au#>1 au#=1
	If au# <0 au#=0
	r#=au#*bias#
	g#=au#*bias#
	b#=au#*bias
	Color  r,g,b
	Plot gx#,gy#
	If KeyDown(1) Goto out
	Next
Next

.out
WaitKey
End
;end main






;Pseudo random 
Function Noise#(x#,y#,oktave#) 
n = x# + y# * 57
n = (n Shl 13 - x# Mod y#) -x# -y# -n   
Return ( 1.0 - ( (n * (n * n * 15731 + 789221) + 1376312589) And 2147483647) / 1073741824.0)
End Function 



;smooth the random numbers
Function SmoothNoise#(xl#,yl#,oktave#)
	corners# = ( Noise#(xl#-1, yl#-1,oktave#)+Noise#(xl#+1, yl#-1,oktave#)+Noise#(xl#-1, yl#+1,oktave#)+Noise#(xl#+1, yl#+1,oktave#) ) / 16 
	sides#   = ( Noise#(xl#-1, yl#,oktave#)  +Noise#(xl#+1, yl#,oktave#)  +Noise#(xl#, yl#-1,oktave#)  +Noise#(xl#, yl#+1,oktave#) ) /  8
	center#  =  Noise#(xl#, yl#,oktave#)/4  
Return (corners# + sides# + center#)
End Function


;interpolate between points
Function InterpolateNoise#(xo#,yo#,oktave#)
     integer_x#    = Floor(xo#)
      fractional_x# = xo# - integer_x#
      integer_y#    = Floor(yo#)
      fractional_y# = yo# - integer_y#
      v1# = SmoothNoise#(integer_x#,integer_y#,oktave#)
      v2# = SmoothNoise#(integer_x#+1 ,integer_y#,oktave#)
      v3# = SmoothNoise#(integer_x#,integer_y#+1 ,oktave#)
      v4# = SmoothNoise#(integer_x#+1 ,integer_y#+1 ,oktave#)
      i1# = linear_Interpolate#(v1# , v2# , fractional_x#)
      i2# = linear_Interpolate#(v3# , v4# , fractional_x#)
    Return linear_Interpolate#(i1# , i2# , fractional_y#)
End Function


Function PerlinNoise_2D#(x#,y#)
      total# = 0
      p# = 0.8   ;persistance                Try 0.1 to 1
      n# = 6 - 1 ; number of ovtaves         Try 1  to 7
      For oktave# = 0 To n#

          frequency# =  (2^oktave#)/40
          amplitude# = (p#^oktave#)

          total# = total# + (InterpolateNoise#(x# * frequency#, y# * frequency#,oktave) * amplitude#)

      Next 
      Return total#

End Function


;bugy
Function Cosine_Interpolate#(a#, b#, x#)
	ft# = x# * 180
	f# = (1 - Cos(ft#)) * 0.5
	Return  (a#*(1-f#) + b#*f#)
End Function



Function linear_Interpolate#(az#, bz#, xz#) 
Return az#*(1-xz#) + bz#*xz# 
End Function


;P.S Blitz Forums (people) are the best i have seen
 
