(function() { // avoid variables ending up in the global scope

	// page components
	var missionDetails, missionsList, wizard,
		pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);


	// Constructors of view components



	function AlbumsList(_alert, _albumsRow, _albumsBody) {
		this.alert = _alert;
		this.albumsRow = _albumsRow;
		this.albumsBody = _albumsBody;

		this.reset = function() {
			this.albumsRow.style.visibility = "hidden";
		}

		this.show = function(next) {
			var that = this;
			makeCall("GET", "GetAlbumList", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var albumsToShow = JSON.parse(req.responseText);
							if (albumsToShow.length == 0) {
								that.alert.textContent = "No albums available!";
								return;
							}
							that.update(albumsToShow); // that visible by closure
							if (next) next(); // show the default element of the list if present
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};


		this.update = function(arrayAlbums) {
			var elem, i, row, imageCell, imageTag, titleCell, linkTitle, dateCell, dateBody, titleAnchor;
			this.albumsBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			arrayAlbums.forEach(function(album) { // self visible here, not this
				row = document.createElement("tr");
				imageCell = document.createElement("td");
				imageTag = document.createElement("img");
				imageTag.currentSrc = album.iconPath;
				imageTag.classList.add("img-thumbnail");
				titleCell = document.createElement("td");
				titleAnchor = document.createElement("a");
				linkTitle = document.createTextNode(album.title);
				titleAnchor.appendChild(linkTitle);

				titleAnchor.setAttribute('albumId', album.id);
				titleAnchor.addEventListener("click", (e) => {

					ImageList.show(e.target.getAttribute("albumId"));
				}, false);
				titleAnchor.href = "#";
				dateCell = document.createElement("td");
				dateBody = document.createElement("p");
				dateBody.textContent = album.creationDate;
				that.albumsBody.appendChild(row);

			});
			this.albumsRow.style.visibility = "visible";

		}



	}


	function ImageList(_alert, _galleryRow, _galleryBody) {
		this.alert = _alert;
		this.galleryRow = _galleryRow;
		this.galleryBody = _galleryBody;

		this.reset = function() {
			this.galleryRow.style.visibility = "hidden";
		}

		this.show = function(next) {
			var that = this;
			makeCall("GET", "GetImagesOfAlbum", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var imagesToShow = JSON.parse(req.responseText);
							if (imagesToShow.length == 0) {
								that.alert.textContent = "No images of this album available!";
								return;
							}
							that.update(imagesToShow, 0, 5); // that visible by closure
							if (next) next(); // show the default element of the list if present
						}
					} else {
						that.alert.textContent = message;
					}
				}
			);
		};


		this.update = function(arrayImages, currentBlock, numToShow) {
			var row, imageThumbnail, titleBody, imageAnchor, imageTag;
			this.albumsBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			row = document.createElement("tr");
			var i = currentBlock *numToShow;
			var end = i + numToShow;

			for (; i < end; i++) {

				var image = arrayImages[i];
				imageThumbnail = document.createElement("td");
				titleBody = document.createElement("p");
				titleBody.textContent = image.title;
				imageAnchor = document.createElement("a");
				imageTag = document.createElement("img");
				imageTag.currentSrc = image.filePath;
				imageTag.classList.add("image");
				imageTag.classList.add("img-thumbnail");
				imageAnchor.appendChild(imageTag);

				imageAnchor.setAttribute('imageId', image.id);
				imageAnchor.addEventListener("click", (e) => {

					imageDetails.show(e.target.getAttribute("imageId"));
				}, false);
				imageAnchor.href = "#";


				that.galleryBody.appendChild(imageThumbnail);
			}


			this.galleryRow.style.visibility = "visible";

		}



	}


	function NextButton() {
		this.registerEvents = function(orchestrator) {
			nextForm: document.getElementById("id_nextForm"),
			this.nextForm.querySelector("input[type='button'].next").addEventListener('click', (e) => {
				var form = e.target.closest("form");

			});

		}
	}
	function PreviousButton() {
		this.registerEvents = function(orchestrator) {
			previousForm: document.getElementById("id_previousForm"),
			this.previousForm.querySelector("input[type='button'].previous").addEventListener('click', (e) => {
				var form = e.target.closest("form");

			});

		}
	}


	function MissionDetails(options) {
		this.alert = options['alert'];
		this.detailcontainer = options['detailcontainer'];
		this.expensecontainer = options['expensecontainer'];
		this.expenseform = options['expenseform'];
		this.closeform = options['closeform'];
		this.date = options['date'];
		this.destination = options['destination'];
		this.status = options['status'];
		this.description = options['description'];
		this.country = options['country'];
		this.province = options['province'];
		this.city = options['city'];
		this.fund = options['fund'];
		this.food = options['food'];
		this.accomodation = options['accomodation'];
		this.travel = options['transportation'];

		this.registerEvents = function(orchestrator) {
			this.expenseform.querySelector("input[type='button']").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					var self = this,
						missionToReport = form.querySelector("input[type = 'hidden']").value;
					makeCall("POST", 'CreateExpensesReport', form,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								if (req.status == 200) {
									orchestrator.refresh(missionToReport);
								} else {
									self.alert.textContent = message;
								}
							}
						}
					);
				} else {
					form.reportValidity();
				}
			});

			this.closeform.querySelector("input[type='button']").addEventListener('click', (event) => {
				var self = this,
					form = event.target.closest("form"),
					missionToClose = form.querySelector("input[type = 'hidden']").value;
				makeCall("POST", 'CloseMission', form,
					function(req) {
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {
								orchestrator.refresh(missionToClose);
							} else {
								self.alert.textContent = message;
							}
						}
					}
				);
			});
		}


		this.show = function(missionid) {
			var self = this;
			makeCall("GET", "GetMissionDetailsData?missionid=" + missionid, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var mission = JSON.parse(req.responseText);
							self.update(mission); // self is the object on which the function
							// is applied
							self.detailcontainer.style.visibility = "visible";
							switch (mission.status) {
								case "OPEN":
									self.expensecontainer.style.visibility = "hidden";
									self.expenseform.style.visibility = "visible";
									self.expenseform.missionid.value = mission.id;
									self.closeform.style.visibility = "hidden";
									break;
								case "REPORTED":
									self.expensecontainer.style.visibility = "visible";
									self.expenseform.style.visibility = "hidden";
									self.closeform.missionid.value = mission.id;
									self.closeform.style.visibility = "visible";
									break;
								case "CLOSED":
									self.expensecontainer.style.visibility = "visible";
									self.expenseform.style.visibility = "hidden";
									self.closeform.style.visibility = "hidden";
									break;
							}
						} else {
							self.alert.textContent = message;

						}
					}
				}
			);
		};


		this.reset = function() {
			this.detailcontainer.style.visibility = "hidden";
			this.expensecontainer.style.visibility = "hidden";
			this.expenseform.style.visibility = "hidden";
			this.closeform.style.visibility = "hidden";
		}

		this.update = function(m) {
			this.date.textContent = m.startDate;
			this.destination.textContent = m.destination;
			this.status.textContent = m.status;
			this.description.textContent = m.description;
			this.country.textContent = m.country;
			this.province.textContent = m.province;
			this.city.textContent = m.city;
			this.fund.textContent = m.fund;
			this.food.textContent = m.expenses.food;
			this.accomodation.textContent = m.expenses.accomodation;
			this.travel.textContent = m.expenses.transportation;
		}
	}

	function Wizard(wizardId, alert) {
		// minimum date the user can choose, in this case now and in the future
		var now = new Date(),
			formattedDate = now.toISOString().substring(0, 10);
		this.wizard = wizardId;
		this.alert = alert;

		this.wizard.querySelector('input[type="date"]').setAttribute("min", formattedDate);

		this.registerEvents = function(orchestrator) {
			// Manage previous and next buttons
			Array.from(this.wizard.querySelectorAll("input[type='button'].next,  input[type='button'].prev")).forEach(b => {
				b.addEventListener("click", (e) => { // arrow function preserve the
					// visibility of this
					var eventfieldset = e.target.closest("fieldset"),
						valid = true;
					if (e.target.className == "next") {
						for (i = 0; i < eventfieldset.elements.length; i++) {
							if (!eventfieldset.elements[i].checkValidity()) {
								eventfieldset.elements[i].reportValidity();
								valid = false;
								break;
							}
						}
					}
					if (valid) {
						this.changeStep(e.target.parentNode, (e.target.className === "next") ? e.target.parentNode.nextElementSibling : e.target.parentNode.previousElementSibling);
					}
				}, false);
			});

			// Manage submit button
			this.wizard.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
				var eventfieldset = e.target.closest("fieldset"),
					valid = true;
				for (i = 0; i < eventfieldset.elements.length; i++) {
					if (!eventfieldset.elements[i].checkValidity()) {
						eventfieldset.elements[i].reportValidity();
						valid = false;
						break;
					}
				}

				if (valid) {
					var self = this;
					makeCall("POST", 'CreateMission', e.target.closest("form"),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) {
								var message = req.responseText; // error message or mission id
								if (req.status == 200) {
									orchestrator.refresh(message); // id of the new mission passed
								} else {
									self.alert.textContent = message;
									self.reset();
								}
							}
						}
					);
				}
			});
			// Manage cancel button
			this.wizard.querySelector("input[type='button'].cancel").addEventListener('click', (e) => {
				e.target.closest('form').reset();
				this.reset();
			});
		};

		this.reset = function() {
			var fieldsets = document.querySelectorAll("#" + this.wizard.id + " fieldset");
			fieldsets[0].hidden = false;
			fieldsets[1].hidden = true;
			fieldsets[2].hidden = true;

		}

		this.changeStep = function(origin, destination) {
			origin.hidden = true;
			destination.hidden = false;
		}
	}

	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");
		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
				document.getElementById("id_username"));
			personalMessage.show();

			albumsList = new AlbumsList(
				alertContainer,
				document.getElementById("id_albumsRow"),
				document.getElementById("id_albumsBody"));

			missionDetails = new MissionDetails({ // many parameters, wrap them in an
				// object
				alert: alertContainer,
				detailcontainer: document.getElementById("id_detailcontainer"),
				expensecontainer: document.getElementById("id_expensecontainer"),
				expenseform: document.getElementById("id_expenseform"),
				closeform: document.getElementById("id_closeform"),
				date: document.getElementById("id_date"),
				destination: document.getElementById("id_destination"),
				status: document.getElementById("id_status"),
				description: document.getElementById("id_description"),
				country: document.getElementById("id_country"),
				province: document.getElementById("id_province"),
				city: document.getElementById("id_city"),
				fund: document.getElementById("id_fund"),
				food: document.getElementById("id_food"),
				accomodation: document.getElementById("id_accomodation"),
				transportation: document.getElementById("id_transportation")
			});
			missionDetails.registerEvents(this);

			wizard = new Wizard(document.getElementById("id_createmissionform"), alertContainer);
			wizard.registerEvents(this);

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})
		};


		this.refresh = function(currentAlbum) {
			alertContainer.textContent = "";
			albumsList.reset();
			albumsList.show();
			missionDetails.reset();
			imageList.show(function() {
				imageList.autoclick(currentAlbum);
			}); // closure preserves visibility of this
			wizard.reset();
		};
	}
})();
