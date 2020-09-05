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
			this.buttonRow.classList.remove("invisible");
			this.buttonRow.classList.add("visible");
		}

		this.hide = function() {
			this.buttonRow.classList.remove("visible");
			this.buttonRow.classList.add("invisible");
		}

		this.registerEvents = function(orchestrator) {
			document.getElementById("id_createNewAccountButton").addEventListener('click', (e) => {
				e.preventDefault();

				this.hide();
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

					var email = e.target.closest("form").email.value;
					var password1 = e.target.closest("form").password.value;
					var password2 = e.target.closest("form").passwordReinserted.value;
					var mailValidate = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email);
					console.log(mailValidate);

					if (password1 === password2 && mailValidate) {

						form.email.closest("input").classList.remove("is-invalid");
						form.password.closest("input").classList.remove("is-invalid");
						form.passwordReinserted.closest("input").classList.remove("is-invalid");
						makeCall("POST", 'Registration', e.target.closest("form"),
							function(req) {

								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText;
									switch (req.status) {
										case 200:
											orchestrator.refresh();
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


					}

					else {
						if (mailValidate && password1 !== password2) {
							document.getElementById("errormessage").textContent = "password are different";
							e.target.closest("form").password.closest("input").classList.add("is-invalid");
							e.target.closest("form").passwordReinserted.closest("input").classList.add("is-invalid");
							e.target.closest("form").email.closest("input").classList.remove("is-invalid");
						}
						if (!mailValidate && password1 === password2) {
							document.getElementById("errormessage").textContent = "You have entered an invalid email address!";
							e.target.closest("form").email.closest("input").classList.add("is-invalid");
							e.target.closest("form").password.closest("input").classList.remove("is-invalid");
							e.target.closest("form").passwordReinserted.closest("input").classList.remove("is-invalid");
						}
						if (!mailValidate && password1 !== password2) {
							document.getElementById("errormessage").textContent = "You have entered an invalid email address and the password are different!";
							e.target.closest("form").email.closest("input").classList.add("is-invalid");
							e.target.closest("form").password.closest("input").classList.add("is-invalid");
							e.target.closest("form").passwordReinserted.closest("input").classList.add("is-invalid");
						}
					}
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


				makeCall("GET", "CheckGuest", null,
					function(req) {
						if (req.readyState == 4) {
							var message = req.responseText;
							switch (req.status) {
								case 200:
									window.location.href = "OnePage.html";
									break;
								case 401: // unauthorized
									document.getElementById("errormessage").textContent = message;
									break;
							}
						} else {
							document.getElementById("errormessage").textContent = "Error";
						}
					}
			);

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
		registrationForm.registerEvents(this);
		guestButton.registerEvents();
		newAccountButton.registerEvents(this);

	};


	this.refresh = function() {
		newAccountButton.reset();
		registrationForm.reset();

	};

	this.showRegisterForm = function() {
		registrationForm.show();
	}
}
}) ();
