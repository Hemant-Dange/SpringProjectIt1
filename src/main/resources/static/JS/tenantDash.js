$(document).ready(function () {
           $(".book-room-btn").click(function () {
               let roomNo = $(this).attr("data-room-no");
               $("#roomNo").val(roomNo);
               $("#bookingModalLabel").text("Book Room " + roomNo);
           });
       });