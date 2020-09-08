(function() { // avoid variables ending up in the global scope

	// page components
	var albumsList, imageList, imageDetails, nextButton, previousButton,
		commentForm, redirectToIndex, closeModalWindow, saveOrderButton, alertModal,
		logoutButton, loginButton;
	pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		pageOrchestrator.start(); // initialize the components
		pageOrchestrator.refresh();
	}, false);

	//funzione che gestisce la lista di album, la makeCall per ottenere dal server la lista degli album, e aggiunge
	//un eventListener all' evento click per selezionare un album, e gestisce gli eventi per il drag&drop
	function AlbumsList(orchestrator, _albumsRow, _albumsBody) {

		this.albumsRow = _albumsRow;
		this.albumsBody = _albumsBody;

		this.reset = function() {
			this.albumsRow.classList.remove("d-block");
			this.albumsRow.classList.add("d-none")
		}

		this.show = function() {
			var that = this;
			makeCall("GET", "GetAlbumList", null,
				function(req) {

					if (req.readyState == 4) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								var albumsToShow = JSON.parse(req.responseText);
								if (albumsToShow.length > 0) {
									that.update(albumsToShow); // that visible by closure
								} else {
									var text = "No albums available!";
									orchestrator.showAlert(text);
									return;
								}

								break;
							case 500: // SC_INTERNAL_SERVER_ERROR
								orchestrator.showAlert(message);
								break;
						}


					}

				}
			);
		};

		this.setDraggable = function() {
			if (sessionStorage.getItem('username')) {

				const draggables = document.querySelectorAll('.draggable');

				draggables.forEach(function(draggable) {
					draggable.style.userSelect = 'none';

					draggable.addEventListener('dragstart', (e) => {

						draggable.classList.add('dragging');
					}, false)

					draggable.addEventListener('dragend', (e) => {

						draggable.classList.remove('dragging');
						//non appena concludo il primo drag si attiva il bottone per salvare la preferenza dell'utente
						orchestrator.showSaveButton();
					}, false)
				});

				this.albumsBody.addEventListener('dragover', (e) => {
					e.preventDefault();
					const afterElement = getDragAfterElement(this.albumsBody, e.clientY);
					const draggable = document.querySelector('.dragging')

					if (afterElement == null) {
						this.albumsBody.appendChild(draggable)
					}
					else {
						this.albumsBody.insertBefore(draggable, afterElement)
					}

				}, false)

				function getDragAfterElement(container, y) {
					const draggableElements = [...container.querySelectorAll('.draggable:not(.dragging)')];

					return draggableElements.reduce((closest, child) => {
						const box = child.getBoundingClientRect();
						const offset = y - box.top - box.height / 2;

						if (offset < 0 && offset > closest.offset) {
							return { offset: offset, element: child }
						} else {
							return closest;
						}

					}, { offset: Number.NEGATIVE_INFINITY }).element;
				}

			}
		}



		this.update = function(arrayAlbums) {
			var elem, i, row, imageCell, imageTag, titleCell, linkTitle, dateCell, dateBody, titleAnchor;
			this.albumsBody.innerHTML = ""; // empty the table body
			var that = this;
			// build updated list
			arrayAlbums.forEach(function(album) {

				row = document.createElement("tr");
				row.classList.add("draggable");
				row.setAttribute('draggable', true);

				imageCell = document.createElement("td");
				imageTag = document.createElement("img");
				imageTag.setAttribute('src', album.iconPath);
				imageTag.classList.add("img-thumbnail");
				imageCell.appendChild(imageTag);
				row.appendChild(imageCell);

				titleCell = document.createElement("td");
				titleAnchor = document.createElement("a");
				linkTitle = document.createTextNode(album.title);
				titleAnchor.appendChild(linkTitle);
				titleCell.appendChild(titleAnchor);
				row.appendChild(titleCell);

				titleAnchor.setAttribute('albumId', album.id);
				titleAnchor.addEventListener("click", (e) => {
					e.preventDefault();
					orchestrator.afterClickAlbum(e.target.getAttribute("albumId"));
				}, false);
				titleAnchor.href = "#";

				dateCell = document.createElement("td");
				dateBody = document.createElement("p");
				dateBody = document.createTextNode(album.date);
				dateCell.appendChild(dateBody);
				row.appendChild(dateCell);

				that.albumsBody.appendChild(row);

			});
			this.albumsRow.classList.remove("d-none");
			this.albumsRow.classList.add("d-block");


			this.setDraggable();
		}



	}
	//funzione che gestisce la lista delle immagini, con la makeCall ottiene dal server la lista di immagini di un dato album
	//implemente metodi per spostare in avanti e indietro le immagini a gruppi di 5
	function ImageList(orchestrator, _galleryRow, _galleryBody, _previousButton, _nextButton) {
		this.galleryRow = _galleryRow;
		this.galleryBody = _galleryBody;
		this.previousButton = _previousButton;
		this.nextButton = _nextButton;
		this.images = null;
		this.currentBlock = 0;
		this.numToShow = 5;
		this.reset = function() {
			this.galleryRow.classList.remove("visible");
			this.galleryRow.classList.add("invisible");
		};
		this.resetImages = function() {
			this.images = null;
			this.currentBlock = 0;
			this.numToShow = 5;
		};
		this.resetButtonsNextAndPrevious = function() {
			this.previousButton.classList.remove("visible");
			this.previousButton.classList.add("invisible");
			this.nextButton.classList.remove("visible");
			this.nextButton.classList.add("invisible");
		};
		this.show = function(albumId) {
			var that = this;
			makeCall("GET", "GetImagesOfAlbum?albumId=" + albumId, null, function(req) {
				if (req.readyState == 4) {
					var message = req.responseText;
					switch (req.status) {
						case 200:
							var imagesToShow = JSON.parse(req.responseText);
							if (imagesToShow.length > 0) {
								that.images = imagesToShow;
								that.update(); // that visible by closure
							}
							else {
								var text = "No images of this album available!";
								orchestrator.showAlert(text);
							}
							break;
						case 400: // SC_BAD_REQUEST
							orchestrator.showAlert(message);
							break;
						case 500: // SC_INTERNAL_SERVER_ERROR
							orchestrator.showAlert(message);
							break;
					}
				}
			});
		};
		this.findImageById = function(imageId) {
			var that = this;
			var i = 0;
			for (i; i < that.images.length; i++) {
				if (that.images[i].id == imageId) {
					return that.images[i];
				}
			}
		};
		this.update = function() {
			var timerDisplay;
			var row, imageThumbnail, titleBody, imageAnchor, imageTag;
			this.galleryBody.innerHTML = ""; // empty the table body
			// build updated list
			var that = this;
			row = document.createElement("tr");
			var i = that.currentBlock * that.numToShow;
			var end = i + that.numToShow;
			if (end > that.images.length) {
				end = that.images.length;
			}
			if (end < that.images.length && that.currentBlock > 0) {
				that.nextButton.classList.remove("invisible");
				that.nextButton.classList.add("visible");
				that.previousButton.classList.remove("invisible");
				that.previousButton.classList.add("visible");
			}
			else {
				if (end >= that.images.length && that.currentBlock > 0) {
					that.nextButton.classList.remove("visible");
					that.nextButton.classList.add("invisible");
					that.previousButton.classList.remove("invisible");
					that.previousButton.classList.add("visible");
				}
				if (end < that.images.length && that.currentBlock === 0) {
					that.nextButton.classList.remove("invisible");
					that.nextButton.classList.add("visible");
					that.previousButton.classList.remove("visible");
					that.previousButton.classList.add("invisible");
				}
			}
			for (; i < end; i++) {
				var image = that.images[i];
				imageThumbnail = document.createElement("td");
				titleBody = document.createElement("p");
				titleBody.textContent = image.title;
				imageThumbnail.appendChild(titleBody);
				imageAnchor = document.createElement("a");
				imageTag = document.createElement("img");
				imageTag.setAttribute('src', image.filePath);
				imageTag.classList.add("image");
				imageTag.classList.add("img-thumbnail");
				imageAnchor.appendChild(imageTag);
				imageThumbnail.appendChild(imageAnchor);
				row.appendChild(imageThumbnail);
				imageAnchor.setAttribute('imageId', image.id);
				imageAnchor.addEventListener("mouseenter", (e) => {
					e.preventDefault();
					obj = e.currentTarget;
					timerDisplay = setTimeout(function() {
						var imageEntered = that.findImageById(obj.getAttribute("imageId"));
						orchestrator.showImageDetails(obj.getAttribute("imageId"), imageEntered);
					}, 500);
				}, false);
				imageAnchor.addEventListener("mouseleave", (e) => {
					e.preventDefault();
					clearTimeout(timerDisplay);
				}, false);
				imageAnchor.addEventListener("click", (e) => {
					e.preventDefault();
				}, false);


				imageAnchor.href = "#";
			}
			that.galleryBody.appendChild(row);
			this.galleryRow.classList.remove("invisible");
			this.galleryRow.classList.add("visible");
		};
		this.next = function() {
			if (this.images) {
				if (((this.currentBlock * this.numToShow) + this.numToShow) < this.images.length) {
					this.currentBlock++;
					this.update();
				}
			}
		};
		this.previous = function() {
			if (this.images && this.currentBlock > 0) {
				this.currentBlock--;
				this.update();
			}
		};
	}

	//funzione per gestire la logica dei parametri per spostare le immagini in avanti
	function NextButton() {
		this.registerEvents = function(orchestrator) {
			document.getElementById("id_nextButton").addEventListener('click', (e) => {
				e.preventDefault();
				orchestrator.moveImagesNext();

			}, false);
		}
	}

	//funzione per gestire la logica dei parametri per spostare le immagini in indietro
	function PreviousButton() {
		this.registerEvents = function(orchestrator) {
			document.getElementById("id_previousButton").addEventListener('click', (e) => {
				e.preventDefault();
				orchestrator.moveImagesPrevious();

			}, false);

		}
	}


	//funzione che gestisce la visualizzazione in una finestra modale della singola immagine e dei suoi commenti e informazioni
	//annesse. Con la makeCall ottiene la lista di commenti associati all'immagine.
	//Gestisce anche la visualizzazione dell'ultimo commento inserito
	function ImageDetails(orchestrator, _titleRow, _titleBody, _descriptionBody, _commentRow,
		_commentBody, _imageAndCommentsRow, _imageContainer) {

		this.titleRow = _titleRow;
		this.titleBody = _titleBody;
		this.descriptionBody = _descriptionBody;
		this.commentRow = _commentRow;
		this.commentBody = _commentBody;
		this.imageAndCommentsRow = _imageAndCommentsRow;
		this.imageContainer = _imageContainer;


		this.reset = function() {

			this.imageContainer.classList.remove("visible");
			this.imageContainer.classList.add("invisible");
			this.commentBody.innerHTML = "";
			this.commentRow.classList.remove("visible");
			this.commentRow.classList.add("invisible");
			this.descriptionBody.innerHTML = "";
			this.titleBody.innerHTML = "";


		}


		this.show = function(imageId, imageSelected) {
			var that = this;
			makeCall("GET", "GetCommentsOfImage?imageId=" + imageId, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								var comments = JSON.parse(req.responseText);
								that.update(imageSelected, comments); // that visible by closure
								break;
							case 400: // SC_BAD_REQUEST
								orchestrator.showAlert(message);
								break;
							case 500: // SC_INTERNAL_SERVER_ERROR
								orchestrator.showAlert(message);
								break;
						}


					}
				}

			);
		};

		this.addNewComment = function(comment) {


			var commentRow, commentCell1, commentUsername, commentCell2, commentP1,
				commentText, commentMedia, commentCell3, commentDate;
			var that = this;

			commentRow = document.createElement("div");
			commentRow.className = "comments-list";
			commentMedia = document.createElement("div");
			commentMedia.className = "media";


			//comments of the image selected
			commentCell1 = document.createElement("div");
			commentP1 = document.createElement("p");
			commentP1.className = "media-heading user_name";
			commentUsername = document.createTextNode(comment.username);
			commentP1.appendChild(commentUsername);
			commentCell1.appendChild(commentP1);
			commentMedia.appendChild(commentCell1);

			commentCell2 = document.createElement("div");
			commentCell2.className = "media-body";
			commentText = document.createTextNode(comment.text);
			commentCell2.appendChild(commentText);
			commentMedia.appendChild(commentCell2);

			commentCell3 = document.createElement("div");
			commentCell3.className = "pull-right";
			commentDate = document.createTextNode(comment.date);
			commentCell3.appendChild(commentDate);
			commentMedia.appendChild(commentCell3);


			commentRow.appendChild(commentMedia);
			that.commentBody.appendChild(commentRow);
		}

		this.showComments = function() {

			var that = this;
			if (that.commentRow.classList.contains("invisible")) {
				that.commentRow.classList.remove("invisible");
				that.commentRow.classList.add("visible");
			}
		}

		this.update = function(imageSelected, commentsOfImage) {
			var row1, imageP, dateP, descriptionP, imageCell, imageDateCell, imageTag,
				imageDescription;

			this.titleBody.innerHTML = "";
			this.commentBody.innerHTML = "";
			this.descriptionBody.innerHTML = "";
			// build updated list
			var that = this;
			var image = imageSelected;
			var comments = commentsOfImage;


			//image info : title + date
			imageP = document.createElement("p");
			imageP.className = "h1 pt-3 border-top";
			imageCell = document.createTextNode(image.title);
			imageP.appendChild(imageCell);
			that.titleBody.appendChild(imageP);
			dateP = document.createElement("p");
			dateP.className = "h6";
			imageDateCell = document.createTextNode(image.date);
			dateP.appendChild(imageDateCell);
			that.titleBody.appendChild(dateP);

			//image scr + description
			imageTag = document.createElement("img");
			imageTag.setAttribute('src', image.filePath);
			imageTag.className = "shadow rounded-lg mb-4 image img-fluid";
			that.descriptionBody.appendChild(imageTag);
			descriptionP = document.createElement("p");
			if (image.description) {
				descriptionP.className = "shadow border border-success rounded-pill p-2 text-break visible";
				imageDescription = document.createTextNode(image.description);
				descriptionP.appendChild(imageDescription);
			} else {
				descriptionP.classList.remove("visible");
				descriptionP.classList.add("invisible");
			}

			that.descriptionBody.appendChild(descriptionP);


			var i = 0;
			for (i; i < comments.length; i++) {
				that.addNewComment(comments[i]);
			}

			//passo l'image id come parametro alla funzione show del comment form.
			orchestrator.showCommentForm(image.id);



			this.imageContainer.classList.remove("invisible");
			this.imageContainer.classList.add("visible");
			if (comments.length > 0) {
				that.commentRow.classList.remove("invisible");
				that.commentRow.classList.add("visible");
			} else {
				this.commentRow.classList.remove("visible");
				this.commentRow.classList.add("invisible");

			}

			document.getElementById("box").style.display = 'block';
			document.getElementById("box").scrollTop = 0;
			document.getElementById("id_scrollbarComment").scrollTop = 0;

		}



	}

	//gestisce la finestra modale degli alert e dei messaggi da mostrare a video all' utente
	function AlertModal(_alert, _textAlert, _spanAlert, _loginButton, _logoutButton) {

		this.alert = _alert;
		this.textAlert = _textAlert //dove inserire il messaggio
		this.span = _spanAlert;
		this.loginButton = _loginButton;
		this.logoutButton = _logoutButton;
		this.continueButton = document.getElementById("id_continueBtn");


		this.show = function(message) {
			this.reset();
			this.update(message);
			this.alert.style.display = "block";


		}

		this.update = function(message) {
			text = document.createTextNode(message);
			this.textAlert.appendChild(text);

		}

		this.reset = function() {
			this.resetContinueButton();
			this.loginButton.reset();
			this.logoutButton.reset();
			this.textAlert.innerHTML = "";
		}


		this.registerEvents = function() {
			var that = this;
			//chiude la finestra cliccando la x
			that.span.addEventListener('click', (e) => {
				e.preventDefault();
				that.alert.style.display = "none";
			}, false);

		}

		this.showLogin = function(message) {
			this.show(message);

			this.loginButton.show();
			footer = document.getElementById("id_modalFooter");

			footer.classList.remove("d-none");
			footer.classList.add("d-block");

		}
		this.showLogout = function(message) {
			this.show(message);

			this.logoutButton.show();
			footer = document.getElementById("id_modalFooter");

			footer.classList.remove("d-none");
			footer.classList.add("d-block");

		}

		this.showContinue = function(message) {
			this.show(message);
			this.showContinueButton();
			footer = document.getElementById("id_modalFooter");

			footer.classList.remove("d-none");
			footer.classList.add("d-block");
			this.continueButton.addEventListener('click', (e) => {
				e.preventDefault();
				window.location.href = "index.html";
			});
		}

		this.showContinueButton = function() {
			this.continueButton.classList.remove("d-none");
			this.continueButton.classList.add("d-block");
		}
		this.resetContinueButton = function() {
			this.continueButton.classList.remove("d-block");
			this.continueButton.classList.add("d-none");
		}
	}

	//funzione che chiude la finestra modale dell'immagine
	function CloseModalWindow() {
		var timerClose;
		var modal_close = document.getElementById("imageWindowClose");
		var modal_area = document.getElementById("id_imageContainer");
		var box = document.getElementById('box');
		this.registerEvents = function() {
			//chiude la finestra cliccando la x
			modal_close.addEventListener('click', (e) => {
				e.preventDefault();
				clearTimeout(timerClose);
				box.style.display = "none";
			}, false);

			// Chiude la finestra quando l'utente clicca al di fuori di essa
			modal_area.addEventListener('mouseleave', (e) => {
				e.preventDefault();
				//l'if evita che la finestra modale delle immagini si chiuda quando compare la finestra modale degli alert
				if (document.getElementById("id_alertUser").style.display !== "block") {
					timerClose = setTimeout(function() {
						box.style.display = "none"
					}, 500);
				}

			}, false);

			modal_area.addEventListener('mouseenter', (e) => {
				e.preventDefault();
				clearTimeout(timerClose);
			}, false);
		}



	}

	//funzione che gestire la redirect to login se l'utente non è loggato quando vuole inserire un commento
	function RedirectToIndex() {

		this.registerEvents = function(orchestrator) {

			document.getElementById("id_loginToComment").addEventListener('click', (e) => {
				e.preventDefault();
				if (!sessionStorage.getItem('username')) {
					makeCall("GET", "GoLogin", null,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								switch (req.status) {
									case 200:

										window.location.href = "index.html";
										break;
									case 401: // unauthorized
										orchestrator.showGuestSloggedAlert(message);
										break;
								}
							}
						}

					);
				} else {
					orchestrator.showGuestLoggedAlert("Permission Denied!: You are logged client side");

				}
			}, false);
		};

	}

	//funzione che gestisce l'inserimento di un commento. Implemente una makeCall per inviare la form con i dati del commento
	//da inserire nel database attraverso una post
	function CommentForm(_commentRow, _userNotLogged, _userLogged) {

		this.commentRow = _commentRow;
		this.userNotLogged = _userNotLogged;
		this.userLogged = _userLogged;

		this.reset = function() {
			document.getElementById("id_textComment").value = "";
			this.commentRow.classList.remove("visible");
			this.commentRow.classList.add("invisible");
			this.userNotLogged.classList.remove("d-none");
			this.userNotLogged.classList.add("d-block");
			this.userLogged.classList.remove("d-block");
			this.userLogged.classList.add("d-none");


		};

		this.show = function(imageId) {
			this.commentRow.classList.remove("invisible");
			this.commentRow.classList.add("visible");
			if (sessionStorage.getItem('username') === null) {
				this.userNotLogged.classList.remove("d-none")
				this.userNotLogged.classList.add("d-block")
				this.userLogged.classList.remove("d-block")
				this.userLogged.classList.add("d-none")
			} else {
				this.userNotLogged.classList.remove("d-block")
				this.userNotLogged.classList.add("d-none")
				this.userLogged.classList.remove("d-none")
				this.userLogged.classList.add("d-block")
				document.getElementById("id_commentImageId").value = imageId;
			}


		};

		this.registerEvents = function(orchestrator) {
			var that = this;
			document.getElementById("id_commentButton").addEventListener('click', (e) => {
				e.preventDefault();
				if (sessionStorage.getItem('username')) {
					var form = e.target.closest("form");
					if (form.checkValidity()) {
						makeCall("POST", 'CreateComment', e.target.closest("form"),
							function(req) {
								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText;

									switch (req.status) {
										case 200:
											var comment = JSON.parse(req.responseText);
											orchestrator.addNewComment(comment);
											var text = "Comment saved";
											orchestrator.showAlert(text);
											break;
										case 400: // bad request
											orchestrator.showAlert(message);
											break;
										case 401: // unauthorized
											orchestrator.showLogLoggedAlert(message);
											break;
										case 500: // server error
											orchestrator.showAlert(message);
											break;
									}
								}
							}
						);
					} else {
						form.reportValidity();
					}
				} else {
					orchestrator.showLogNotLoggedAlert("You are not logged");

				}
			}, false);
		}

	};

	//Funzione che implementa la makeCall per inviare con una post i dati dell'ordine degli album da inserire nel database
	//come preferenza dell'utente
	function SaveOrderButton(_saveButton) {
		this.saveButton = _saveButton;


		this.registerEvents = function(orchestrator) {
			this.saveButton.addEventListener('click', (e) => {
				e.preventDefault();
				this.saveOrder(orchestrator);
			}, false);
		}

		this.show = function() {
			this.saveButton.classList.remove("invisible")
			this.saveButton.classList.add("visible")
		}

		this.reset = function() {
			this.saveButton.classList.remove("visible")
			this.saveButton.classList.add("invisible")
		}

		this.saveOrder = function(orchestrator) {
			var that = this;

			var _username = sessionStorage.getItem('username');
			if (_username) {
				var _arrayPosition = [];

				arrayPosition = _arrayPosition;
				tableRows = document.querySelectorAll("a[albumId]");

				tableRows.forEach(function(row) {
					arrayPosition.push(row.getAttribute("albumId"))
				});
				var obj = arrayPosition;
				makeCallSendObj("POST", 'SaveAlbumOrder', obj,
					function(req) {
						if (req.readyState == XMLHttpRequest.DONE) {
							var message = req.responseText;

							switch (req.status) {
								case 200:
									var text = "Order saved";
									orchestrator.showAlert(text);
									that.reset();
									break;
								case 400: // bad request
									orchestrator.showAlert(message);
									break;
								case 401: // unauthorized
									orchestrator.showLogLoggedAlert(message);
									break;
								case 500: // server error
									orchestrator.showAlert(message);
									break;
							}
						}
					}

				);
			} else {
				orchestrator.showLogNotLoggedAlert("You are not logged");

			}
		}
	}
	//funzione che gestisce il bottone di Logout e invia una get per sloggare al server
	function LogoutButton(_logoutBtn) {
		this.logoutBtn = _logoutBtn;

		this.show = function() {
			this.logoutBtn.classList.remove("d-none")
			this.logoutBtn.classList.add("d-block")
		}

		this.reset = function() {
			this.logoutBtn.classList.remove("d-block")
			this.logoutBtn.classList.add("d-none")
		}

		this.registerEvents = function(orchestrator) {
			this.logoutBtn.addEventListener('click', (e) => {
				e.preventDefault();
				if (sessionStorage.getItem('username')) {
					makeCall("GET", "Logout", null,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								switch (req.status) {
									case 200:
										window.sessionStorage.removeItem('username');
										window.location.href = "index.html";
										break;
									case 401: // unauthorized
										orchestrator.showContinueLogoutClientAlert("Errore: eri loggato sul client ma non sul server. Sei stato sloggato sul client, continua");
										break;
								}
							}
						}

					);
				} else {
					orchestrator.showLogNotLoggedAlert("You are not logged in client side");

				}
			}, false)
		}
	}
	//funzione che gestisce il bottone di Login e invia una get per chiedere al server di reindirizzarti alla login page
	function LoginButton(_goLoginBtn) {
		this.goLoginBtn = _goLoginBtn;

		this.show = function() {
			this.goLoginBtn.classList.remove("d-none")
			this.goLoginBtn.classList.add("d-block")
		}

		this.reset = function() {
			this.goLoginBtn.classList.remove("d-block")
			this.goLoginBtn.classList.add("d-none")
		}

		this.registerEvents = function(orchestrator) {
			this.goLoginBtn.addEventListener('click', (e) => {
				e.preventDefault();
				if (!sessionStorage.getItem('username')) {
					makeCall("GET", "GoLogin", null,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								switch (req.status) {
									case 200:
										window.location.href = "index.html";
										break;
									case 401: // unauthorized
										orchestrator.showContinueLoginClientAlert("Errore: eri loggato sul server e non sul client, quindi sei stato sloggato sul server, continua");
										break;
								}
							}
						}

					);
				}
				else {
					orchestrator.showGuestLoggedAlert("Permission Denied!: You are logged client side");

				}
			}, false)
		}
	}

	//funzione principale che gestisce la creazione di tutti i componenti della pagina e di registrare gli eventi ad essi
	//associati, e che gestisce i vari riferimenti tra i vari componenti (in modo tale che i componenti non debbano 
	//interagire direttamente tra di loro)
	function PageOrchestrator() {

		this.start = function() {

			albumsList = new AlbumsList(
				this,
				document.getElementById("id_albumsRow"),
				document.getElementById("id_albumsBody"));

			imageList = new ImageList(
				this,
				document.getElementById("id_galleryRow"),
				document.getElementById("id_galleryBody"),
				document.getElementById("id_previousButton"),
				document.getElementById("id_nextButton"));

			imageDetails = new ImageDetails(
				this,
				document.getElementById("id_titleRow"),
				document.getElementById("id_titleBody"),
				document.getElementById("id_descriptionBody"),
				document.getElementById("id_commentRow"),
				document.getElementById("id_commentBody"),
				document.getElementById("id_imageAndCommentsRow"),
				document.getElementById("id_imageContainer"));

			commentForm = new CommentForm(
				document.getElementById("id_commentForm"),
				document.getElementById("id_userNotLogged"),
				document.getElementById("id_userLogged"));

			redirectToIndex = new RedirectToIndex();

			closeModalWindow = new CloseModalWindow();

			loginButton2 = new LoginButton(
				document.getElementById("id_alertLogintBtn"));

			logoutButton2 = new LogoutButton(
				document.getElementById("id_alertLogoutBtn"));

			alertModal = new AlertModal(
				document.getElementById("id_alertUser"),
				document.getElementById("id_textAlert"),
				document.getElementById("id_closeAlert"),
				loginButton2,
				logoutButton2);

			saveOrderButton = new SaveOrderButton(
				document.getElementById("id_saveButton"));

			nextButton = new NextButton();

			previousButton = new PreviousButton();

			logoutButton = new LogoutButton(
				document.querySelector("a[href='Logout']"));

			loginButton = new LoginButton(
				document.querySelector("a[href='GoLogin']"));


			nextButton.registerEvents(this);
			previousButton.registerEvents(this);
			commentForm.registerEvents(this);
			redirectToIndex.registerEvents(this);
			closeModalWindow.registerEvents();
			saveOrderButton.registerEvents(this);
			alertModal.registerEvents();
			logoutButton.registerEvents(this);
			loginButton.registerEvents(this);
			logoutButton2.registerEvents(this);
			loginButton2.registerEvents(this);


		};



		this.refresh = function() {

			albumsList.reset();
			imageList.reset();
			albumsList.show(this);

			if (sessionStorage.getItem('username')) {
				logoutButton.show();
				loginButton.reset();
			}
			else {
				logoutButton.reset();
				loginButton.show();
			}
		};

		this.afterClickAlbum = function(albumId) {
			imageList.resetButtonsNextAndPrevious();
			imageList.resetImages();
			imageList.reset();
			imageList.show(albumId);

		};
		this.addNewComment = function(comment) {
			imageDetails.addNewComment(comment);
			imageDetails.showComments();
		};

		this.showImageDetails = function(imageId, imageSelected) {
			imageDetails.reset();
			imageDetails.show(imageId, imageSelected);
		};

		this.showCommentForm = function(imageId) {
			commentForm.reset();
			commentForm.show(imageId);
		};

		this.resetImageContainer = function() {

			imageDetails.reset();
		};

		this.moveImagesNext = function() {

			imageList.next();
		};


		this.moveImagesPrevious = function() {

			imageList.previous();
		};

		this.showSaveButton = function() {
			saveOrderButton.show();
		}

		this.showAlert = function(message) {
			alertModal.show(message);

		};

		this.showGuestSloggedAlert = function(message) {
			alertModal.showLogin(message);

		};

		this.showGuestLoggedAlert = function(message) {
			alertModal.showLogout(message);
		};

		this.showContinueLogoutClientAlert = function(message) {
			sessionStorage.clear();
			alertModal.showContinue(message);
		};

		this.showLogNotLoggedAlert = function(message) {
			alertModal.showLogin(message);
		};
		this.showLogLoggedAlert = function(message) {
			alertModal.showLogout(message);
		};
		this.showContinueLoginClientAlert = function(message) {
			alertModal.showContinue(message);
		};
	}
})();
