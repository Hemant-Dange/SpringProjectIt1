$(document).ready(function() {
	let rowsPerPage = 3;
	let rows = $("#allBookingsTable tbody tr");
	let totalRows = rows.length;
	let currentPage = 1;

	function showPage(page) {
		rows.hide();
		let start = (page - 1) * rowsPerPage;
		let end = start + rowsPerPage;
		rows.slice(start, end).show();
		$("#pageNumber").text("Page " + page);
	}

	function updateButtons() {
		$("#prevPage").prop("disabled", currentPage === 1);
		$("#nextPage").prop("disabled", currentPage * rowsPerPage >= totalRows);
	}

	$("#prevPage").click(function() {
		if (currentPage > 1) {
			currentPage--;
			showPage(currentPage);
			updateButtons();
		}
	});

	$("#nextPage").click(function() {
		if (currentPage * rowsPerPage < totalRows) {
			currentPage++;
			showPage(currentPage);
			updateButtons();
		}
	});

	showPage(currentPage);
	updateButtons();
});