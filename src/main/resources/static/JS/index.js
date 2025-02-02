$(document).ready(function() {

    // Function to reveal elements on scroll
    function revealOnScroll() {
        $('.animate').each(function() {
            var elementTop = $(this).offset().top;
            var windowBottom = $(window).scrollTop() + $(window).height();

            if (elementTop < windowBottom - 100) { 
                $(this).addClass('visible'); // Add the visible class when in viewport
            }
        });
    }

    // Function to check when the section reaches the footer
    function checkFooterOverlap() {
        var sectionBottom = $('.services-section').offset().top + $('.services-section').outerHeight();
        var footerTop = $('.footer').offset().top;

        if (sectionBottom > footerTop) {
            $('.services-section').css('transform', 'translateY(20px)'); // Moves the section down slightly
        } else {
            $('.services-section').css('transform', 'translateY(0)');
        }
    }

    // Function to blur the carousel when scrolling
    function toggleBlurEffect() {
        if ($(window).scrollTop() > 50) {
            $('body').addClass('scrolled'); // Apply blur effect
        } else {
            $('body').removeClass('scrolled'); // Remove blur effect
        }
    }

    // Trigger functions on page load
    revealOnScroll();
    checkFooterOverlap();
    toggleBlurEffect();

    // Trigger functions on scroll
    $(window).on('scroll', function() {
        revealOnScroll();
        checkFooterOverlap();
        toggleBlurEffect();
    });
});
