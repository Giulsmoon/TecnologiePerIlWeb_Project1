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
				if (form.checkValidity() && !sessionStorage.getItem('username')) {
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

		this.showErrorMessage = function(message) {
			errorDialog = document.getElementById("errormessageReg");
			errorDialog.textContent = message;
			errorDialog.classList.remove("invisible");
			errorDialog.classList.add("visible");

		}
		this.showRegistrationDone = function(message) {
			registrationDone = document.getElementById("id_registrationDone");
			registrationText = document.getElementById("id_registrationText");
			registrationText.textContent = message;
			registrationDone.classList.remove("d-none");
			registrationDone.classList.add("d-block");
		}
		this.resetRegistrationDone = function() {
			registrationDone = document.getElementById("id_registrationDone");
			registrationDone.classList.remove("d-block");
			registrationDone.classList.add("d-none");
			registrationText = document.getElementById("id_registrationText");
			registrationText.textContent = "";

		}
		this.resetErrorMessage = function() {
			errorDialog = document.getElementById("errormessageReg");
			errorDialog.textContent = "";
			errorDialog.classList.remove("visible");
			errorDialog.classList.add("invisible");
		}
		this.registerEvents = function(orchestrator) {
			document.getElementById("id_registrationButton").addEventListener('click', (e) => {
				that=this;
				e.preventDefault();
				var form = e.target.closest("form");
				if (form.checkValidity() && !sessionStorage.getItem('username')) {
					var username = form.username.value;
					var usernameValidate = username.match(/^[0-9a-zA-Z]+$/);
					var email = form.email.value;
					var password1 = form.password.value;
					var password1Validate = password1.match(/^[0-9a-zA-Z]+$/);
					var password2 = form.passwordReinserted.value;
					var password2Validate = password1.match(/^[0-9a-zA-Z]+$/);

					var mailValidate = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email);
					console.log(mailValidate);

					if (password1 === password2 && mailValidate && usernameValidate
						&& password1Validate && password2Validate) {
						form.username.closest("input").classList.remove("is-invalid");
						form.email.closest("input").classList.remove("is-invalid");
						form.password.closest("input").classList.remove("is-invalid");
						form.passwordReinserted.closest("input").classList.remove("is-invalid");
						makeCall("POST", 'Registration', form,
							function(req) {

								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText;
									switch (req.status) {
										case 200:
											orchestrator.refresh();
											that.showRegistrationDone("Registration Done");
											break;
										case 400: // bad request
											that.showErrorMessage(message);
											break;
										case 401: // unauthorized
											that.showErrorMessage(message);
											break;
										case 500: // server error
											that.showErrorMessage(message);
											break;
									}
								}
							}
						);


					}

					else {
						if (!usernameValidate || !password1Validate || !password2Validate) {
							this.showErrorMessage("username or password must be only number and letter value");
							form.password.closest("input").classList.add("is-invalid");
							form.passwordReinserted.closest("input").classList.add("is-invalid");
							form.username.closest("input").classList.add("is-invalid");

						}
						else {
							form.password.closest("input").classList.remove("is-invalid");
							form.passwordReinserted.closest("input").classList.remove("is-invalid");
							form.username.closest("input").classList.remove("is-invalid");

							if (mailValidate && password1 !== password2) {
								this.showErrorMessage("password are different");
								form.password.closest("input").classList.add("is-invalid");
								form.passwordReinserted.closest("input").classList.add("is-invalid");
								form.email.closest("input").classList.remove("is-invalid");
							}
							if (!mailValidate && password1 === password2) {
								this.showErrorMessage("You have entered an invalid email address!");
								form.email.closest("input").classList.add("is-invalid");
								form.password.closest("input").classList.remove("is-invalid");
								form.passwordReinserted.closest("input").classList.remove("is-invalid");
							}
							if (!mailValidate && password1 !== password2) {
								this.showErrorMessage("You have entered an invalid email address and the password are different!");
								form.email.closest("input").classList.add("is-invalid");
								form.password.closest("input").classList.add("is-invalid");
								form.passwordReinserted.closest("input").classList.add("is-invalid");
							}
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

				if (sessionStorage.getItem('username') === null) {
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
							}
						}
					);
				}
			});

		}
	};


	function PageOrchestrator() {
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
			registrationForm.resetErrorMessage();
			registrationForm.resetRegistrationDone();
			newAccountButton.reset();
			registrationForm.reset();

		};

		this.showRegisterForm = function() {
			registrationForm.resetErrorMessage();
			registrationForm.show();
		}
	}
})();
