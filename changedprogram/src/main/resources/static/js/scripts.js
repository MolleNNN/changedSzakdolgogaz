$(document).ready(function(){
    $('.navbar-nav .nav-link, .kezdolap-sign').on('click', function(){
        $(this).animate({
            fontSize: '1.1em'
        }, 100, function(){
            $(this).animate({
                fontSize: '1em'
            }, 100);
        });
    });
});
