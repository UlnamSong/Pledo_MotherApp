function videoPlay() {
	
	$(window).ready(function() {
		
		var q01width = $('#qPosition01').width();
		var q01height = $('#qPosition01').height();
		var q01Position = $('#qPosition01').offset();
		
		$('#solve').css('width', q01width + 10)
							 .css('height', q01height)
							 .css("top", q01Position.top - 4)
							 .css("left", q01Position.left);
		var boxPosition = $('#solve').offset();
		
		var t_url = "hint_01.mp4";
		
		window.android.callVideoPlay(t_url);
		
	});
	
}

function solve() {
	
	$(window).ready(function() {
		
		var q01width = $('#qPosition01').width();
		var q01height = $('#qPosition01').height();
		var q01Position = $('#qPosition01').offset();
		
		$('#solve').css('width', q01width + 10)
							 .css('height', q01height)
							 .css("top", q01Position.top - 4)
							 .css("left", q01Position.left);
		var boxPosition = $('#solve').offset();
		
		var answer = $('#qPosition01').attr('ans');
		
		window.android.setAnswer(answer, q01width, q01height, 
														 q01Position.top, q01Position.left);
		
	});
	
}

function correctAnswer() {
	$('#solve').css('display', 'none');
}
function incorrectAnswer(value) {
	$('#incor_ans').text(value);
}