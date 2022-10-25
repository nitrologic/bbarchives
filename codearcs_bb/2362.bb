; ID: 2362
; Author: Ross C
; Date: 2008-11-22 19:20:01
; Title: Pie chart code
; Description: Creates a pie chart, based on x number of values

Graphics 640,480
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Type segment
   Field angle#
   Field value#
End Type

; just some sizes for the pie chart, based on the graphics resolution, so it will scale ok.
Global pie_chart_start_x# = GraphicsWidth()/2
Global pie_chart_start_y# = GraphicsHeight()/2

Global pie_chart_size# = GraphicsHeight()/2.2
Global segment_text_position# = 0.75 ; a value between 0 and 1: 0 = at the middle. 1 = at the edge.

Global total_value ; this is calculated below, when generating the type object segments.

; create 6 chart segments
For loop = 1 To 6
	s.segment = New segment
	s\value# = Rand(10,100)
	total_value = total_value + s\value
Next

While Not KeyHit(1)

	Cls

	draw_pie_chart()

	Flip
Wend

Function draw_pie_chart()

    ; draw the circle shape of the pie chart. use the start X and Y and the pie chart size here.
	Oval pie_chart_start_x - pie_chart_size, pie_chart_start_y - pie_chart_size,pie_chart_size*2,pie_chart_size*2,0

	Local angle# = 90 ; set this to 90 to overcome blitz rotation system, so it start vertically.
	Local angle_gap# ; used to work out the angle size, based on the value in the type object.
	                 ; as always: (value / total value)*100 gives you a percentage.
	Local text_angle#; i take half of the angle_gap and use this angle to place the visual value.

	; draw initial line
	Line pie_chart_start_x, pie_chart_start_y, pie_chart_start_x - (Cos(angle)*pie_chart_size), pie_chart_start_y - (Sin(angle)*pie_chart_size)
	; draw the total value
	Text pie_chart_start_x, pie_chart_start_y - (pie_chart_size*1.05), total_value,True,True

	For s.segment = Each segment
		
		angle_gap = 360 * (s\value / total_value)
		text_angle = angle_gap/2
		angle = angle + angle_gap
		
		; BELOW: draw a line to show the segment.
		Line pie_chart_start_x, pie_chart_start_y, pie_chart_start_x - (Cos(angle)*pie_chart_size), pie_chart_start_y - (Sin(angle)*pie_chart_size)
		; BELOW: place the visual value of the type object, halfway between the centre and circumfirance.
		Text pie_chart_start_x - (Cos(angle - text_angle)*(pie_chart_size*segment_text_position)), pie_chart_start_y - (Sin(angle - text_angle)*(pie_chart_size*segment_text_position)),s\value,True,True

		
	Next
	
End Function
