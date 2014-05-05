$(document).ready(function(){

	// The small arrow that marks the active search icon:
	var arrow = $('<span>',{className:'arrow'}).appendTo('ul.icons');
	
	$('ul.icons li').click(function(){
		var el = $(this);
		
		if(el.hasClass('active')){
			// The icon is already active, exit
			return false;
		}
		
		el.siblings().removeClass('active');
		el.addClass('active');

		// $('hiddenbox').value = el.attr('class');
		var newclassname = el.attr('class');
		console.log(newclassname);
		$('#hiddenbox').val(newclassname);
        var newinputvalue = $('#hiddenbox').val();
		console.log(newinputvalue);
		
		// Move the arrow below this icon
		arrow.stop().animate({
			left		: el.position().left,
			marginLeft	: (el.width()/2)-4
		});
		

	});

	// Marking the web search icon as active:
	$('li.web').click();
	
	// Focusing the input text box:
	$('#s').focus();


	
	
});
