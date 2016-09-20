function videoPlayer() {
	
	var idValue = $('video').attr('id');
	var video = document.getElementById(idValue);
	
	$('#' + idValue).click(function(event) {
		if(video.paused) {
			video.play();
			$('#end-video').css('display', 'none');
		} else {
			video.pause();
			$('#end-video').css('display', 'inline');
		}
	});

	video.addEventListener("ended", function() {
		$('#end-video').css('display', 'inline');
	});
	
	$('#end-video').click(function(event) {
		// 호출할 메소드 작성(ex>AndroidBridge 클래스)
		// ex> window.MyApp.getBackEvent();
		
		
		// ...
		// webview.addJavascriptInterface(new AndroidBirdge(), "MyApp");
		// ...
	});
	
}











