$(document).ready(function() {

    function revealOnScroll() {
        $('.animate').each(function() {
            var elementTop = $(this).offset().top;
            var windowBottom = $(window).scrollTop() + $(window).height();

            if (elementTop < windowBottom - 100) { 
                $(this).addClass('visible'); 
            }
        });
    }

    function checkFooterOverlap() {
        var sectionBottom = $('.services-section').offset().top + $('.services-section').outerHeight();
        var footerTop = $('.footer').offset().top;

        if (sectionBottom > footerTop) {
            $('.services-section').css('transform', 'translateY(20px)'); 
        } else {
            $('.services-section').css('transform', 'translateY(0)');
        }
    }

    function toggleBlurEffect() {
        if ($(window).scrollTop() > 50) {
            $('body').addClass('scrolled'); 
        } else {
            $('body').removeClass('scrolled'); 
        }
    }

    revealOnScroll();
    checkFooterOverlap();
    toggleBlurEffect();

    $(window).on('scroll', function() {
        revealOnScroll();
        checkFooterOverlap();
        toggleBlurEffect();
    });
});
