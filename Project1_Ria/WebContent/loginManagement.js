/**
 * Login management
 */

(function () { // avoid variables ending up in the global scope

	document.getElementById("loginbutton").addEventListener('click', (e) => {
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'CheckLogin', e.target.closest("form"),
				function (req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								sessionStorage.setItem('username', message);
								window.location.href = "OnePage.html";
								break;
							case 400: // bad request
								document.getElementById("errormessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("errormessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("errormessage").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});

})();


(function () {
	document.getElementById("continueAsGuest").addEventListener('click', (e) => {
		if (sessionStorage.getItem('username') == null) {
			makeCall("GET", "CheckGuest", null,
				function (req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							window.location.href = "OnePage.html";
						}
					 else {
						document.getElementById("errormessage").textContent = "Error";
					}
				}
			}
			);
		} else {
			document.getElementById("errormessage").textContent = "you Are logged";
		}
	});
})();
