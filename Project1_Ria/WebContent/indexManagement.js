/**
 * Login management
 */
(function() { // avoid variables ending up in the global scope

	// page components
	var loginForm, registrationForm, guestButton, newAccountButton;
	pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		pageOrchestrator.start(); // initialize the components
		pageOrchestrator.refresh();
	}, false);


	function LoginForm() {

		this.reset = function() {
			this.registrationRow.style.visibility = "hidden";
		};

		this.show = function(next) { };

		this.registerEvents = function(orchestrator) {

			document.getElementById("id_loginButton").addEventListener('click', (e) => {
				e.preventDefault();
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'CheckLogin', e.target.closest("form"),
						function(req) {
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

		}


	};

	function NewAccountButton(_buttonRow) {
		this.buttonRow = _buttonRow;
		this.reset = function() {
			this.buttonRow.classList.remove("visible");
			this.buttonRow.classList.add("invisible");
		}

		this.registerEvents = function(orchestrator) {
			document.getElementById("id_createNewAccountButton").addEventListener('click', (e) => {
				e.preventDefault();

				this.reset();
				orchestrator.showRegisterForm();
			}, false);

		};
	}
	function RegistrationForm(_registrationRow) {
		this.registrationRow = _registrationRow;
		this.reset = function() {
			this.registrationRow.classList.remove("visible")
			this.registrationRow.classList.add("invisible")

		};
		this.show = function() {
			this.registrationRow.classList.remove("invisible")
			this.registrationRow.classList.add("visible")
		};

		this.registerEvents = function(orchestrator) {

			document.getElementById("id_registrationButton").addEventListener('click', (e) => {
				e.preventDefault();

				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'Registration', e.target.closest("form"),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText;
								switch (req.status) {
									case 200:
										document.getElementById("errormessage").textContent
											= "Registration Done";
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
		}

	};

	function GuestButton() {

		this.registerEvents = function(orchestrator) {
			document.getElementById("id_continueAsGuest").addEventListener('click', (e) => {
				e.preventDefault();

				if (sessionStorage.getItem('username') === null) {
					makeCall("GET", "CheckGuest", null,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								if (req.status == 200) {
									window.location.href = "OnePage.html";
								} else {
									document.getElementById("errormessage").textContent = "Error";
								}
							}
						}
					);
				} else {
					document.getElementById("errormessage").textContent = "you Are logged";
				}
			});

		}
	};


	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");
		this.start = function() {

			sessionStorage.clear(); //Ogni volta che apro il sito pulisco le sessioni vecchie

			loginForm = new LoginForm();

			registrationForm = new RegistrationForm(
				document.getElementById("id_registrationRow"));

			guestButton = new GuestButton();

			newAccountButton = new NewAccountButton(document.getElementById("id_buttonRow"));

			loginForm.registerEvents();
			registrationForm.registerEvents();
			guestButton.registerEvents();
			newAccountButton.registerEvents(this);

		};


		this.refresh = function() {
			registrationForm.reset();

		};

		this.showRegisterForm = function() {
			registrationForm.show();
		}
	}
})();
