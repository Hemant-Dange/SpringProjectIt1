$(document).ready(function() {
	$(".select-room-btn").click(function() {
		let roomNo = $(this).attr("data-room-no");
		$("#booking-form-" + roomNo).slideDown();
		$(this).hide();
	});

	$(".cancel-btn").click(function() {
		let form = $(this).closest(".booking-form");
		form.slideUp();
		form.siblings(".select-room-btn").show();
	});

	$(".checkin-date").on("focus", function() {
		let today = new Date().toISOString().split("T")[0];
		$(this).attr("min", today);
	});

	$(".checkin-date, .checkout-date").on("change", function() {
		let form = $(this).closest(".booking-form");
		let checkinDate = form.find(".checkin-date").val();
		let checkoutDate = form.find(".checkout-date").val();

		if (checkinDate) {
			form.find(".checkout-date").attr("min", checkinDate);
		}

		if (checkoutDate && checkoutDate <= checkinDate) {
			alert("Check-out date must be later than Check-in date!");
			form.find(".checkout-date").val("");
		}
	});
});