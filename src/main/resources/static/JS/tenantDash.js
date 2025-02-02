$(document).ready(function () {
    const rooms = [
        { id: 1, status: "vacant" },
        { id: 2, status: "occupied" },
        { id: 3, status: "vacant" },
        { id: 4, status: "occupied" },
        { id: 5, status: "vacant" },
        { id: 6, status: "occupied" },
        { id: 7, status: "vacant" },
        { id: 8, status: "vacant" }
    ];

    let bookingCount = 0; // Counter for booking entries

    // Populate room grid
    const roomGrid = $("#roomGrid");
    rooms.forEach(room => {
        const roomDiv = $("<div>")
            .addClass("room")
            .addClass(room.status === "vacant" ? "vacant" : "occupied")
            .text(`Room ${room.id}`)
            .attr("data-id", room.id);
        roomGrid.append(roomDiv);
    });

    // Handle room selection
    $(".room").click(function () {
        if ($(this).hasClass("vacant")) {
            $(".room").removeClass("selected");
            $(this).addClass("selected").css("outline", "2px solid yellow");
        } else {
            alert("This room is already occupied. Please select a vacant room.");
        }
    });

    // Handle booking form submission
    $("#bookingForm").submit(function (event) {
        event.preventDefault();
        const selectedRoom = $(".room.selected").data("id");
        const checkInDate = $("#checkInDate").val();
        const checkOutDate = $("#checkOutDate").val();

        if (!selectedRoom) {
            alert("Please select a room.");
        } else if (!checkInDate || !checkOutDate) {
            alert("Please select both check-in and check-out dates.");
        } else {
            // Remove "No history found" message if present
            $("#bookingLog .no-history").remove();

            // Add booking entry to the log
            bookingCount++;
            $("#bookingLog").append(`
                <tr id="booking-${bookingCount}">
                    <td>${bookingCount}</td>
                    <td>Room ${selectedRoom}</td>
                    <td>${checkInDate}</td>
                    <td>${checkOutDate}</td>
                    <td id="status-${bookingCount}">Pending</td>
                    <td>
                        <button class="btn btn-danger btn-sm cancel-booking" data-id="${bookingCount}">Cancel</button>
                    </td>
                </tr>
            `);

            alert(`Room ${selectedRoom} booked successfully from ${checkInDate} to ${checkOutDate}! The status is pending approval.`);
        }
    });

    // // Validate date selection
    // $('#bookingForm').on('submit', function (e) {
    //     e.preventDefault();
  
    //     var checkInDate = new Date($('#checkInDate').val());
    //     var checkOutDate = new Date($('#checkOutDate').val());
  
    //     if (checkOutDate <= checkInDate) {
    //       alert('Check-out date must be greater than check-in date.');
    //       return;
    //     }
  
    //     // Assuming a room is selected (you can add your own logic to select a room)
    //     var selectedRoom = $('.room.selected');
    //     if (selectedRoom.length === 0) {
    //       alert('Please select a room.');
    //       return;
    //     }
  
    //     // Update room status to occupied
    //     selectedRoom.removeClass('vacant').addClass('occupied').text('Occupied');
  
    //     // Optionally, you can add the booking details to the booking log
    //     var roomId = selectedRoom.data('room-id');
    //     var bookingLog = $('#bookingLog');
    //     var newRow = `
    //       <tr>
    //         <td>${roomId}</td>
    //         <td>Room ${roomId}</td>
    //         <td>${$('#checkInDate').val()}</td>
    //         <td>${$('#checkOutDate').val()}</td>
    //         <td>Occupied</td>
    //         <td><button class="cancel-booking btn btn-danger">Cancel</button></td>
    //       </tr>
    //     `;
    //     bookingLog.append(newRow);
  
    //     // Clear the form
    //     $('#bookingForm')[0].reset();
    //   });
  
    //   // Dynamic password match message
    //   $('#tenantConfirmPassword').on('input', function () {
    //     var password = $('#tenantPassword').val();
    //     var confirmPassword = $(this).val();
    //     if (password !== confirmPassword) {
    //       $(this).get(0).setCustomValidity('Passwords do not match.');
    //     } else {
    //       $(this).get(0).setCustomValidity('');
    //     }
    //   });
  
    //   // Redirect to login page on close
    //   $('#closePopup').on('click', function () {
    //     window.location.href = 'login.html';
    //   });
  
    //   // Room selection logic (example)
    //   $('.room').on('click', function () {
    //     if (!$(this).hasClass('occupied')) {
    //       $('.room').removeClass('selected');
    //       $(this).addClass('selected');
    //     }
    //   });

    // // Handle booking cancellation
    // $(document).on("click", ".cancel-booking", function () {
    //     const bookingId = $(this).data("id");
    //     const statusCell = $(`#status-${bookingId}`);

    //     // Change status to "Cancelled"
    //     statusCell.text("Cancelled").css("color", "gray");

    //     // Remove the cancel button after cancellation
    //     $(this).remove();

    //     alert("Booking has been cancelled.");
    // });


});
